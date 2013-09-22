package com.jvanier.android.opensesame.models;

public class HistoryItem {
	private boolean up;
	private String message;
	private String since;
	
	public boolean isUp() {
		return up;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSince() {
		return since;
	}

	public void setSince(String since) {
		this.since = since;
	}

	@Override
	public String toString() {
		return message;
	}
}
