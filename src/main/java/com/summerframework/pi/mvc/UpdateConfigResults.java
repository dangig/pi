package com.summerframework.pi.mvc;

import java.util.ArrayList;

public class UpdateConfigResults {

	private boolean success;

	private ArrayList<String> errors;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public ArrayList<String> getErrors() {
		return errors;
	}

	public void setErrors(ArrayList<String> errors) {
		this.errors = errors;
	}

}
