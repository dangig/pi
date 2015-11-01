package com.summerframework.pi;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.summerframework.pi.services.ErrorHandler;
import com.summerframework.pi.services.KeezerProcessingService;

public class ScheduledTrigger {

	@Autowired
	ErrorHandler errorHandler;

	private AtomicBoolean springContextInitialized = new AtomicBoolean(false);

	private AtomicBoolean alreadyInAnExecution = new AtomicBoolean(false);

	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTrigger.class);

	@Autowired
	private KeezerProcessingService keezerProcessingService;

	public void process() {
		if (!isSpringContextInitialized()) {
			LOGGER.warn("***** Waiting for Spring context to be ready before starting *****");
			return;
		}
		if (isAlreadyInAnExecution()) {
			LOGGER.warn("***** Already in an execution. Skipping this run. *****");
			return;
		}
		LOGGER.debug("***** PROCESSING *****");
		this.setAlreadyInAnExecution(true);

		try {

			String executionId = "" + new Date();

			// Scheduled services here.
			keezerProcessingService.processKeezerAction(executionId);

		} catch (Exception e) {
			// Catch if something goes wrong (e.g. server down or something) and
			// handle appropriately, for example by sending email.
			LOGGER.error("An unexpected error has occurred.", e);

			// Other handling here.
			errorHandler.handleError(e.getMessage());

		} finally {
			this.setAlreadyInAnExecution(false);
			LOGGER.debug("**** FINISHED PROCESSING *****");
		}
	}

	public boolean isSpringContextInitialized() {
		return springContextInitialized.get();
	}

	public void setSpringContextInitialized(boolean springContextInitialized) {
		this.springContextInitialized.set(springContextInitialized);
	}

	public boolean isAlreadyInAnExecution() {
		return alreadyInAnExecution.get();
	}

	public void setAlreadyInAnExecution(boolean alreadyInAnExecution) {
		this.alreadyInAnExecution.set(alreadyInAnExecution);
	}

	@PostConstruct
	public void postConstruct() {
		setSpringContextInitialized(true);
	}

}
