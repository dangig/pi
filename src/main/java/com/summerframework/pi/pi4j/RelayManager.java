package com.summerframework.pi.pi4j;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pi4j.util.ExecUtil;

/**
 * Autowire the RelayManager to turn on or off relays.
 * 
 * Usage: relayManager.turnOn(3);
 */
@Component
public class RelayManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(RelayManager.class);

	@Autowired
	GpioSingleton gpioSingleton;

	private AtomicBoolean r1 = new AtomicBoolean(false);
	private AtomicBoolean r2 = new AtomicBoolean(false);
	private AtomicBoolean r3 = new AtomicBoolean(false);
	private AtomicBoolean r4 = new AtomicBoolean(false);

	public void turnOn(int relayNumber) {
		// LOGGER.debug("Turning On " + relayNumber);

		switch (relayNumber) {
		case 1:
			r1.set(true);
			if (gpioSingleton.isRunningOnPi()) {
				// Set relay 1
				try {
					ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d DAE002h9 -v -r 1 -c on ");
				} catch (IOException | InterruptedException e) {
					throw new IllegalStateException(e);
				}
			}
			break;
		case 2:
			r2.set(true);
			if (gpioSingleton.isRunningOnPi()) {
				// Set relay 2
				try {
					ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d DAE002h9 -v -r 2 -c on ");
				} catch (IOException | InterruptedException e) {
					throw new IllegalStateException(e);
				}
			}
			break;
		case 3:
			r3.set(true);
			if (gpioSingleton.isRunningOnPi()) {
				// Set relay 3
				try {
					ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d DAE002h9 -v -r 3 -c on ");
				} catch (IOException | InterruptedException e) {
					throw new IllegalStateException(e);
				}
			}
			break;
		case 4:
			r4.set(true);
			if (gpioSingleton.isRunningOnPi()) {
				// Set relay 4
				try {
					ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d DAE002h9 -v -r 4 -c on ");
				} catch (IOException | InterruptedException e) {
					throw new IllegalStateException(e);
				}
			}
			break;

		default:
			LOGGER.error("Invalid relay number: " + relayNumber);
			break;
		}

		printGlobal();

	}

	public void turnOff(int relayNumber) {
		// LOGGER.debug("Turning Off " + relayNumber);

		switch (relayNumber) {
		case 1:
			r1.set(false);
			if (gpioSingleton.isRunningOnPi()) {
				// Reset relay 1
				try {
					ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d DAE002h9 -v -r 1 -c off ");
				} catch (IOException | InterruptedException e) {
					throw new IllegalStateException(e);
				}
			}
			break;
		case 2:
			r2.set(false);
			if (gpioSingleton.isRunningOnPi()) {
				// Reset relay 2
				try {
					ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d DAE002h9 -v -r 2 -c off ");
				} catch (IOException | InterruptedException e) {
					throw new IllegalStateException(e);
				}
			}
			break;
		case 3:
			r3.set(false);
			if (gpioSingleton.isRunningOnPi()) {
				// Reset relay 3
				try {
					ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d DAE002h9 -v -r 3 -c off ");
				} catch (IOException | InterruptedException e) {
					throw new IllegalStateException(e);
				}
			}
			break;
		case 4:
			r4.set(false);
			if (gpioSingleton.isRunningOnPi()) {
				// Reset relay 4
				try {
					ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d DAE002h9 -v -r 4 -c off ");
				} catch (IOException | InterruptedException e) {
					throw new IllegalStateException(e);
				}
			}
			break;

		default:
			LOGGER.error("Invalid relay number: " + relayNumber);
			break;
		}

		printGlobal();

	}

	private void printGlobal() {
		String relayState = "";

		if (r1.get()) {
			relayState += "x";
		} else {
			relayState += " ";
		}

		if (r2.get()) {
			relayState += "x";
		} else {
			relayState += " ";
		}

		if (r3.get()) {
			relayState += "x";
		} else {
			relayState += " ";
		}

		if (r4.get()) {
			relayState += "x";
		} else {
			relayState += " ";
		}

		LOGGER.debug(relayState);
	}
}
