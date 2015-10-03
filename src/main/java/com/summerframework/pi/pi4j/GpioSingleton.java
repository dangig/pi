package com.summerframework.pi.pi4j;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.system.NetworkInfo;
import com.pi4j.system.SystemInfo;

@Component
public class GpioSingleton {

	private static final Logger LOGGER = LoggerFactory.getLogger(GpioSingleton.class);

	@Value("${pi.serial.number}")
	private String piSerialNumber;

	private GpioController gpio;
	
	private GpioPinDigitalOutput ipLed;

	@PostConstruct
	public void setUp() {
		LOGGER.debug("Setup of gpio controller. (A single one for the duration of the app!).");
		if (isRunningOnPi()) {
			if (gpio == null) {
				gpio = GpioFactory.getInstance();

				// Register the listener here to have the button telling us IP
				// address.
				registerIpButtonAndIpLed();
			}
		}
	}

	/**
	 * Checks if we're running on the Pi environment or on development
	 * environment.
	 * 
	 * @return true if running on Pi with the provided serial number
	 */
	public boolean isRunningOnPi() {
		try {
			if (piSerialNumber.equalsIgnoreCase(SystemInfo.getSerial())) {
				return true;
			}
		} catch (Exception e) {
			LOGGER.warn("Not running on Raspberry Pi.", e);
		}
		return false;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public GpioController getGpioControllerInstance() {
		if (gpio == null) {
			setUp();
		}
		if (gpio == null) {
			if (isRunningOnPi()) {
				throw new IllegalStateException("Invalid state for GPIO Controller. Should not happen. If this happens contact support.");
			} else {
				throw new IllegalStateException("Not running on Pi. Change your code so that it calls isRunningOnPi() before calling gpio operations.");
			}
		}
		return gpio;
	}

	/**
	 * Call this ONLY if application shuts down. Not during operations.
	 */
	public void shutdownGpioController() {
		if (gpio != null) {

			// stop all GPIO activity/threads by shutting down the GPIO
			// controller
			// (this method will forcefully shutdown all GPIO monitoring threads
			// and
			// scheduled tasks)
			gpio.shutdown();
			gpio = null;
		} else {
			LOGGER.error("GPIO controller already shutdown.");
		}

	}

	private void registerIpButtonAndIpLed() {
		// Registering the listener on the button to give IP address
		// last digit (blinking, IP last digit = 100 + NUMBER OF BLINKS)
		// Call this only once.

		// provision gpio pin #02 (board pin #27) as an input pin with
		// its internal pull down resistor enabled.
		final GpioPinDigitalInput ipButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);

		// provision gpio pin #27 (board pin #16) as an output pin and
		// turn on
		ipLed = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27, "MyLED", PinState.HIGH);
		
		// set shutdown state for this pin
		ipLed.setShutdownOptions(true, PinState.LOW);

		// create and register gpio pin listener
		ipButton.addListener(new GpioPinListenerDigital() {

			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {

				// synchronize block in case button is pushed twice ...
				synchronized (ipButton) {

					int ipAddressMinus100;
					// Read IP address
					try {
						String[] ipAddresses = NetworkInfo.getIPAddresses();
						// Take first one, else, return 0 (not on
						// network)
						if (ipAddresses != null) {

							if (ipAddresses.length > 0) {
								// take first.
								String ipAddress = ipAddresses[0];
								LOGGER.debug("IP address is " + ipAddress);
								String ipAddressLastDigit = ipAddress.substring(ipAddresses[0].lastIndexOf(".") + 1);
								try {
									int ipAddressLastDigitInt = Integer.parseInt(ipAddressLastDigit);
									LOGGER.debug("IP address last digit is: " + ipAddressLastDigitInt);
									if (ipAddressLastDigitInt >= 100) {
										ipAddressMinus100 = ipAddressLastDigitInt - 100;
									} else {
										ipAddressMinus100 = -1;
									}

								} catch (NumberFormatException nfe) {
									LOGGER.error("IP address cannot be parsed", nfe);
									ipAddressMinus100 = -1;
								}
							} else {
								ipAddressMinus100 = -1;
							}

						} else {
							ipAddressMinus100 = -1;
						}
					} catch (IOException e) {
						LOGGER.error("An error occurred", e);
						ipAddressMinus100 = -1;
					} catch (InterruptedException e) {
						LOGGER.error("An error occurred", e);
						ipAddressMinus100 = -1;
					}

					// Make the LED blink. If -1, light for 10 seconds.
					// If 0, light once for 3 seconds. If not, blink for
					// that amount of times.
					
					ipLed.low();
					threadSleep(2000);
					if (ipAddressMinus100 < 0) {
						LOGGER.debug("IP. ipAddressMinus100 is -1... An error occurred. Lighting for 10 s.");
						ipLed.pulse(10000, true); // set second argument
													// to 'true' use a
													// blocking call
					} else if (ipAddressMinus100 == 0) {
						LOGGER.debug("IP. ipAddressMinus100 is 0... Lighting for 3 s.");
						ipLed.pulse(3000, true);
					} else if (ipAddressMinus100 > 0) {
						LOGGER.debug("IP. ipAddressMinus100 is " + ipAddressMinus100);
						for (int i = 0; i < ipAddressMinus100; i++) {
							LOGGER.debug("IP. Pulse #" + (i + 1));
							ipLed.pulse(250, true);
							threadSleep(250);						}
					}

				}

			}
		});
	}

	public GpioPinDigitalOutput getIpLed() {
		return ipLed;
	}
	
	private void threadSleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			LOGGER.error("Thread.sleep interupted.");
		}
	}
}
