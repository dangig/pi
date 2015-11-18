package com.summerframework.pi.jpa.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;

/**
 * JPA model that represents a temperature log (one timestamp, many temperatures
 * 
 */
@Entity
public class TemperatureLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private long timestamp;

	@Column(precision = 8, scale = 4)
	private BigDecimal temperature1Keg;

	@Column(precision = 8, scale = 4)
	private BigDecimal temperature2Freezer;

	@Column(precision = 8, scale = 4)
	private BigDecimal temperature3;

	@Column(precision = 8, scale = 4)
	private BigDecimal temperature4;

	@Size(max = 4)
	private String state;

	@Column(precision = 8, scale = 4)
	private BigDecimal configTemperature1;

	@Column(precision = 8, scale = 4)
	private BigDecimal configTemperature2;

	@Column(precision = 8, scale = 4)
	private BigDecimal configTemperature3;

	@Column(precision = 8, scale = 4)
	private BigDecimal configTemperature4;

	private boolean freezer;

	private boolean heater;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public BigDecimal getTemperature1Keg() {
		return temperature1Keg;
	}

	public void setTemperature1Keg(BigDecimal temperature1Keg) {
		this.temperature1Keg = temperature1Keg;
	}

	public BigDecimal getTemperature2Freezer() {
		return temperature2Freezer;
	}

	public void setTemperature2Freezer(BigDecimal temperature2Freezer) {
		this.temperature2Freezer = temperature2Freezer;
	}

	public BigDecimal getTemperature3() {
		return temperature3;
	}

	public void setTemperature3(BigDecimal temperature3) {
		this.temperature3 = temperature3;
	}

	public BigDecimal getTemperature4() {
		return temperature4;
	}

	public void setTemperature4(BigDecimal temperature4) {
		this.temperature4 = temperature4;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public BigDecimal getConfigTemperature1() {
		return configTemperature1;
	}

	public void setConfigTemperature1(BigDecimal configTemperature1) {
		this.configTemperature1 = configTemperature1;
	}

	public BigDecimal getConfigTemperature2() {
		return configTemperature2;
	}

	public void setConfigTemperature2(BigDecimal configTemperature2) {
		this.configTemperature2 = configTemperature2;
	}

	public BigDecimal getConfigTemperature3() {
		return configTemperature3;
	}

	public void setConfigTemperature3(BigDecimal configTemperature3) {
		this.configTemperature3 = configTemperature3;
	}

	public BigDecimal getConfigTemperature4() {
		return configTemperature4;
	}

	public void setConfigTemperature4(BigDecimal configTemperature4) {
		this.configTemperature4 = configTemperature4;
	}

	public boolean isFreezer() {
		return freezer;
	}

	public void setFreezer(boolean freezer) {
		this.freezer = freezer;
	}

	public boolean isHeater() {
		return heater;
	}

	public void setHeater(boolean heater) {
		this.heater = heater;
	}

}