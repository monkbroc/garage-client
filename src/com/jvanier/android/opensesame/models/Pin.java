package com.jvanier.android.opensesame.models;

public class Pin {
	
	private String pin;

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public boolean isValid() {
		return pin.length() > 0;
	}

}
