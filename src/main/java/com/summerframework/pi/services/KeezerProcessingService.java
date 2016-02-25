package com.summerframework.pi.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
		KeezerConfig keezerConfigB = keezerConfigRepository.findOne(1l);
		KeezerConfig keezerConfigC = keezerConfigRepository.findOne(2l);
		KeezerConfig keezerConfigD = keezerConfigRepository.findOne(3l);
		KeezerConfig keezerConfigE = keezerConfigRepository.findOne(4l);
		KeezerConfig keezerConfigF = keezerConfigRepository.findOne(5l);
		KeezerConfig keezerConfigG = keezerConfigRepository.findOne(6l);
		KeezerConfig keezerConfigH = keezerConfigRepository.findOne(7l);
		KeezerConfig keezerConfigI = keezerConfigRepository.findOne(8l);
		KeezerConfig keezerConfigJ = keezerConfigRepository.findOne(9l);

		long timestampConfigB = keezerConfigB.getStateChangeDatetime();
		long timestampConfigC = keezerConfigC.getStateChangeDatetime();
		long timestampConfigD = keezerConfigD.getStateChangeDatetime();
		long timestampConfigE = keezerConfigE.getStateChangeDatetime();
		long timestampConfigF = keezerConfigF.getStateChangeDatetime();
		long timestampConfigG = keezerConfigG.getStateChangeDatetime();
		long timestampConfigH = keezerConfigH.getStateChangeDatetime();
		long timestampConfigI = keezerConfigI.getStateChangeDatetime();
		long timestampConfigJ = keezerConfigJ.getStateChangeDatetime();

		BigDecimal tempB1 = keezerConfigB.getThreshold1HeatStartFreezerStopTemperature();
		BigDecimal tempB2 = keezerConfigB.getThreshold2FreezerStopTemperature();
		BigDecimal tempB3 = keezerConfigB.getThreshold3HeatStopTemperature();
		BigDecimal tempB4 = keezerConfigB.getThreshold4FreezerStartHeatStopTemperature();

		BigDecimal tempC1 = keezerConfigC.getThreshold1HeatStartFreezerStopTemperature();
		BigDecimal tempC2 = keezerConfigC.getThreshold2FreezerStopTemperature();
		BigDecimal tempC3 = keezerConfigC.getThreshold3HeatStopTemperature();
		BigDecimal tempC4 = keezerConfigC.getThreshold4FreezerStartHeatStopTemperature();
		
		BigDecimal tempD1 = keezerConfigD.getThreshold1HeatStartFreezerStopTemperature();
		BigDecimal tempD2 = keezerConfigD.getThreshold2FreezerStopTemperature();
		BigDecimal tempD3 = keezerConfigD.getThreshold3HeatStopTemperature();
		BigDecimal tempD4 = keezerConfigD.getThreshold4FreezerStartHeatStopTemperature();

		BigDecimal tempE1 = keezerConfigE.getThreshold1HeatStartFreezerStopTemperature();
		BigDecimal tempE2 = keezerConfigE.getThreshold2FreezerStopTemperature();
		BigDecimal tempE3 = keezerConfigE.getThreshold3HeatStopTemperature();
		BigDecimal tempE4 = keezerConfigE.getThreshold4FreezerStartHeatStopTemperature();

		BigDecimal tempF1 = keezerConfigF.getThreshold1HeatStartFreezerStopTemperature();
		BigDecimal tempF2 = keezerConfigF.getThreshold2FreezerStopTemperature();
		BigDecimal tempF3 = keezerConfigF.getThreshold3HeatStopTemperature();
		BigDecimal tempF4 = keezerConfigF.getThreshold4FreezerStartHeatStopTemperature();
		
		BigDecimal tempG1 = keezerConfigG.getThreshold1HeatStartFreezerStopTemperature();
		BigDecimal tempG2 = keezerConfigG.getThreshold2FreezerStopTemperature();
		BigDecimal tempG3 = keezerConfigG.getThreshold3HeatStopTemperature();
		BigDecimal tempG4 = keezerConfigG.getThreshold4FreezerStartHeatStopTemperature();
		
		BigDecimal tempH1 = keezerConfigH.getThreshold1HeatStartFreezerStopTemperature();
		BigDecimal tempH2 = keezerConfigH.getThreshold2FreezerStopTemperature();
		BigDecimal tempH3 = keezerConfigH.getThreshold3HeatStopTemperature();
		BigDecimal tempH4 = keezerConfigH.getThreshold4FreezerStartHeatStopTemperature();

		// Validate config is ok.
		// NOTE: This logic is duplicated in WebController's update Keezer config method...
		if (timestampConfigB < timestampConfigC && timestampConfigC < timestampConfigD && timestampConfigD < timestampConfigE && timestampConfigE < timestampConfigF && timestampConfigF < timestampConfigG && timestampConfigG < timestampConfigH && timestampConfigH < timestampConfigI && timestampConfigI < timestampConfigJ) {
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

		if (tempE1.compareTo(tempE2) < 0 && tempE2.compareTo(tempE3) < 0 && tempE3.compareTo(tempE4) < 0) {
			// That's what we want.
			LOGGER.debug("Config for E temperatures ok.");
		} else {
			errorHandler.handleError("Configuration error. Temperatures for config 4 (E) are not in order ascending.");
			throw new IllegalStateException("Configuration error. Temperatures for config 4 (E) are not in order ascending.");
		}

		if (tempF1.compareTo(tempF2) < 0 && tempF2.compareTo(tempF3) < 0 && tempF3.compareTo(tempF4) < 0) {
			// That's what we want.
			LOGGER.debug("Config for F temperatures ok.");
		} else {
			errorHandler.handleError("Configuration error. Temperatures for config 5 (F) are not in order ascending.");
			throw new IllegalStateException("Configuration error. Temperatures for config 5 (F) are not in order ascending.");
		}
		
		if (tempG1.compareTo(tempG2) < 0 && tempG2.compareTo(tempG3) < 0 && tempG3.compareTo(tempG4) < 0) {
			// That's what we want.
			LOGGER.debug("Config for G temperatures ok.");
		} else {
			errorHandler.handleError("Configuration error. Temperatures for config 6 (G) are not in order ascending.");
			throw new IllegalStateException("Configuration error. Temperatures for config 6 (G) are not in order ascending.");
		}
		
		if (tempH1.compareTo(tempH2) < 0 && tempH2.compareTo(tempH3) < 0 && tempH3.compareTo(tempH4) < 0) {
			// That's what we want.
			LOGGER.debug("Config for H temperatures ok.");
		} else {
			errorHandler.handleError("Configuration error. Temperatures for config 7 (H) are not in order ascending.");
			throw new IllegalStateException("Configuration error. Temperatures for config 7 (H) are not in order ascending.");
		}
		// Check in which period of time we are now:
		// A: Before timestamp 1 (Turn everything off and do nothing)
		// B: Between timestamp 1 and 2 (Process according to config with ID 1)
		// C: Between timestamp 2 and 3 (Process according to config with ID 2)
		// D: Between timestamp 3 and 4 (Process according to config with ID 3)
		// E: Between timestamp 4 and 5, linear from point 4 to 5
		// F: Between timestamp 5 and 6 (Process according to config with ID 5)
		// G: Between timestamp 6 and 7, linear from point 6 to 7
		// H: Between timestamp 7 and 8 (Process according to config with ID 7)
		// I: After timestamp 8 (Turn everything off and do nothing)
		// J: After timestamp 9 (Do nothing, do not even turn things off).

		String state;
		Date nowDate = new Date();
		long now = nowDate.getTime();

		if (now < timestampConfigB) {
			state = "A";
		} else if (now < timestampConfigC) {
			state = "B";
		} else if (now < timestampConfigD) {
			state = "C";
		} else if (now < timestampConfigE) {
			state = "D";
		} else if (now < timestampConfigF) {
			state = "E";
		} else if (now < timestampConfigG) {
			state = "F";
		} else if (now < timestampConfigH) {
			state = "G";
		} else if (now < timestampConfigI) {
			state = "H";
		} else if (now < timestampConfigJ) {
			state = "I";
		} else {
			state = "J";
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
			takeActionLinearForTemperatureSets(temperatureLog, temperature1, now, timestampConfigE, tempE1, tempE2, tempE3, tempE4, timestampConfigF, tempF1, tempF2, tempF3, tempF4);
			break;
			
		case "F":
			takeActionForTemperatureSet(temperatureLog, temperature1, tempF1, tempF2, tempF3, tempF4);
			break;

		case "G":
			takeActionLinearForTemperatureSets(temperatureLog, temperature1, now, timestampConfigG, tempG1, tempG2, tempG3, tempG4, timestampConfigH, tempH1, tempH2, tempH3, tempH4);
			break;
			
		case "H":
			takeActionForTemperatureSet(temperatureLog, temperature1, tempH1, tempH2, tempH3, tempH4);
			break;
			
		case "I":
			// Do nothing. Period is over. Set all relays off.
			relayManager.turnRelayStateForAllRelays(1, false);
			sleep(1000);
			break;
			
		case "J":
			// Do nothing.
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
	
	/**
	 * 
	 * @param temperatureLog : Used only to store temp1, 2, 3 and 4 into it, for chart purpose.
	 * @param temperatureKeg
	 * 
	 * Process linearly from point YA1 to point YB1, from YA2 to YB2, etc., having XA being the timestampXA and XB being the timestamp XB.
	 */
	private void takeActionLinearForTemperatureSets(TemperatureLog temperatureLog, BigDecimal temperatureKeg, long now, long timestampConfigXA, BigDecimal tempYA1, BigDecimal tempYA2, BigDecimal tempYA3, BigDecimal tempYA4, long timestampConfigXB, BigDecimal tempYB1, BigDecimal tempYB2, BigDecimal tempYB3, BigDecimal tempYB4) {

		// LOGIC SPECIFIC TO LINEARITY HERE
		
		// Ok. now, temp1, temp2, temp3 and temp4 are calculated linearly. The remainder of the logic is untouched.
		
		BigDecimal temp1 = solveForYC(timestampConfigXA, tempYA1, timestampConfigXB, tempYB1, now);
		BigDecimal temp2 = solveForYC(timestampConfigXA, tempYA2, timestampConfigXB, tempYB2, now);
		BigDecimal temp3 = solveForYC(timestampConfigXA, tempYA3, timestampConfigXB, tempYB3, now);
		BigDecimal temp4 = solveForYC(timestampConfigXA, tempYA4, timestampConfigXB, tempYB4, now);
		
		// END OF LOGIC SPECIFIC TO LINEARITY
		
		takeActionForTemperatureSet(temperatureLog, temperatureKeg, temp1, temp2, temp3, temp4);
	}
	
	/**
	 * Given two points, A and B, and the x coordinate of a third point C, find the y coordinate of C.
	 * 
	 * @param xA
	 * @param yA
	 * @param xB
	 * @param yB
	 * @param xC
	 * @return
	 */
	private BigDecimal solveForYC(long xA, BigDecimal yA, long xB, BigDecimal yB, long xC) {
		
		// y = mx + b.
		
		// m = (yB - yA) / (xB - xA)
		BigDecimal m = (yB.subtract(yA)).divide(new BigDecimal(xB - xA), 5, RoundingMode.HALF_UP);
		
		// b = y - mx
		BigDecimal b = yA.subtract(m.multiply(new BigDecimal(xA)));
		
		// yC = m xC + b
		BigDecimal yC = m.multiply(new BigDecimal(xC)).add(b);
		
		return yC;
	}
	
	private void sleep(long timeInMillis) {
		try {
			Thread.sleep(timeInMillis);
		} catch (InterruptedException e) {
			LOGGER.error("InterruptedException", e);
		}
	}
}
