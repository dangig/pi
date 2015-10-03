package com.summerframework.pi.pi4j;

//START SNIPPET: control-gpio-snippet

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Examples
 * FILENAME      :  ControlGpioExample.java  
 * 
 * This file is part of the Pi4J project. More information about 
 * this project can be found here:  http://www.pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2015 Pi4J
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pi4j.io.gpio.GpioPinDigitalOutput;

/**
 * This example code demonstrates how to perform simple state control of a GPIO
 * pin on the Raspberry Pi.
 * 
 * @author Robert Savage
 */
@Component
@Deprecated
public class ControlGpioExample {

	private static final Logger LOGGER = LoggerFactory.getLogger(ControlGpioExample.class);

	@Autowired
	private GpioSingleton gpioSingleton;

	@Deprecated
	public void testLightFor5seconds() {

		LOGGER.debug("<--Pi4J--> GPIO Control Example ... started.");

		final GpioPinDigitalOutput pin = gpioSingleton.getIpLed();

		LOGGER.debug("--> GPIO state should be: Initially ON");

		pin.low();
		LOGGER.debug("--> GPIO state should be: OFF");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pin.high();
		LOGGER.debug("--> GPIO state should be: ON");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// turn off gpio pin #01
		pin.low();
		LOGGER.debug("--> GPIO state should be: OFF");

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// turn on gpio pin #01 for 1 second and then off
		LOGGER.debug("--> GPIO state should be: ON for only 1 second");
		pin.pulse(1000, true); // set second argument to 'true' use a blocking
								// call
		LOGGER.debug("--> GPIO state should be: OFF");
		// light will be closed... ok.. just a test.

	}

}
// END SNIPPET: control-gpio-snippet