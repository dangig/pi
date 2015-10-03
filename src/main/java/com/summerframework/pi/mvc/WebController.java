package com.summerframework.pi.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.summerframework.pi.pi4j.ControlGpioExample;
import com.summerframework.pi.pi4j.GpioSingleton;

@Controller
public class WebController {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebController.class);

	@Autowired
	private ControlGpioExample controlGpioExampleOn27;

	@Autowired
	private GpioSingleton gpioSingleton;
	
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

}
