package com.summerframework.pi.jpa.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * JPA model that represents a temperature log (one timestamp, many temperatures
 * 
 */
@Entity
public class HumidityLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private long timestamp;

	@Column(precision = 8, scale = 4)
	private BigDecimal humidity;

	@Column(precision = 8, scale = 4)
	private BigDecimal temperature;

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

	public BigDecimal getHumidity() {
		return humidity;
	}

	public void setHumidity(BigDecimal humidity) {
		this.humidity = humidity;
	}

	public BigDecimal getTemperature() {
		return temperature;
	}

	public void setTemperature(BigDecimal temperature) {
		this.temperature = temperature;
	}

}