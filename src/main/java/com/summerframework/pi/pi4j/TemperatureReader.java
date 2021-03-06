package com.summerframework.pi.pi4j;

import java.io.IOException;
import java.math.BigDecimal;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pi4j.util.ExecUtil;

/**
 * Reads temperature from DS18B20 thermometers.
 */
@Component
public class TemperatureReader {

	private static final Logger LOGGER = LoggerFactory.getLogger(TemperatureReader.class);

	@Value("${temperature.command.1}")
	private String temperature1Command;

	@Value("${temperature.command.2}")
	private String temperature2Command;

	@Value("${temperature.command.3}")
	private String temperature3Command;

	@Value("${temperature.command.4}")
	private String temperature4Command;

	public BigDecimal readThermometer1() {
		// cat /sys/bus/w1/devices/28-041471b487ff/w1_slave

		if (StringUtils.isBlank(temperature1Command)) {
			LOGGER.debug("No thermometer command available for thermometer 1");
			return null;
		}

		try {
			String[] response = ExecUtil.execute(temperature1Command);

			// Parse response. Validate that line 1 ends with "YES" and if so
			// take last 5 digits of line 2, e.g. "20625" representing 20.625
			// degrees.

			if (ArrayUtils.isEmpty(response) || response.length != 2 || !"YES".equals(StringUtils.substring(response[0], response[0].length() - 3))) {
				throw new IllegalStateException("Could not read temperature 1, CRC not ok.");
			}

			return extractTemperature("1", response[1]);

		} catch (IOException | InterruptedException e) {
			throw new IllegalStateException("Could not read temperature 1", e);
		}
	}

	public BigDecimal readThermometer2() {
		// cat /sys/bus/w1/devices/28-041471b609ff/w1_slave

		if (StringUtils.isBlank(temperature2Command)) {
			LOGGER.debug("No thermometer command available for thermometer 2");
			return null;
		}

		try {
			String[] response = ExecUtil.execute(temperature2Command);

			// Parse response. Validate that line 1 ends with "YES" and if so
			// take last 5 digits of line 2, e.g. "20625" representing 20.625
			// degrees.

			if (ArrayUtils.isEmpty(response) || response.length != 2 || !"YES".equals(StringUtils.substring(response[0], response[0].length() - 3))) {
				throw new IllegalStateException("Could not read temperature 2, CRC not ok.");
			}

			return extractTemperature("2", response[1]);

		} catch (IOException | InterruptedException e) {
			throw new IllegalStateException("Could not read temperature 2", e);
		}
	}

	public BigDecimal readThermometer3() {

		if (StringUtils.isBlank(temperature3Command)) {
			LOGGER.debug("No thermometer command available for thermometer 3");
			return null;
		}

		try {
			String[] response = ExecUtil.execute(temperature3Command);

			// Parse response. Validate that line 1 ends with "YES" and if so
			// take last 5 digits of line 2, e.g. "20625" representing 20.625
			// degrees.

			if (ArrayUtils.isEmpty(response) || response.length != 2 || !"YES".equals(StringUtils.substring(response[0], response[0].length() - 3))) {
				throw new IllegalStateException("Could not read temperature 3, CRC not ok.");
			}

			return extractTemperature("3", response[1]);

		} catch (IOException | InterruptedException e) {
			throw new IllegalStateException("Could not read temperature 3", e);
		}
	}

	public BigDecimal readThermometer4() {

		if (StringUtils.isBlank(temperature4Command)) {
			LOGGER.debug("No thermometer command available for thermometer 4");
			return null;
		}

		try {
			String[] response = ExecUtil.execute(temperature4Command);

			// Parse response. Validate that line 1 ends with "YES" and if so
			// take last 5 digits of line 2, e.g. "20625" representing 20.625
			// degrees.

			if (ArrayUtils.isEmpty(response) || response.length != 2 || !"YES".equals(StringUtils.substring(response[0], response[0].length() - 3))) {
				throw new IllegalStateException("Could not read temperature 3, CRC not ok.");
			}

			return extractTemperature("4", response[1]);

		} catch (IOException | InterruptedException e) {
			throw new IllegalStateException("Could not read temperature 4", e);
		}
	}

	public BigDecimal extractTemperature(String tempId, String endingOfRawReading) {
		// Get last 10 digits, will eventually include "t=125750 or t=0".
		String temperatureStringRaw = StringUtils.substring(endingOfRawReading, endingOfRawReading.length() - 10);
		LOGGER.debug("Read temperature " + tempId + ": " + temperatureStringRaw);
		// Can be: 20625, 9875 for below 10! Assuming -1525 for -1, -10525
		// for -10

		// Can be: t=1, =25 ==> Take everything after "=". Can be: -10 take
		// everything
		String negSignIfAny;
		String temperatureInt;
		String temperatureDecimal;
		if (StringUtils.contains(temperatureStringRaw, "=")) {

			// Get everything after "="
			String temperatureAfterEqual = StringUtils.substring(temperatureStringRaw, temperatureStringRaw.indexOf("=") + 1);

			// boolean to check if that string starts with "-", if so take
			// everything after.
			String temperatureAfterEqualWithoutNegSign;
			if (StringUtils.startsWith(temperatureAfterEqual, "-")) {
				negSignIfAny = "-";
				temperatureAfterEqualWithoutNegSign = temperatureAfterEqual.substring(1); // remove
																							// negative
																							// sign
			} else {
				negSignIfAny = "";
				temperatureAfterEqualWithoutNegSign = temperatureAfterEqual; // the
																				// string
																				// is
																				// already
																				// positive
			}

			// If that result has three digits or less, they are ALL digits
			// part, having integer part being 0.
			if (temperatureAfterEqualWithoutNegSign.length() <= 3) {
				// DECIMAL
				temperatureDecimal = StringUtils.leftPad(temperatureAfterEqualWithoutNegSign, 3, "0");
				// INT being 0
				temperatureInt = "0";
			} else {
				// DECIMAL
				// Get last 3 digits
				temperatureDecimal = StringUtils.substring(temperatureAfterEqualWithoutNegSign, temperatureAfterEqualWithoutNegSign.length() - 3);

				// INT
				String temperatureIntRaw = StringUtils.removeEnd(temperatureAfterEqualWithoutNegSign, temperatureDecimal);
				temperatureInt = StringUtils.substring(temperatureIntRaw, temperatureIntRaw.indexOf("=") + 1);

			}

		} else {
			throw new IllegalStateException("Could not find t= in the string. Please validate if thermometer is plugged in.");
		}
		// Put: Integer part being: negative sign + digit (or zero) +
		// "." + decimal part.
		return new BigDecimal(negSignIfAny + temperatureInt + "." + temperatureDecimal);
	}
}
