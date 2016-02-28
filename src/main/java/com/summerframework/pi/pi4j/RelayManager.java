package com.summerframework.pi.pi4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
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

	public static final String SHARED_MUTEX_FILE = "relaymutex.txt";
	
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

	@Value("${keezer.relayFreezer}")
	private int keezerRelayFreezer;

	@Value("${keezer.relayHeater}")
	private int keezerRelayHeater;
	
	public synchronized void turnRelayState(int relayBoard, int relayNumber, boolean onOrOffBoolean) {
		// call to drcontrol is not thread safe (tested). Need to hold shared
		// mutex.

		LOGGER.debug("About to aquire lock to make a change to a relay");

		try {
			File file = new File(SHARED_MUTEX_FILE);
			FileChannel channel = new RandomAccessFile(file, "rw").getChannel();

			// Use the file channel to create a lock on the file.
			// This method blocks until it can retrieve the lock.
			FileLock lock = channel.lock();

			// perform operation
			turnRelayStatePrivate(relayBoard, relayNumber, onOrOffBoolean);

			// Release the lock - if it is not null!
			if (lock != null) {
				lock.release();
			}

			// Close the file
			channel.close();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

	}
	
	private void turnRelayStatePrivate(int relayBoard, int relayNumber, boolean onOrOffBoolean) {
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
						String command;
						if (onOrOffBoolean) {
							command = "/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial1 + " -v -r 1 -c on";
						} else {
							command = "/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial1 + " -v -r 1 -c off";
						}
						LOGGER.debug("About to call this command: " + command);
						String[] response = ExecUtil.execute(command);
						LOGGER.debug("Command completed. Result follows.");
						checkResponse(response);
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
						String command;
						if (onOrOffBoolean) {
							command = "/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial1 + " -v -r 2 -c on";
						} else {
							command = "/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial1 + " -v -r 2 -c off";
						}
						LOGGER.debug("About to call this command: " + command);
						String[] response = ExecUtil.execute(command);
						LOGGER.debug("Command completed. Result follows.");
						checkResponse(response);
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
							String[] response = ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial1 + " -v -r 3 -c on");
							checkResponse(response);
						} else {
							String[] response = ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial1 + " -v -r 3 -c off");
							checkResponse(response);
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
							String[] response = ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial1 + " -v -r 4 -c on");
							checkResponse(response);
						} else {
							String[] response = ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial1 + " -v -r 4 -c off");
							checkResponse(response);
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
							String[] response = ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial2 + " -v -r 1 -c on");
							checkResponse(response);
						} else {
							String[] response = ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial2 + " -v -r 1 -c off");
							checkResponse(response);
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
							String[] response = ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial2 + " -v -r 2 -c on");
							checkResponse(response);
						} else {
							String[] response = ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial2 + " -v -r 2 -c off");
							checkResponse(response);
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
							String[] response = ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial2 + " -v -r 3 -c on");
							checkResponse(response);
						} else {
							String[] response = ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial2 + " -v -r 3 -c off");
							checkResponse(response);
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
							String[] response = ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial2 + " -v -r 4 -c on");
							checkResponse(response);
						} else {
							String[] response = ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial2 + " -v -r 4 -c off");
							checkResponse(response);
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

	/**
	 * Do not use, as other applications can use other relays.
	 * 
	 * @param relayBoard
	 * @param onOrOffBoolean
	 * 
	 * @deprecated
	 */
	public synchronized void turnRelayStateForAllRelays(int relayBoard, boolean onOrOffBoolean) {
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
						String[] response = ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial1 + " -v -r all -c on");
						checkResponse(response);
					} else {
						String[] response = ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial1 + " -v -r all -c off");
						checkResponse(response);
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
						String[] response = ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial2 + " -v -r all -c on");
						checkResponse(response);
					} else {
						String[] response = ExecUtil.execute("/home/pi/connectors/drcontrol/drcontrol.py -d " + relayBoardSerial2 + " -v -r all -c off");
						checkResponse(response);
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

	/**
	 * Throws IllegalStateException if relay cannot be turned on/off
	 */
	private void checkResponse(String[] denkoviResponse) {

		LOGGER.debug("DR response: " + denkoviResponse);

		if (denkoviResponse != null) {
			for (String responseLine : denkoviResponse) {
				LOGGER.debug("DR --> " + responseLine);
			}
		}

		// Last line of response must start with 'Relay' (e.g. "Relay 3 to ON"
		// or "Relay all to OFF", etc.)
		if (denkoviResponse == null || denkoviResponse.length < 1) {
			throw new IllegalStateException("Unable to get a response from Denkovi!");
		}

		LOGGER.debug("Does last line start with 'Relay'? : " + StringUtils.startsWith(denkoviResponse[denkoviResponse.length - 1], "Relay"));
		if (!StringUtils.startsWith(denkoviResponse[denkoviResponse.length - 1], "Relay")) {
			throw new IllegalStateException("Problem getting response from Denkovi: " + denkoviResponse[denkoviResponse.length - 1]);
		}

	}

	public AtomicBoolean getRelayFreezer() {
		// Just return the state of the relay for freezer
		if (keezerRelayFreezer == 1) {
			return r11;
		} else if (keezerRelayFreezer == 3) {
			return r13;
		} else {
			throw new IllegalStateException("Freezer must be on relay 1 or 3.");
		}
	}
	
	public AtomicBoolean getRelayHeater() {
		// Just return the state of the relay for heater
		if (keezerRelayHeater == 2) {
			return r12;
		} else if (keezerRelayHeater == 4) {
			return r14;
		} else {
			throw new IllegalStateException("Heater must be on relay 2 or 4.");
		}
	}

}
