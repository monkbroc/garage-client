package com.jvanier.android.opensesame.tasks;

import java.io.IOException;

import org.json.JSONException;

import com.jvanier.android.opensesame.models.Challenge;
import com.jvanier.android.opensesame.models.Pin;
import com.jvanier.android.opensesame.net.SendAnswerCommand;
import com.jvanier.android.opensesame.net.GetChallengeCommand;

public class OpenDoorTask extends NetworkTask<Void, Void, Boolean> {

	private GetChallengeCommand challengeCommand;
	private SendAnswerCommand answerCommand;
	
	private Pin pin;
	
	public OpenDoorTask(Pin pin) {
		this.pin = pin;
	}

	@Override
	public void abort() {
		super.abort();
		if (challengeCommand != null) challengeCommand.cancel();
	}
	
	@Override
	protected Boolean doNetworkAction() throws IOException, JSONException {
		challengeCommand = new GetChallengeCommand();
		Challenge challenge = challengeCommand.execute();
		
		challenge.setPin(pin);
		
		answerCommand = new SendAnswerCommand(challenge);
		return answerCommand.execute();
	}

	public Pin getPin() {
		return pin;
	}
}
