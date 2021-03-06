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
import com.summerframework.pi.services.TemperatureChartHelper;
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

	@Autowired
	private TemperatureChartHelper temperatureChartHelper;

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
		KeezerConfig configF = keezerConfigRepository.findOne(5l);
		KeezerConfig configG = keezerConfigRepository.findOne(6l);
		KeezerConfig configH = keezerConfigRepository.findOne(7l);
		KeezerConfig configI = keezerConfigRepository.findOne(8l);
		KeezerConfig configJ = keezerConfigRepository.findOne(9l);

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
		currentConfig.setTe1(configE.getThreshold1HeatStartFreezerStopTemperature());
		currentConfig.setTe2(configE.getThreshold2FreezerStopTemperature());
		currentConfig.setTe3(configE.getThreshold3HeatStopTemperature());
		currentConfig.setTe4(configE.getThreshold4FreezerStartHeatStopTemperature());
		
		currentConfig.setDatef(WEB_DATE_SDF.format(configF.getStateChangeDatetime()));
		currentConfig.setTf1(configF.getThreshold1HeatStartFreezerStopTemperature());
		currentConfig.setTf2(configF.getThreshold2FreezerStopTemperature());
		currentConfig.setTf3(configF.getThreshold3HeatStopTemperature());
		currentConfig.setTf4(configF.getThreshold4FreezerStartHeatStopTemperature());
		
		currentConfig.setDateg(WEB_DATE_SDF.format(configG.getStateChangeDatetime()));
		currentConfig.setTg1(configG.getThreshold1HeatStartFreezerStopTemperature());
		currentConfig.setTg2(configG.getThreshold2FreezerStopTemperature());
		currentConfig.setTg3(configG.getThreshold3HeatStopTemperature());
		currentConfig.setTg4(configG.getThreshold4FreezerStartHeatStopTemperature());
		
		currentConfig.setDateh(WEB_DATE_SDF.format(configH.getStateChangeDatetime()));
		currentConfig.setTh1(configH.getThreshold1HeatStartFreezerStopTemperature());
		currentConfig.setTh2(configH.getThreshold2FreezerStopTemperature());
		currentConfig.setTh3(configH.getThreshold3HeatStopTemperature());
		currentConfig.setTh4(configH.getThreshold4FreezerStartHeatStopTemperature());

		
		currentConfig.setDatei(WEB_DATE_SDF.format(configI.getStateChangeDatetime()));
		
		currentConfig.setDatej(WEB_DATE_SDF.format(configJ.getStateChangeDatetime()));

		return currentConfig;

	}

	@RequestMapping(value = "/updatekeezerconfig", method = RequestMethod.POST)
	public @ResponseBody UpdateConfigResults updateKeezerConfig(@RequestBody ConfigForm configForm) {

		LOGGER.debug("*********** UPDATING KEEZER CONFIG ********************");

		WEB_DATE_SDF.setTimeZone(TimeZone.getTimeZone("Canada/Eastern"));

		// By design, defaulting success to false, and set to true only at the
		// end once successful. Error list is linked here for simplicity.
		UpdateConfigResults results = new UpdateConfigResults();
		results.setSuccess(false);
		ArrayList<String> errors = new ArrayList<String>();
		results.setErrors(errors);

		// Input validation. Dates must be valid, in ascending order.
		// Temperatures must be valid, in ascending order for a given state.
		long timestampConfigB;
		long timestampConfigC;
		long timestampConfigD;
		long timestampConfigE;
		long timestampConfigF;
		long timestampConfigG;
		long timestampConfigH;
		long timestampConfigI;
		long timestampConfigJ;
		
		try {
			timestampConfigB = WEB_DATE_SDF.parse(configForm.getDateb()).getTime();
		} catch (ParseException | NullPointerException e) {
			errors.add("Date B is not properly formatted, or incomplete. Please fix and try again.");
			return results;
		}
		try {
			timestampConfigC = WEB_DATE_SDF.parse(configForm.getDatec()).getTime();
		} catch (ParseException | NullPointerException e) {
			errors.add("Date C is not properly formatted, or incomplete. Please fix and try again.");
			return results;
		}
		try {
			timestampConfigD = WEB_DATE_SDF.parse(configForm.getDated()).getTime();
		} catch (ParseException | NullPointerException e) {
			errors.add("Date D is not properly formatted, or incomplete. Please fix and try again.");
			return results;
		}
		try {
			timestampConfigE = WEB_DATE_SDF.parse(configForm.getDatee()).getTime();
		} catch (ParseException | NullPointerException e) {
			errors.add("Date E is not properly formatted, or incomplete. Please fix and try again.");
			return results;
		}
		try {
			timestampConfigF = WEB_DATE_SDF.parse(configForm.getDatef()).getTime();
		} catch (ParseException | NullPointerException e) {
			errors.add("Date F is not properly formatted, or incomplete. Please fix and try again.");
			return results;
		}
		try {
			timestampConfigG = WEB_DATE_SDF.parse(configForm.getDateg()).getTime();
		} catch (ParseException | NullPointerException e) {
			errors.add("Date G is not properly formatted, or incomplete. Please fix and try again.");
			return results;
		}
		try {
			timestampConfigH = WEB_DATE_SDF.parse(configForm.getDateh()).getTime();
		} catch (ParseException | NullPointerException e) {
			errors.add("Date H is not properly formatted, or incomplete. Please fix and try again.");
			return results;
		}
		try {
			timestampConfigI = WEB_DATE_SDF.parse(configForm.getDatei()).getTime();
		} catch (ParseException | NullPointerException e) {
			errors.add("Date I is not properly formatted, or incomplete. Please fix and try again.");
			return results;
		}
		try {
			timestampConfigJ = WEB_DATE_SDF.parse(configForm.getDatej()).getTime();
		} catch (ParseException | NullPointerException e) {
			errors.add("Date J is not properly formatted, or incomplete. Please fix and try again.");
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
		BigDecimal tempE1 = configForm.getTe1();
		BigDecimal tempE2 = configForm.getTe2();
		BigDecimal tempE3 = configForm.getTe3();
		BigDecimal tempE4 = configForm.getTe4();
		BigDecimal tempF1 = configForm.getTf1();
		BigDecimal tempF2 = configForm.getTf2();
		BigDecimal tempF3 = configForm.getTf3();
		BigDecimal tempF4 = configForm.getTf4();
		BigDecimal tempG1 = configForm.getTg1();
		BigDecimal tempG2 = configForm.getTg2();
		BigDecimal tempG3 = configForm.getTg3();
		BigDecimal tempG4 = configForm.getTg4();
		BigDecimal tempH1 = configForm.getTh1();
		BigDecimal tempH2 = configForm.getTh2();
		BigDecimal tempH3 = configForm.getTh3();
		BigDecimal tempH4 = configForm.getTh4();

		// Validate if config is ok
		// NOTE: This logic is duplicated in KeezerProcessingService...
		if (timestampConfigB < timestampConfigC && timestampConfigC < timestampConfigD && timestampConfigD < timestampConfigE && timestampConfigE < timestampConfigF && timestampConfigF < timestampConfigG && timestampConfigG < timestampConfigH && timestampConfigH < timestampConfigI && timestampConfigI < timestampConfigJ) {
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

		if (tempE1.compareTo(tempE2) < 0 && tempE2.compareTo(tempE3) < 0 && tempE3.compareTo(tempE4) < 0) {
			// That's what we want.
			LOGGER.debug("Config for E temperatures ok.");
		} else {
			errors.add("Temperatures for config 4 (E) are not in order ascending. Please fix and try again.");
			return results;
		}

		if (tempF1.compareTo(tempF2) < 0 && tempF2.compareTo(tempF3) < 0 && tempF3.compareTo(tempF4) < 0) {
			// That's what we want.
			LOGGER.debug("Config for F temperatures ok.");
		} else {
			errors.add("Temperatures for config 5 (F) are not in order ascending. Please fix and try again.");
			return results;
		}
		
		if (tempG1.compareTo(tempG2) < 0 && tempG2.compareTo(tempG3) < 0 && tempG3.compareTo(tempG4) < 0) {
			// That's what we want.
			LOGGER.debug("Config for G temperatures ok.");
		} else {
			errors.add("Temperatures for config 6 (G) are not in order ascending. Please fix and try again.");
			return results;
		}
		
		if (tempH1.compareTo(tempH2) < 0 && tempH2.compareTo(tempH3) < 0 && tempH3.compareTo(tempH4) < 0) {
			// That's what we want.
			LOGGER.debug("Config for H temperatures ok.");
		} else {
			errors.add("Temperatures for config 7 (H) are not in order ascending. Please fix and try again.");
			return results;
		}
		
		
		// Ok validation successful! Try to update.

		KeezerConfig configB = new KeezerConfig();
		configB.setId(1l); // by design
		configB.setStateChangeDatetime(timestampConfigB);
		configB.setThreshold1HeatStartFreezerStopTemperature(tempB1);
		configB.setThreshold2FreezerStopTemperature(tempB2);
		configB.setThreshold3HeatStopTemperature(tempB3);
		configB.setThreshold4FreezerStartHeatStopTemperature(tempB4);

		KeezerConfig configC = new KeezerConfig();
		configC.setId(2l); // by design
		configC.setStateChangeDatetime(timestampConfigC);
		configC.setThreshold1HeatStartFreezerStopTemperature(tempC1);
		configC.setThreshold2FreezerStopTemperature(tempC2);
		configC.setThreshold3HeatStopTemperature(tempC3);
		configC.setThreshold4FreezerStartHeatStopTemperature(tempC4);

		KeezerConfig configD = new KeezerConfig();
		configD.setId(3l); // by design
		configD.setStateChangeDatetime(timestampConfigD);
		configD.setThreshold1HeatStartFreezerStopTemperature(tempD1);
		configD.setThreshold2FreezerStopTemperature(tempD2);
		configD.setThreshold3HeatStopTemperature(tempD3);
		configD.setThreshold4FreezerStartHeatStopTemperature(tempD4);

		KeezerConfig configE = new KeezerConfig();
		configE.setId(4l); // by design
		configE.setStateChangeDatetime(timestampConfigE);
		configE.setThreshold1HeatStartFreezerStopTemperature(tempE1);
		configE.setThreshold2FreezerStopTemperature(tempE2);
		configE.setThreshold3HeatStopTemperature(tempE3);
		configE.setThreshold4FreezerStartHeatStopTemperature(tempE4);
		
		KeezerConfig configF = new KeezerConfig();
		configF.setId(5l); // by design
		configF.setStateChangeDatetime(timestampConfigF);
		configF.setThreshold1HeatStartFreezerStopTemperature(tempF1);
		configF.setThreshold2FreezerStopTemperature(tempF2);
		configF.setThreshold3HeatStopTemperature(tempF3);
		configF.setThreshold4FreezerStartHeatStopTemperature(tempF4);

		KeezerConfig configG = new KeezerConfig();
		configG.setId(6l); // by design
		configG.setStateChangeDatetime(timestampConfigG);
		configG.setThreshold1HeatStartFreezerStopTemperature(tempG1);
		configG.setThreshold2FreezerStopTemperature(tempG2);
		configG.setThreshold3HeatStopTemperature(tempG3);
		configG.setThreshold4FreezerStartHeatStopTemperature(tempG4);
		
		KeezerConfig configH = new KeezerConfig();
		configH.setId(7l); // by design
		configH.setStateChangeDatetime(timestampConfigH);
		configH.setThreshold1HeatStartFreezerStopTemperature(tempH1);
		configH.setThreshold2FreezerStopTemperature(tempH2);
		configH.setThreshold3HeatStopTemperature(tempH3);
		configH.setThreshold4FreezerStartHeatStopTemperature(tempH4);
		
		KeezerConfig configI = new KeezerConfig();
		configI.setId(8l); // by design
		configI.setStateChangeDatetime(timestampConfigI);
		configI.setThreshold1HeatStartFreezerStopTemperature(null);
		configI.setThreshold2FreezerStopTemperature(null);
		configI.setThreshold3HeatStopTemperature(null);
		configI.setThreshold4FreezerStartHeatStopTemperature(null);
		
		KeezerConfig configJ = new KeezerConfig();
		configJ.setId(9l); // by design
		configJ.setStateChangeDatetime(timestampConfigJ);
		configJ.setThreshold1HeatStartFreezerStopTemperature(null);
		configJ.setThreshold2FreezerStopTemperature(null);
		configJ.setThreshold3HeatStopTemperature(null);
		configJ.setThreshold4FreezerStartHeatStopTemperature(null);

		// save to db
		try {
			keezerConfigRepository.save(configB);
			keezerConfigRepository.save(configC);
			keezerConfigRepository.save(configD);
			keezerConfigRepository.save(configE);
			keezerConfigRepository.save(configF);
			keezerConfigRepository.save(configG);
			keezerConfigRepository.save(configH);
			keezerConfigRepository.save(configI);
			keezerConfigRepository.save(configJ);
		} catch (Exception e) {
			errors.add("Unexpected error occurred. Unable to save to database. Error message: " + e.getMessage());
			return results;
		}

		// Finally !! Success.
		results.setSuccess(true);
		return results;
	}

	/**
	 * Called on-demand by the web page to chart all historical data.
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getalldata", method = RequestMethod.GET)
	public @ResponseBody List<TemperatureLog> getAllTemperatureLogs() {

		LOGGER.debug("*********** GETTING ALL TEMPERATURE LOGS ********************");
		return temperatureChartHelper.getAllData();
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
