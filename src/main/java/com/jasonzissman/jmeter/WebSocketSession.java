package com.jasonzissman.jmeter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.websocket.WebSocket;
import com.ning.http.client.websocket.WebSocketByteListener;
import com.ning.http.client.websocket.WebSocketUpgradeHandler;

public class WebSocketSession {
	
	private WebSocket websocket = null;
	private boolean isSessionDone = false;
	
	private WebSocketSessionResults results = new WebSocketSessionResults();
	
	private String url = "ws://echo.websocket.org";
	private int connTimeout = 5000;
	private int responseTimeout = 5000;
	private String messageToSend = "";
	private String responseToEndComm = "[No response to terminate communication has been defined]";
	
	public WebSocketSession(String url, String messageToSend){
		this.url = url;
		this.messageToSend = messageToSend;
	}
	
	public WebSocketSession(String url, int connTimeout, int responseTimeout, String messageToSend, String responseToEndComm) {
		this.url = url;
		this.connTimeout = connTimeout;
		this.responseTimeout = responseTimeout;
		this.messageToSend = messageToSend;
		this.responseToEndComm = responseToEndComm;
	}
	
	public void executeSession() {
		AsyncHttpClient httpClient = new AsyncHttpClient();
		WebSocketByteListener webSocketListener = getWebSocketListener();
		WebSocketUpgradeHandler.Builder builder = new WebSocketUpgradeHandler.Builder();
		BoundRequestBuilder boundRequestBuilder = httpClient.prepareGet(url);
		
		WebSocketUpgradeHandler upgradeHandler = builder.addWebSocketListener(webSocketListener).build();
		ListenableFuture<WebSocket> webSocketFuture;
		try {
			results.addToLogOfActivities("Connecting to: '" + url +"'");
			webSocketFuture = boundRequestBuilder.execute(upgradeHandler);
			websocket = webSocketFuture.get();
			// TODO - should synchronize class variables
			// TODO - need to make the two timeout values less ambiguous
			int accruedTimeMs = 0;
			while(isSessionDone == false && accruedTimeMs < responseTimeout){
				Thread.sleep(100);
				accruedTimeMs += 100;
			}

			if (results.didReceivedResponseToEndComm() == false){
				results.setTimedOut(true);
			}
			
		} catch (Exception ex){
			results.addToLogOfActivities("Error occurred: " + ex.getLocalizedMessage());
			results.setSuccessful(false);
			ex.printStackTrace();
			closeConnection();
		}
		
		
		httpClient.close();
	}
	
	public WebSocketByteListener getWebSocketListener(){
		WebSocketByteListener webSocketByteListener = new WebSocketByteListener() {

			public void onOpen(WebSocket websocket) {
				results.addToLogOfActivities("Connection opened.  Sending message: '" + messageToSend + "'");
				websocket.sendMessage(messageToSend.getBytes());
			}

			public void onClose(WebSocket websocket) {
				results.addToLogOfActivities("Connection closed.");
				isSessionDone = true;
			}

			public void onError(Throwable t) {
				results.addToLogOfActivities("Error occurred: " + t.getLocalizedMessage());
				t.printStackTrace();
				closeConnection();
			}

			public void onMessage(byte[] message) {
				String messageStr = new String(message);
				results.addToLogOfActivities("Message received: '" + messageStr + "'");
				
				if (messageStr.contains(responseToEndComm)){
					results.setReceivedResponseToEndComm(true);
					closeConnection();
				}
			}

			public void onFragment(byte[] arg0, boolean arg1) {
				
			}
			
		};
		return webSocketByteListener;
	}
	
	private void closeConnection(){
		try {
			if (websocket != null && websocket.isOpen()){
				websocket.close();
			}
		} catch (Exception ex){
			results.addToLogOfActivities("Error occurred: " + ex.getLocalizedMessage());
			ex.printStackTrace();
		}
	}
	
	public WebSocketSessionResults getSessionResults(){
		return this.results;
	}
}
