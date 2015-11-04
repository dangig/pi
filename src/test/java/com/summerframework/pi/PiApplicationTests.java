package com.summerframework.pi;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PiApplication.class)
@WebAppConfiguration
public class PiApplicationTests {

	private static final Logger LOGGER = LoggerFactory.getLogger(PiApplicationTests.class);
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Test
	public void contextLoads() {
	}

	@Test
	public void someTempDates() {
		String date1 = "2015-10-29 11:00:00";
		String date2 = "2015-11-03 13:00:00";
		String date3 = "2015-11-03 14:00:00";
		//String date3p = "2015-11-09 14:00:00";
		String date4 = "2015-11-23 15:00:00";

		try {
			LOGGER.debug(date1 + ": " + sdf.parse(date1).getTime());
			LOGGER.debug(date2 + ": " + sdf.parse(date2).getTime());
			LOGGER.debug(date3 + ": " + sdf.parse(date3).getTime());
			//LOGGER.debug(date3p + ": " + sdf.parse(date3p).getTime());
			LOGGER.debug(date4 + ": " + sdf.parse(date4).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
