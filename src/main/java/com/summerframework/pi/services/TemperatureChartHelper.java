package com.summerframework.pi.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.summerframework.pi.jpa.model.TemperatureLog;
import com.summerframework.pi.jpa.model.TemperatureLogRepository;

@Service
public class TemperatureChartHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(TemperatureChartHelper.class);

	@Autowired
	private TemperatureLogRepository temperatureLogRepository;

	/**
	 * Called by MVC controller, requested from user.
	 * 
	 * @return
	 */
	public List<TemperatureLog> getAllData() {
		Iterable<TemperatureLog> allData = temperatureLogRepository.findAll();
		return processData(allData.iterator());
	}

	/**
	 * Called by web socket service, to push this to user when new temperatures
	 * are available.
	 * 
	 * @return
	 */
	public List<TemperatureLog> getLastHourData(long timestampNow) {
		long timestampOneHourAgo = timestampNow - 60 * 60 * 1000;
		Iterable<TemperatureLog> lastHourData = temperatureLogRepository.findByTimestampGreaterThanEqual(timestampOneHourAgo);
		return processData(lastHourData.iterator());
	}

	private List<TemperatureLog> processData(Iterator<TemperatureLog> iterator) {
		if (iterator == null) {
			throw new NullPointerException("Iterator must not be null");
		}
		List<TemperatureLog> list = new ArrayList<TemperatureLog>(1000);
		TemperatureLog previousTemperatureLogRef = null;
		while (iterator.hasNext()) {
			TemperatureLog currentTemperatureLog = iterator.next();

			// Discontinuous line correction:
			// If current timestamp minus previous timestamp > 120
			// seconds, insert nulls at previous timestamp + 10s. This is to
			// break the line on google chart, avoiding meaningless continuous
			// line for a long period of idle time.
			if (previousTemperatureLogRef != null) {
				if (currentTemperatureLog.getTimestamp() - previousTemperatureLogRef.getTimestamp() > (120*1000)) {
					TemperatureLog aNullTemperatureLog = new TemperatureLog();
					aNullTemperatureLog.setTimestamp(previousTemperatureLogRef.getTimestamp() + 10*1000);
					// All other fields being null
					list.add(aNullTemperatureLog);
				}
			}
			
			// Add to list
			list.add(currentTemperatureLog);
			
			// used for next iteration
			previousTemperatureLogRef = currentTemperatureLog;
		}
		return list;
	}

}
