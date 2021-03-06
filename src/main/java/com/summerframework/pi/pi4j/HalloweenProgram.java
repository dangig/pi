package com.summerframework.pi.pi4j;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pi4j.util.ExecUtil;

/**
 * Halloween 2015 program. Controls 4 pumpkins.
 * 
 * Blink eyes, some side lines, and grande finale.
 */
@Component
public class HalloweenProgram {

	private static final Logger LOGGER = LoggerFactory.getLogger(HalloweenProgram.class);

	// Uncomment the next line to have this start automatically during
	// Halloween.
	// @PostConstruct
	public void infiniteLoop() {

		// Here we define the infinite Halloween program, flashing 4 lights (4
		// relays of the relay board)

		while (true) {

			newProgram1();

		}

	}

	private void newProgram1() {
		try {
			ExecUtil.execute("/home/pi/connectors/drcontrol/drcontroldg.py -d DAE002h9 -v -r program1 -c on ");
		} catch (IOException | InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

}
