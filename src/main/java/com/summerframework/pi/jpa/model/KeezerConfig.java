package com.summerframework.pi.jpa.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * JPA model that represents Keezer config.
 * 
 * date with the lowest ID (1): start date dates in between: date where config
 * will change (2-3) date with the highest ID (4): end date (no temperature
 * needed)
 * 
 * Validation to perform: There must be 4 and only 4 rows in this database.
 * 
 * Datetimes must be in ascending order: datetime of row 1 must be < datetime of
 * row 2 .... < datetime of row 4.
 * 
 * Temperatures must be in adcending order: For row 1, threshold1 must be <
 * threshhold2 which must be < threshold3
 * 
 * 
 */
@Entity
public class KeezerConfig {

	@Id
	private long id;
	private long stateChangeDatetime;
	private BigDecimal threshold1HeatStartFreezerStopTemperature; // e.g. 15 degrees
	private BigDecimal threshold2FreezerStopTemperature; // e.g. 16 degrees
	private BigDecimal threshold3HeatStopTemperature; //e.g. 17 degrees
	private BigDecimal threshold4FreezerStartHeatStopTemperature; // e.g. 18 degrees

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getStateChangeDatetime() {
		return stateChangeDatetime;
	}

	public void setStateChangeDatetime(long stateChangeDatetime) {
		this.stateChangeDatetime = stateChangeDatetime;
	}

	public BigDecimal getThreshold1HeatStartFreezerStopTemperature() {
		return threshold1HeatStartFreezerStopTemperature;
	}

	public void setThreshold1HeatStartFreezerStopTemperature(BigDecimal threshold1HeatStartFreezerStopTemperature) {
		this.threshold1HeatStartFreezerStopTemperature = threshold1HeatStartFreezerStopTemperature;
	}

	public BigDecimal getThreshold2FreezerStopTemperature() {
		return threshold2FreezerStopTemperature;
	}

	public void setThreshold2FreezerStopTemperature(BigDecimal threshold2FreezerStopTemperature) {
		this.threshold2FreezerStopTemperature = threshold2FreezerStopTemperature;
	}

	public BigDecimal getThreshold3HeatStopTemperature() {
		return threshold3HeatStopTemperature;
	}

	public void setThreshold3HeatStopTemperature(BigDecimal threshold3HeatStopTemperature) {
		this.threshold3HeatStopTemperature = threshold3HeatStopTemperature;
	}

	public BigDecimal getThreshold4FreezerStartHeatStopTemperature() {
		return threshold4FreezerStartHeatStopTemperature;
	}

	public void setThreshold4FreezerStartHeatStopTemperature(BigDecimal threshold4FreezerStartHeatStopTemperature) {
		this.threshold4FreezerStartHeatStopTemperature = threshold4FreezerStartHeatStopTemperature;
	}

}