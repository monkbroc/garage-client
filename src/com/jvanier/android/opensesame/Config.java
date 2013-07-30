package com.jvanier.android.opensesame;

public class Config {

    public static final String C2DM_SENDER = "999999999999"; // Your Sender id here
    public static final String C2DM_STATE_UP = "up";
	public static final String C2DM_STATE_SINCE = "since";
    
    public static final String SERVER_BASE_URL = "http://yourcomputer.dyndns.org/garage";
    public static final String SERVER_STATE_URL = SERVER_BASE_URL + "/state";
    public static final String SERVER_REGISTER_URL_TEMPLATE = SERVER_BASE_URL + "/register?id=%s";
    public static final String SERVER_OPEN_CHALLENGE_URL = SERVER_BASE_URL + "/open";
    public static final String SERVER_OPEN_ANSWER_URL_TEMPLATE = SERVER_BASE_URL + "/open?challenge=%s&answer=%s";
    public static final String SERVER_CHALLENGE = "challenge";
    public static final String SERVER_ANSWER = "answer";
    public static final String SERVER_SUCCESS = "success";
    public static final String SERVER_HISTORY_URL = SERVER_BASE_URL + "/history";
    
    public static final String INTENT_STATE_UPDATE = "com.jvanier.android.opensesame.intent.UPDATE";
    public static final String INTENT_STATE_UP = "up";
	public static final String INTENT_STATE_SINCE = "since";

    public static final String INTENT_REQUEST_UPDATE = "com.jvanier.android.opensesame.intent.REQUEST";

}
