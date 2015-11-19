package com.summerframework.pi.websockets;

import java.util.List;

import com.summerframework.pi.jpa.model.TemperatureLog;

public class ChartResponse {

	private String content;

	private List<TemperatureLog> temperatureLogs;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<TemperatureLog> getTemperatureLogs() {
		return temperatureLogs;
	}

	public void setTemperatureLogs(List<TemperatureLog> temperatureLogs) {
		this.temperatureLogs = temperatureLogs;
	}

}
