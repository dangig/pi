package com.summerframework.pi.mvc;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pi4j.util.ExecUtil;
import com.summerframework.pi.jpa.model.KeezerConfig;
import com.summerframework.pi.jpa.model.KeezerConfigRepository;
import com.summerframework.pi.jpa.model.TemperatureLog;
import com.summerframework.pi.jpa.model.TemperatureLogRepository;
import com.summerframework.pi.pi4j.ControlGpioExample;
import com.summerframework.pi.pi4j.GpioSingleton;
import com.summerframework.pi.pi4j.TemperatureReader;
import com.summerframework.pi.websockets.ChartRequest;
import com.summerframework.pi.websockets.ChartResponse;

@Controller
public class WebController {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebController.class);

	@Autowired
	private ControlGpioExample controlGpioExampleOn27;

	@Autowired
	private GpioSingleton gpioSingleton;

	@Autowired
	private TemperatureReader temperatureReader;

	@Autowired
	private TemperatureLogRepository temperatureLogRepository;

	@Autowired
	private KeezerConfigRepository keezerConfigRepository;

	private SimpleDateFormat WEB_DATE_SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

	@RequestMapping(value = "/telix", method = RequestMethod.GET)
	public @ResponseBody String getTelix() {
		LOGGER.info("In getTelix!");
		return "Bonjour, en direct du Raspberry Pi.";
	}

	@RequestMapping(value = "/ledtestdonotcall", method = RequestMethod.GET)
	@Deprecated
	public @ResponseBody String allumeGpio27Pi4jGpio16Board() {

		if (gpioSingleton.isRunningOnPi()) {
			controlGpioExampleOn27.testLightFor5seconds();
		} else {
			LOGGER.warn("Not running on Raspberry Pi currently.");
		}

		return "Lumière allumée";
	}

	@RequestMapping(value = "/testr1on", method = RequestMethod.GET)
	public @ResponseBody String testr1on() {

		if (gpioSingleton.isRunningOnPi()) {

			try {
				return "R1 on. " + ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d DAE002h9 -v -r 1 -c on ");
			} catch (IOException | InterruptedException e) {
				throw new IllegalStateException(e);
			}

		} else {
			LOGGER.warn("Not running on Raspberry Pi currently.");
		}

		return "R1 on";
	}

	@RequestMapping(value = "/testr1off", method = RequestMethod.GET)
	public @ResponseBody String testr1off() {

		if (gpioSingleton.isRunningOnPi()) {

			try {
				return "R1 off. " + ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d DAE002h9 -v -r 1 -c off ");
			} catch (IOException | InterruptedException e) {
				throw new IllegalStateException(e);
			}

		} else {
			LOGGER.warn("Not running on Raspberry Pi currently.");
		}

		return "R1 off";
	}

	@RequestMapping(value = "/testtemperatures", method = RequestMethod.GET)
	public @ResponseBody String testtemp1() {
		if (gpioSingleton.isRunningOnPi()) {

			TemperatureLog temperatureLog = new TemperatureLog();
			temperatureLog.setTimestamp(new Date().getTime());
			temperatureLog.setTemperature1Keg(temperatureReader.readThermometer1());
			temperatureLog.setTemperature2Freezer(temperatureReader.readThermometer2());
			temperatureLogRepository.save(temperatureLog);

			return "Temperature 1: " + temperatureLog.getTemperature1Keg() + "; Temperature 2: " + temperatureLog.getTemperature2Freezer();
		} else {
			LOGGER.warn("Not running on Raspberry Pi currently.");
		}

		return "Not running on Raspberry Pi currently.";
	}

	@RequestMapping(value = "/getcurrentkeezerconfig", method = RequestMethod.GET)
	public @ResponseBody ConfigForm getCurrentKeezerConfig() {

		LOGGER.debug("*********** GETTING KEEZER CONFIG ********************");
		
		WEB_DATE_SDF.setTimeZone(TimeZone.getTimeZone("Canada/Eastern"));

		// By design:
		KeezerConfig configB = keezerConfigRepository.findOne(1l);
		KeezerConfig configC = keezerConfigRepository.findOne(2l);
		KeezerConfig configD = keezerConfigRepository.findOne(3l);
		KeezerConfig configE = keezerConfigRepository.findOne(4l);

		ConfigForm currentConfig = new ConfigForm();
		currentConfig.setDateb(WEB_DATE_SDF.format(configB.getStateChangeDatetime()));
		currentConfig.setTb1(configB.getThreshold1HeatStartFreezerStopTemperature());
		currentConfig.setTb2(configB.getThreshold2FreezerStopTemperature());
		currentConfig.setTb3(configB.getThreshold3HeatStopTemperature());
		currentConfig.setTb4(configB.getThreshold4FreezerStartHeatStopTemperature());

		currentConfig.setDatec(WEB_DATE_SDF.format(configC.getStateChangeDatetime()));
		currentConfig.setTc1(configC.getThreshold1HeatStartFreezerStopTemperature());
		currentConfig.setTc2(configC.getThreshold2FreezerStopTemperature());
		currentConfig.setTc3(configC.getThreshold3HeatStopTemperature());
		currentConfig.setTc4(configC.getThreshold4FreezerStartHeatStopTemperature());

		currentConfig.setDated(WEB_DATE_SDF.format(configD.getStateChangeDatetime()));
		currentConfig.setTd1(configD.getThreshold1HeatStartFreezerStopTemperature());
		currentConfig.setTd2(configD.getThreshold2FreezerStopTemperature());
		currentConfig.setTd3(configD.getThreshold3HeatStopTemperature());
		currentConfig.setTd4(configD.getThreshold4FreezerStartHeatStopTemperature());

		currentConfig.setDatee(WEB_DATE_SDF.format(configE.getStateChangeDatetime()));

		return currentConfig;

	}
	
	@RequestMapping(value = "/updatekeezerconfig", method = RequestMethod.POST)
	public @ResponseBody UpdateConfigResults updateKeezerConfig(@RequestBody ConfigForm configForm) {

		LOGGER.debug("*********** UPDATING KEEZER CONFIG ********************");
		
		WEB_DATE_SDF.setTimeZone(TimeZone.getTimeZone("Canada/Eastern"));

		// By design, defaulting success to false, and set to true only at the end once successful. Error list is linked here for simplicity.
		UpdateConfigResults results = new UpdateConfigResults();
		results.setSuccess(false);
		ArrayList<String> errors = new ArrayList<String>();
		results.setErrors(errors);
		
		// Input validation. Dates must be valid, in ascending order. Temperatures must be valid, in ascending order for a given state.
		long timestampConfig1;
		long timestampConfig2;
		long timestampConfig3;
		long timestampConfig4;
		try {
			timestampConfig1 = WEB_DATE_SDF.parse(configForm.getDateb()).getTime();
		} catch (ParseException | NullPointerException e) {
			errors.add("Date B is not properly formatted, or incomplete. Please fix and try again.");
			return results;
		}
		try {
			timestampConfig2 = WEB_DATE_SDF.parse(configForm.getDatec()).getTime();
		} catch (ParseException | NullPointerException e) {
			errors.add("Date C is not properly formatted, or incomplete. Please fix and try again.");
			return results;
		}
		try {
			timestampConfig3 = WEB_DATE_SDF.parse(configForm.getDated()).getTime();
		} catch (ParseException | NullPointerException e) {
			errors.add("Date D is not properly formatted, or incomplete. Please fix and try again.");
			return results;
		}
		try {
			timestampConfig4 = WEB_DATE_SDF.parse(configForm.getDatee()).getTime();
		} catch (ParseException | NullPointerException e) {
			errors.add("Date E is not properly formatted, or incomplete. Please fix and try again.");
			return results;
		}
		
		BigDecimal tempB1 = configForm.getTb1();
		BigDecimal tempB2 = configForm.getTb2();
		BigDecimal tempB3 = configForm.getTb3();
		BigDecimal tempB4 = configForm.getTb4();
		BigDecimal tempC1 = configForm.getTc1();
		BigDecimal tempC2 = configForm.getTc2();
		BigDecimal tempC3 = configForm.getTc3();
		BigDecimal tempC4 = configForm.getTc4();
		BigDecimal tempD1 = configForm.getTd1();
		BigDecimal tempD2 = configForm.getTd2();
		BigDecimal tempD3 = configForm.getTd3();
		BigDecimal tempD4 = configForm.getTd4();
		
		// Validate if config is ok
		// NOTE: This logic is duplicated in KeezerProcessingService... 
		if (timestampConfig1 < timestampConfig2 && timestampConfig2 < timestampConfig3 && timestampConfig3 < timestampConfig4) {
			// That's what we want
			LOGGER.debug("Config for timestamps ok.");
		} else {
			errors.add("Dates must be in ascending order. Please fix and try again.");
			return results;
		}

		if (tempB1.compareTo(tempB2) < 0 && tempB2.compareTo(tempB3) < 0 && tempB3.compareTo(tempB4) < 0) {
			// That's what we want.
			LOGGER.debug("Config for B temperatures ok.");
		} else {
			errors.add("Temperatures for config 1 (B) are not in order ascending. Please fix and try again.");
			return results;
		}

		if (tempC1.compareTo(tempC2) < 0 && tempC2.compareTo(tempC3) < 0 && tempC3.compareTo(tempC4) < 0) {
			// That's what we want.
			LOGGER.debug("Config for C temperatures ok.");
		} else {
			errors.add("Temperatures for config 2 (C) are not in order ascending. Please fix and try again.");
			return results;
		}

		if (tempD1.compareTo(tempD2) < 0 && tempD2.compareTo(tempD3) < 0 && tempD3.compareTo(tempD4) < 0) {
			// That's what we want.
			LOGGER.debug("Config for D temperatures ok.");
		} else {
			errors.add("Temperatures for config 3 (D) are not in order ascending. Please fix and try again.");
			return results;
		}
		
		// Ok validation successful! Try to update.
		
		
		KeezerConfig configB = new KeezerConfig();
		configB.setId(1l); // by design
		configB.setStateChangeDatetime(timestampConfig1);
		configB.setThreshold1HeatStartFreezerStopTemperature(tempB1);
		configB.setThreshold2FreezerStopTemperature(tempB2);
		configB.setThreshold3HeatStopTemperature(tempB3);
		configB.setThreshold4FreezerStartHeatStopTemperature(tempB4);
		
		KeezerConfig configC = new KeezerConfig();
		configC.setId(2l); // by design
		configC.setStateChangeDatetime(timestampConfig2);
		configC.setThreshold1HeatStartFreezerStopTemperature(tempC1);
		configC.setThreshold2FreezerStopTemperature(tempC2);
		configC.setThreshold3HeatStopTemperature(tempC3);
		configC.setThreshold4FreezerStartHeatStopTemperature(tempC4);
		
		KeezerConfig configD = new KeezerConfig();
		configD.setId(3l); // by design
		configD.setStateChangeDatetime(timestampConfig3);
		configD.setThreshold1HeatStartFreezerStopTemperature(tempD1);
		configD.setThreshold2FreezerStopTemperature(tempD2);
		configD.setThreshold3HeatStopTemperature(tempD3);
		configD.setThreshold4FreezerStartHeatStopTemperature(tempD4);
		
		KeezerConfig configE = new KeezerConfig();
		configE.setId(4l); // by design
		configE.setStateChangeDatetime(timestampConfig4);
		configE.setThreshold1HeatStartFreezerStopTemperature(null);
		configE.setThreshold2FreezerStopTemperature(null);
		configE.setThreshold3HeatStopTemperature(null);
		configE.setThreshold4FreezerStartHeatStopTemperature(null);
		
		// save to db
		try {
			keezerConfigRepository.save(configB);
			keezerConfigRepository.save(configC);
			keezerConfigRepository.save(configD);
			keezerConfigRepository.save(configE);
		} catch (Exception e) {
			errors.add("Unexpected error occurred. Unable to save to database. Error message: " + e.getMessage());
			return results;
		}
		
		// Finally !! Success.
		results.setSuccess(true);
		return results;
	}

	// WebSocket subscription
	@MessageMapping("/livechart")
	@SendTo("/topic/livechartevent")
	public ChartResponse liveChartEvent(ChartRequest chartRequest) throws Exception {
		ChartResponse chartResponse = new ChartResponse();
		chartResponse.setContent("This is the event data");
		return chartResponse;
	}

}
