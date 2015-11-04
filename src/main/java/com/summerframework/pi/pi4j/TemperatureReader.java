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
			// Get last 5 digits
			String temperatureNoDot = StringUtils.substring(response[1], response[1].length() - 5);
			LOGGER.debug("Read temperature 1: " + temperatureNoDot);
			String temperatureInt = StringUtils.substring(temperatureNoDot, 0, 2);
			String temperatureDecimal = StringUtils.substring(temperatureNoDot, 2);
			return new BigDecimal(temperatureInt + "." + temperatureDecimal);
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
			// Get last 5 digits
			String temperatureNoDot = StringUtils.substring(response[1], response[1].length() - 5);
			LOGGER.debug("Read temperature 2: " + temperatureNoDot);
			String temperatureInt = StringUtils.substring(temperatureNoDot, 0, 2);
			String temperatureDecimal = StringUtils.substring(temperatureNoDot, 2);
			return new BigDecimal(temperatureInt + "." + temperatureDecimal);
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
			// Get last 5 digits
			String temperatureNoDot = StringUtils.substring(response[1], response[1].length() - 5);
			LOGGER.debug("Read temperature 3: " + temperatureNoDot);
			String temperatureInt = StringUtils.substring(temperatureNoDot, 0, 2);
			String temperatureDecimal = StringUtils.substring(temperatureNoDot, 2);
			return new BigDecimal(temperatureInt + "." + temperatureDecimal);
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
			// Get last 5 digits
			String temperatureNoDot = StringUtils.substring(response[1], response[1].length() - 5);
			LOGGER.debug("Read temperature 4: " + temperatureNoDot);
			String temperatureInt = StringUtils.substring(temperatureNoDot, 0, 2);
			String temperatureDecimal = StringUtils.substring(temperatureNoDot, 2);
			return new BigDecimal(temperatureInt + "." + temperatureDecimal);
		} catch (IOException | InterruptedException e) {
			throw new IllegalStateException("Could not read temperature 4", e);
		}
	}

}
