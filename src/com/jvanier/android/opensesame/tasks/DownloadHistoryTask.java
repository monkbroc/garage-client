package com.jvanier.android.opensesame.tasks;

import java.io.IOException;

import org.json.JSONException;

import com.jvanier.android.opensesame.models.HistoryList;
import com.jvanier.android.opensesame.net.DownloadHistoryCommand;

public class DownloadHistoryTask extends NetworkTask<Void, Void, HistoryList> {

	private DownloadHistoryCommand command;
	
	@Override
	public void abort() {
		super.abort();
		if (command != null) command.cancel();
	}
	
	@Override
	protected HistoryList doNetworkAction() throws IOException, JSONException {
		command = new DownloadHistoryCommand();
		return command.execute();
	}

}
