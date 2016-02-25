package com.summerframework.pi.services;

import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.summerframework.pi.i2c.HTU21DF;
import com.summerframework.pi.i2c.SSD1306;
import com.summerframework.pi.jpa.model.HumidityLog;
import com.summerframework.pi.jpa.model.HumidityLogRepository;

/**
 * Reads temperature and humidity from HTU21DF sensor and displays it on the
 * SSD1306 oled display.
 * 
 * Logs to database for monitoring
 */

//TODO: Uncomment if using this service
// @Service
public class HumidityReaderService {

	private static final Logger LOGGER = LoggerFactory.getLogger(HumidityReaderService.class);

	@Autowired
	ErrorHandler errorHandler;

	@Autowired
	private HumidityLogRepository humidityLogRepository;

	HTU21DF htu21df = new HTU21DF();

	SSD1306 ssd1306 = new SSD1306();

	@PostConstruct
	public void init() {
		// Init humidity sensor
		try {
			if (!htu21df.begin()) {
				LOGGER.error("Sensor htu21df not found!");
				System.exit(1);
			}
		} catch (Exception e) {
			LOGGER.error("An error occurred initializing sensor htu21df", e);
			System.exit(1);
		}

		// Init oled display
		try {
			ssd1306.begin();
		} catch (Exception e) {
			LOGGER.error("An error occurred initializing oled display", e);
			System.exit(1);
		}
	}

	public void processHumidityAction(String executionId) {
		LOGGER.debug("In HumidityReaderService.");

		// Read humidity and temperature
		final NumberFormat NF = new DecimalFormat("##00.0");
		float temperature = 0;
		float humidity = 0;
		try {
			humidity = htu21df.readHumidity();
		} catch (Exception e) {
			LOGGER.error("An error occurred reading humidity", e);
		}

		try {
			temperature = htu21df.readTemperature();
		} catch (Exception e) {
			LOGGER.error("An error occurred reading temperature", e);
		}

		LOGGER.debug("Temperature: " + NF.format(temperature) + " C");
		LOGGER.debug("Humidity   : " + NF.format(humidity) + " %");

		// Prepare humitity log to save to DB later
		long now = new Date().getTime();
		HumidityLog humidityLog = new HumidityLog();
		humidityLog.setTimestamp(now);
		humidityLog.setTemperature(new BigDecimal(temperature));
		humidityLog.setHumidity(new BigDecimal(humidity));

		// Save humidityLog to database.
		humidityLogRepository.save(humidityLog);

		// Display on oled display
		try {

			ssd1306.clear();
			ssd1306.getGraphics().clearRect(0, 0, 127, 63);
			ssd1306.getGraphics().setColor(Color.WHITE);
			ssd1306.getGraphics().setFont(new Font("Monospaced", Font.PLAIN, 12));
			ssd1306.getGraphics().drawRect(0, 0, 127, 63);
			SimpleDateFormat sdf = new SimpleDateFormat("MMM d, HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
			ssd1306.getGraphics().drawString(sdf.format(new Date()), 9, 20);

			ssd1306.getGraphics().setFont(new Font("Monospaced", Font.PLAIN, 16));
			ssd1306.getGraphics().drawLine(0, 32, 127, 32);
			ssd1306.getGraphics().drawLine(63, 32, 63, 63);

			ssd1306.getGraphics().drawString(NF.format(temperature) + " C", 2, 55);
			ssd1306.getGraphics().drawString(NF.format(humidity) + " %", 66, 55);

			ssd1306.displayImage();

		} catch (Exception e) {
			LOGGER.error("An error occurred displaying humidity to oled display", e);
		}

	}

}
