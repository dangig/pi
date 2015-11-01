package com.summerframework.pi.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.summerframework.pi.jpa.model.KeezerConfigRepository;
import com.summerframework.pi.jpa.model.TemperatureLogRepository;
import com.summerframework.pi.pi4j.RelayManager;
import com.summerframework.pi.pi4j.TemperatureReader;

@Service
public class KeezerProcessingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(KeezerProcessingService.class);

	@Autowired
	ErrorHandler errorHandler;

	@Autowired
	private KeezerConfigRepository keezerConfigRepository;

	@Autowired
	private TemperatureLogRepository temperatureLogRepository;

	@Autowired
	private TemperatureReader temperatureReader;
	
	@Autowired
	private RelayManager relayManager;

	public void processKeezerAction(String executionId) {
		LOGGER.debug("In KeezerProcessingService.");
		
		// TODO.
	}

}
