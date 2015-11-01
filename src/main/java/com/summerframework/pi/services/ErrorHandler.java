package com.summerframework.pi.services;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ErrorHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandler.class);

	public void handleError(String message, Throwable exception) {
		Date timestamp = new Date();
		LOGGER.error("Timestamp: " + timestamp + ", Reference number: " + timestamp.getTime() + " Message: " + message);
		performErrorHandling(timestamp.getTime(), message);
	}

	public void handleError(String message) {
		Date timestamp = new Date();
		LOGGER.error("Timestamp: " + timestamp + ", Reference number: " + timestamp.getTime() + " Message: " + message);
		performErrorHandling(timestamp.getTime(), message);
	}

	private void performErrorHandling(long timestamp, String message) {
		// Add other error handling here if needed.

	}

}
