package com.jvanier.android.opensesame.tasks;

import java.io.IOException;

import org.json.JSONException;

import com.jvanier.android.opensesame.models.GarageDoorState;
import com.jvanier.android.opensesame.net.DownloadStateCommand;

public class DownloadStateTask extends NetworkTask<Void, Void, GarageDoorState> {

	private DownloadStateCommand command;
	
	@Override
	public void abort() {
		super.abort();
		if (command != null) command.cancel();
	}
	
	@Override
	protected GarageDoorState doNetworkAction() throws IOException, JSONException {
		command = new DownloadStateCommand();
		return command.execute();
	}

}
