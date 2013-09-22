package com.jvanier.android.opensesame.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class HistoryList extends Observable {
	private List<HistoryItem> history;
	
	public HistoryList() {
		history = new ArrayList<HistoryItem>();
	}

	public List<HistoryItem> getHistory() {
		return history;
	}

	synchronized public void add(HistoryItem historyItem) {
		history.add(historyItem);
		setChanged();
	}

	synchronized public void consume(HistoryList template) {
		history = template.getHistory();
		setChanged();
		notifyObservers();
	}
	
	
}
