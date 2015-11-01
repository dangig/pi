package com.summerframework.pi.pi4j;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pi4j.util.ExecUtil;

/**
 * Autowire the RelayManager to turn on or off relays.
 * 
 * calls: /home/pi/connectors/drcontrol/drcontrol.py -d DAE002h9 -v -r 3 -c off
 * 
 * Usage: relayManager.turnOn(3);
 */
@Component
public class RelayManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(RelayManager.class);

	@Autowired
	GpioSingleton gpioSingleton;

	@Value("${relayboard.serial.1}")
	private String relayBoardSerial1;

	@Value("${relayboard.serial.2}")
	private String relayBoardSerial2;

	private AtomicBoolean r11 = new AtomicBoolean(false);
	private AtomicBoolean r12 = new AtomicBoolean(false);
	private AtomicBoolean r13 = new AtomicBoolean(false);
	private AtomicBoolean r14 = new AtomicBoolean(false);

	private AtomicBoolean r21 = new AtomicBoolean(false);
	private AtomicBoolean r22 = new AtomicBoolean(false);
	private AtomicBoolean r23 = new AtomicBoolean(false);
	private AtomicBoolean r24 = new AtomicBoolean(false);

	public void turnRelayState(int relayBoard, int relayNumber, boolean onOrOffBoolean) {
		// LOGGER.debug("Turning On " + relayNumber);

		switch (relayBoard) {
		case 1:
			// RELAY BOARD #1
			switch (relayNumber) {
			case 1:
				r11.set(onOrOffBoolean);
				if (gpioSingleton.isRunningOnPi()) {
					// Set relay 1
					try {
						if (onOrOffBoolean) {
							ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial1 + " -v -r 1 -c on");
						} else {
							ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial1 + " -v -r 1 -c off");
						}
					} catch (IOException | InterruptedException e) {
						throw new IllegalStateException(e);
					}
				}
				break;
			case 2:
				r12.set(onOrOffBoolean);
				if (gpioSingleton.isRunningOnPi()) {
					// Set relay 2
					try {
						if (onOrOffBoolean) {
							ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial1 + " -v -r 2 -c on");
						} else {
							ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial1 + " -v -r 2 -c off");
						}
					} catch (IOException | InterruptedException e) {
						throw new IllegalStateException(e);
					}
				}
				break;
			case 3:
				r13.set(onOrOffBoolean);
				if (gpioSingleton.isRunningOnPi()) {
					// Set relay 3
					try {
						if (onOrOffBoolean) {
							ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial1 + " -v -r 3 -c on");
						} else {
							ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial1 + " -v -r 3 -c off");
						}
					} catch (IOException | InterruptedException e) {
						throw new IllegalStateException(e);
					}
				}
				break;
			case 4:
				r14.set(onOrOffBoolean);
				if (gpioSingleton.isRunningOnPi()) {
					// Set relay 4
					try {
						if (onOrOffBoolean) {
							ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial1 + " -v -r 4 -c on");
						} else {
							ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial1 + " -v -r 4 -c off");
						}
					} catch (IOException | InterruptedException e) {
						throw new IllegalStateException(e);
					}
				}
				break;

			default:
				LOGGER.error("Invalid relay number: " + relayNumber);
				break;
			}

			break;

		case 2:
			// RELAY BOARD #2
			switch (relayNumber) {
			case 1:
				r21.set(onOrOffBoolean);
				if (gpioSingleton.isRunningOnPi()) {
					// Set relay 1
					try {
						if (onOrOffBoolean) {
							ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial2 + " -v -r 1 -c on");
						} else {
							ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial2 + " -v -r 1 -c off");
						}
					} catch (IOException | InterruptedException e) {
						throw new IllegalStateException(e);
					}
				}
				break;
			case 2:
				r22.set(onOrOffBoolean);
				if (gpioSingleton.isRunningOnPi()) {
					// Set relay 2
					try {
						if (onOrOffBoolean) {
							ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial2 + " -v -r 2 -c on");
						} else {
							ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial2 + " -v -r 2 -c off");
						}
					} catch (IOException | InterruptedException e) {
						throw new IllegalStateException(e);
					}
				}
				break;
			case 3:
				r23.set(onOrOffBoolean);
				if (gpioSingleton.isRunningOnPi()) {
					// Set relay 3
					try {
						if (onOrOffBoolean) {
							ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial2 + " -v -r 3 -c on");
						} else {
							ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial2 + " -v -r 3 -c off");
						}
					} catch (IOException | InterruptedException e) {
						throw new IllegalStateException(e);
					}
				}
				break;
			case 4:
				r24.set(onOrOffBoolean);
				if (gpioSingleton.isRunningOnPi()) {
					// Set relay 4
					try {
						if (onOrOffBoolean) {
							ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial2 + " -v -r 4 -c on");
						} else {
							ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial2 + " -v -r 4 -c off");
						}
					} catch (IOException | InterruptedException e) {
						throw new IllegalStateException(e);
					}
				}
				break;

			default:
				LOGGER.error("Invalid relay number: " + relayNumber);
				break;
			}

			break;

		default:
			LOGGER.error("Invalid relay board number: " + relayBoard);
			break;

		}

	}

	public void turnRelayStateForAllRelays(int relayBoard, boolean onOrOffBoolean) {
		// LOGGER.debug("Turning On " + relayNumber);

		switch (relayBoard) {
		case 1:
			// RELAY BOARD #1
			r11.set(onOrOffBoolean);
			r12.set(onOrOffBoolean);
			r13.set(onOrOffBoolean);
			r14.set(onOrOffBoolean);
			if (gpioSingleton.isRunningOnPi()) {
				// Set all relays
				try {
					if (onOrOffBoolean) {
						ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial1 + " -v -r all -c on");
					} else {
						ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial1 + " -v -r all -c off");
					}
				} catch (IOException | InterruptedException e) {
					throw new IllegalStateException(e);
				}
			}

			break;

		case 2:
			// RELAY BOARD #2
			r21.set(onOrOffBoolean);
			r22.set(onOrOffBoolean);
			r23.set(onOrOffBoolean);
			r24.set(onOrOffBoolean);
			if (gpioSingleton.isRunningOnPi()) {
				// Set all relays
				try {
					if (onOrOffBoolean) {
						ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial2 + " -v -r all -c on");
					} else {
						ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial2 + " -v -r all -c off");
					}
				} catch (IOException | InterruptedException e) {
					throw new IllegalStateException(e);
				}
			}

			break;

		default:
			LOGGER.error("Invalid relay board number: " + relayBoard);
			break;

		}

	}

	public AtomicBoolean getR11() {
		return r11;
	}

	public AtomicBoolean getR12() {
		return r12;
	}

	public AtomicBoolean getR13() {
		return r13;
	}

	public AtomicBoolean getR14() {
		return r14;
	}

	public AtomicBoolean getR21() {
		return r21;
	}

	public AtomicBoolean getR22() {
		return r22;
	}

	public AtomicBoolean getR23() {
		return r23;
	}

	public AtomicBoolean getR24() {
		return r24;
	}

}
