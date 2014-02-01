package com.jasonzissman.jmeter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WebSocketSessionResults {
	
	private boolean successful = true;
	private boolean receivedResponseToEndComm = false;
	private boolean timedOutWhileWaitingForResponse = false;
	private int accruedTime = 0;
	private String logOfActivity = "";
	
	public boolean didReceivedResponseToEndComm() {
		return receivedResponseToEndComm;
	}
	
	public void setReceivedResponseToEndComm(boolean receivedResponseToEndComm) {
		this.receivedResponseToEndComm = receivedResponseToEndComm;
	}
	
	public boolean didTimeOutWhileWaitingForResponse() {
		return timedOutWhileWaitingForResponse;
	}
	
	public void setTimedOutWhileWaitingForResponse(boolean timedOut) {
		this.timedOutWhileWaitingForResponse = timedOut;
	}

	public String getLogOfActivity() {
		return logOfActivity;
	}

	public void setLogOfActivity(String logOfActivity) {
		this.logOfActivity = logOfActivity;
	}
	
	public void addToLogOfActivities(String logEntry){
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
		String timeStamp  = timeFormat.format(new Date());
		this.logOfActivity += timeStamp + ": " + logEntry + System.getProperty("line.separator");
	}
	
	public void setSuccessful(boolean successful){
		this.successful = successful;
	}
	public boolean wasSuccessful(){
		return this.successful;
	}

	public int getAccruedTestTime() {
		return accruedTime;
	}

	public void setAccruedTestTime(int accruedTestTime) {
		this.accruedTime = accruedTestTime;
	}
	
	
}
