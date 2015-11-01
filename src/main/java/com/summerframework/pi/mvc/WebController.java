package com.summerframework.pi.mvc;

import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pi4j.util.ExecUtil;
import com.summerframework.pi.jpa.model.TemperatureLog;
import com.summerframework.pi.jpa.model.TemperatureLogRepository;
import com.summerframework.pi.pi4j.ControlGpioExample;
import com.summerframework.pi.pi4j.GpioSingleton;
import com.summerframework.pi.pi4j.TemperatureReader;

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

}
