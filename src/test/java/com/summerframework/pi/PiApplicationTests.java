package com.summerframework.pi;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.summerframework.pi.pi4j.TemperatureReader;

//@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PiApplication.class)
@WebAppConfiguration
public class PiApplicationTests {

	private static final Logger LOGGER = LoggerFactory.getLogger(PiApplicationTests.class);
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	TemperatureReader temperatureReader;

	//@Test
	public void contextLoads() {
	}

	//@Test
	public void someTempDates() {
		String date1 = "2015-11-05 12:40:00";
		String date2 = "2015-11-05 20:00:00";
		String date3 = "2015-11-06 04:00:00";
		String date4 = "2015-11-06 11:00:00";
		
		long check = 1446747200067l;
		long check2 = 1446747150092l;
		

		try {
			LOGGER.debug(date1 + ": " + sdf.parse(date1).getTime());
			LOGGER.debug(date2 + ": " + sdf.parse(date2).getTime());
			LOGGER.debug(date3 + ": " + sdf.parse(date3).getTime());
			LOGGER.debug(date4 + ": " + sdf.parse(date4).getTime());
			
			LOGGER.debug("check: " + check + ": " + new Date(check));
			LOGGER.debug("check2: " + check2 + ": " + new Date(check2));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	//@Test
	public void parseTemps() {
		String temperatures[] = { "xyzyzt=123687", "xyzyzt=9687", "xyxyyxzt=160", "xyxyyxzt=60", "xyxyyxzt=6", "xyxyyxzt=0", "xyzyzt=123687", "xyzyzt=9687", "xyxyyxzt=160", "xyxyyxzt=60",
				"xyxyyxzt=6", "xyxyyxzt=0" };
		for (String temperatureStringRaw : temperatures) {

			LOGGER.debug("Read temperature 1: " + temperatureStringRaw);
			
			LOGGER.debug("Number " + temperatureReader.extractTemperature("X", temperatureStringRaw));

		}

	}
	
	@Test
	public void tellDate() {
		LOGGER.debug("Date when freezer turned off: "+ new Date(1446629700063l));
	}

}
