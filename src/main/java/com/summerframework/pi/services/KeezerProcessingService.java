package com.summerframework.pi.services;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.summerframework.pi.jpa.model.KeezerConfig;
import com.summerframework.pi.jpa.model.KeezerConfigRepository;
import com.summerframework.pi.jpa.model.TemperatureLog;
import com.summerframework.pi.jpa.model.TemperatureLogRepository;
import com.summerframework.pi.pi4j.RelayManager;
import com.summerframework.pi.pi4j.TemperatureReader;
import com.summerframework.pi.websockets.ChartResponse;

/**
 * Freezer on Relay #1
 * 
 * Heater on Relay #2
 * 
 * Performs action based on current keg temperature.
 * 
 * Logs temperatures to database for monitoring.
 *
 */
@Service
public class KeezerProcessingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(KeezerProcessingService.class);

	@Autowired
	ErrorHandler errorHandler;

	@Autowired
	private KeezerConfigRepository keezerConfigRepository;

	@Autowired
	private TemperatureLogRepository temperatureLogRepository;

	@Autowired
	private TemperatureReader temperatureReader;

	@Autowired
	private RelayManager relayManager;
	
	@Autowired
	private SimpMessageSendingOperations messagingTemplate;
	
	@Autowired
	private TemperatureChartHelper temperatureChartHelper;

	public void initTurnAllRelaysOff() {
		LOGGER.debug("Turning all relays off. (Assumption on the start of program).");
		relayManager.turnRelayStateForAllRelays(1, false);
		sleep(1000);
	}
	
	public void processKeezerAction(String executionId) {
		LOGGER.debug("In KeezerProcessingService.");
		
		// Retrieve config
		KeezerConfig keezerConfig1 = keezerConfigRepository.findOne(1l);
		KeezerConfig keezerConfig2 = keezerConfigRepository.findOne(2l);
		KeezerConfig keezerConfig3 = keezerConfigRepository.findOne(3l);
		KeezerConfig keezerConfig4 = keezerConfigRepository.findOne(4l);

		long timestampConfig1 = keezerConfig1.getStateChangeDatetime();
		long timestampConfig2 = keezerConfig2.getStateChangeDatetime();
		long timestampConfig3 = keezerConfig3.getStateChangeDatetime();
		long timestampConfig4 = keezerConfig4.getStateChangeDatetime();

		BigDecimal tempB1 = keezerConfig1.getThreshold1HeatStartFreezerStopTemperature();
		BigDecimal tempB2 = keezerConfig1.getThreshold2FreezerStopTemperature();
		BigDecimal tempB3 = keezerConfig1.getThreshold3HeatStopTemperature();
		BigDecimal tempB4 = keezerConfig1.getThreshold4FreezerStartHeatStopTemperature();

		BigDecimal tempC1 = keezerConfig2.getThreshold1HeatStartFreezerStopTemperature();
		BigDecimal tempC2 = keezerConfig2.getThreshold2FreezerStopTemperature();
		BigDecimal tempC3 = keezerConfig2.getThreshold3HeatStopTemperature();
		BigDecimal tempC4 = keezerConfig2.getThreshold4FreezerStartHeatStopTemperature();
		

		BigDecimal tempD1 = keezerConfig3.getThreshold1HeatStartFreezerStopTemperature();
		BigDecimal tempD2 = keezerConfig3.getThreshold2FreezerStopTemperature();
		BigDecimal tempD3 = keezerConfig3.getThreshold3HeatStopTemperature();
		BigDecimal tempD4 = keezerConfig3.getThreshold4FreezerStartHeatStopTemperature();

		// Validate config is ok.
		// NOTE: This logic is duplicated in WebController's update Keezer config method...
		if (timestampConfig1 < timestampConfig2 && timestampConfig2 < timestampConfig3 && timestampConfig3 < timestampConfig4) {
			// That's what we want
			LOGGER.debug("Config for timestamps ok.");
		} else {
			errorHandler.handleError("Configuration error. Times are not in order ascending.");
			throw new IllegalStateException("Configuration error. Times are not in order ascending.");
		}

		if (tempB1.compareTo(tempB2) < 0 && tempB2.compareTo(tempB3) < 0 && tempB3.compareTo(tempB4) < 0) {
			// That's what we want.
			LOGGER.debug("Config for B temperatures ok.");
		} else {
			errorHandler.handleError("Configuration error. Temperatures for config 1 (B) are not in order ascending.");
			throw new IllegalStateException("Configuration error. Temperatures for config 1 (B) are not in order ascending.");
		}

		if (tempC1.compareTo(tempC2) < 0 && tempC2.compareTo(tempC3) < 0 && tempC3.compareTo(tempC4) < 0) {
			// That's what we want.
			LOGGER.debug("Config for C temperatures ok.");
		} else {
			errorHandler.handleError("Configuration error. Temperatures for config 2 (C) are not in order ascending.");
			throw new IllegalStateException("Configuration error. Temperatures for config 2 (C) are not in order ascending.");
		}

		if (tempD1.compareTo(tempD2) < 0 && tempD2.compareTo(tempD3) < 0 && tempD3.compareTo(tempD4) < 0) {
			// That's what we want.
			LOGGER.debug("Config for D temperatures ok.");
		} else {
			errorHandler.handleError("Configuration error. Temperatures for config 3 (D) are not in order ascending.");
			throw new IllegalStateException("Configuration error. Temperatures for config 3 (D) are not in order ascending.");
		}

		// Check in which period of time we are now:
		// A: Before timestamp 1 (Turn everything off and do nothing)
		// B: Between timestamp 1 and 2 (Process according to config with ID 1)
		// C: Between timestamp 2 and 3 (Process according to config with ID 2)
		// D: Between timestamp 3 and 4 (Process according to config with ID 3)
		// E: After timestamp 4 (Turn everything off and do nothing)

		String state;
		Date nowDate = new Date();
		long now = nowDate.getTime();

		if (now < timestampConfig1) {
			state = "A";
		} else if (now < timestampConfig2) {
			state = "B";
		} else if (now < timestampConfig3) {
			state = "C";
		} else if (now < timestampConfig4) {
			state = "D";
		} else {
			state = "E";
		}

		// Take temperature readings.
		BigDecimal temperature1 = temperatureReader.readThermometer1();
		BigDecimal temperature2 = temperatureReader.readThermometer2();
		BigDecimal temperature3 = temperatureReader.readThermometer3();
		BigDecimal temperature4 = temperatureReader.readThermometer4();
		LOGGER.debug("Current date: [{}] ({}). We are in period: [{}]. Thermometer 1: [{}], Thermometer 2: [{}]", nowDate, now, state, temperature1, temperature2);

		// Prepare temperature log to save to DB later
		TemperatureLog temperatureLog = new TemperatureLog();
		temperatureLog.setTimestamp(now);
		temperatureLog.setFreezer(relayManager.getR11().get());
		temperatureLog.setHeater(relayManager.getR12().get());
		temperatureLog.setState(state);
		temperatureLog.setTemperature1Keg(temperature1);
		temperatureLog.setTemperature2Freezer(temperature2);
		temperatureLog.setTemperature3(temperature3);
		temperatureLog.setTemperature4(temperature4);

		// Determine what action to take.

		switch (state) {
		case "A":
			// Do nothing. Period not started. Set all relays off.
			relayManager.turnRelayStateForAllRelays(1, false);
			sleep(1000);
			break;

		case "B":
			takeActionForTemperatureSet(temperatureLog, temperature1, tempB1, tempB2, tempB3, tempB4);
			break;

		case "C":
			takeActionForTemperatureSet(temperatureLog, temperature1, tempC1, tempC2, tempC3, tempC4);
			break;

		case "D":
			takeActionForTemperatureSet(temperatureLog, temperature1, tempD1, tempD2, tempD3, tempD4);
			break;

		case "E":
			// Do nothing. Period is over. Set all relays off.
			relayManager.turnRelayStateForAllRelays(1, false);
			sleep(1000);
			break;

		default:
			LOGGER.warn("Invalid state : [{}]", state);
			errorHandler.handleError("Invalid state : " + state);
			break;
		}
		
		// The logic above (takeActionForTemperatureSet) stored the config temperatures. It is now time to save temperatureLog to database.
		temperatureLogRepository.save(temperatureLog);
		
		// Broadcast the news!
		ChartResponse chartData = new ChartResponse();
		chartData.setContent("Sending temperatures for last hour");
		chartData.setTemperatureLogs(temperatureChartHelper.getLastHourData(now));
		messagingTemplate.convertAndSend("/topic/livechartevent", chartData);
	}

	/**
	 * 
	 * @param temperatureLog : Used only to store temp1, 2, 3 and 4 into it, for chart purpose.
	 * @param temperatureKeg
	 * @param temp1
	 * @param temp2
	 * @param temp3
	 * @param temp4
	 */
	private void takeActionForTemperatureSet(TemperatureLog temperatureLog, BigDecimal temperatureKeg, BigDecimal temp1, BigDecimal temp2, BigDecimal temp3, BigDecimal temp4) {

		// Logic:

		// if keg temperature is below temp1, Freezer: OFF, Heat: ON.

		// if keg temperature is between temp1 and temp2, Freezer: OFF, Heat: No
		// change.
		// stop freezer

		// if keg temperature is between temp2 and temp3, do nothing

		// if keg temperature is between temp 3 and temp 4, Heat: OFF.
		
		// if keg temperature is higher than temp 4:  Freezer: ON, Heat: OFF

		// But first, store our configured temperature for chart purpose
		temperatureLog.setConfigTemperature1(temp1);
		temperatureLog.setConfigTemperature2(temp2);
		temperatureLog.setConfigTemperature3(temp3);
		temperatureLog.setConfigTemperature4(temp4);
		
		LOGGER.debug("Currently, freezer is [{}] and heater is [{}]", relayManager.getR11().get(), relayManager.getR12().get());
		LOGGER.debug("Keg temperature is [{}]", temperatureKeg);
		if (temperatureKeg.compareTo(temp1) < 0) {
			LOGGER.debug("Keg temperature is therefore below temp1 [{}]. Freezer: OFF, Heat: ON.", temp1);
			if (relayManager.getR11().get() == true) { 
				relayManager.turnRelayState(1, 1, false); // If freezer is on ==> Set Freezer OFF
			}
			if (relayManager.getR12().get() == false) {
				sleep(2000);
				relayManager.turnRelayState(1, 2, true); // If heat is off ==> Set Heat ON
			}
			
		} else if (temperatureKeg.compareTo(temp2) < 0) {
			LOGGER.debug("Keg temperature is therefore between temp1 [{}] and temp2 [{}]. Freezer: OFF, Heat: No change.", temp1, temp2);
			if (relayManager.getR11().get() == true) {
				relayManager.turnRelayState(1, 1, false); // If freezer is on ==> Freezer OFF
			}
		} else if (temperatureKeg.compareTo(temp3) < 0) {
			LOGGER.debug("Keg temperature is therefore between temp2 [{}] and temp3 [{}]. Freezer: No change, Heat: No change.", temp2, temp3);
		} else if (temperatureKeg.compareTo(temp4) < 0) {
			LOGGER.debug("Keg temperature is therefore between temp3 [{}] and temp4 [{}]. Freezer: No change, Heat: OFF", temp3, temp4);
			if (relayManager.getR12().get() == true) {
				relayManager.turnRelayState(1,  2, false);
			}
		} else {
			LOGGER.debug("Keg temperature is therefore higher than temp4 [{}]. Freezer: ON, Heat: OFF.", temp4);
			if (relayManager.getR12().get() == true) {
				relayManager.turnRelayState(1, 2, false); // If heat is on ==> Heat OFF
			}
			if (relayManager.getR11().get() == false) {
				sleep(2000);
				relayManager.turnRelayState(1, 1, true); // If freezer is off ==> Freezer ON
			}
			
		}
	}
	
	private void sleep(long timeInMillis) {
		try {
			Thread.sleep(timeInMillis);
		} catch (InterruptedException e) {
			LOGGER.error("InterruptedException", e);
		}
	}
}
