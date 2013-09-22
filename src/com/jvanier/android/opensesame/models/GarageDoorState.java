package com.jvanier.android.opensesame.models;

import java.util.Observable;

public class GarageDoorState extends Observable {

	private boolean up;
	private String since;
	
	public boolean isUp() {
		return up;
	}
	public void setUp(boolean up) {
		this.up = up;
		setChanged();
	}
	public String getSince() {
		return since;
	}
	public void setSince(String since) {
		this.since = since;
		setChanged();
	}

	synchronized public void consume(GarageDoorState template) {
		setUp(template.isUp());
		setSince(template.getSince());
		notifyObservers();
	}

	// Singleton
	private static GarageDoorState instance;
	
	public static GarageDoorState getInstance() {
		if (instance == null) {
			instance = new GarageDoorState();
		}
		return instance;
	}
	

}
