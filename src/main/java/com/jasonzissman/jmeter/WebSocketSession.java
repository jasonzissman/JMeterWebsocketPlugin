package com.jasonzissman.jmeter;

import java.io.IOException;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.websocket.WebSocket;
import com.ning.http.client.websocket.WebSocketByteListener;
import com.ning.http.client.websocket.WebSocketUpgradeHandler;

import com.jasonzissman.jmeter.WebSocketSessionResults;

public class WebSocketSession {
	
	private int testUpdateIntervalMs = 20;
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

		results.addToLogOfActivities("Connecting to: '" + url +"'");
		AsyncHttpClient httpClient = new AsyncHttpClient();

		try {
			ListenableFuture<WebSocket> webSocketFuture = createWebSocketFuture(httpClient);
			websocket = webSocketFuture.get();

			int accruedTimeMs = waitForSessionToFinish();
			results.addToLogOfActivities("Test duration: " + accruedTimeMs + "ms");
			
			if (results.didReceivedResponseToEndComm() == false && results.wasSuccessful() == true){
				results.setTimedOutWhileWaitingForResponse(true);
				results.addToLogOfActivities("Never received response: '" + responseToEndComm + "'");
			}
			
		} catch (Exception ex){
			results.addToLogOfActivities("Error occurred: " + ex.getLocalizedMessage());
			results.setSuccessful(false);
			ex.printStackTrace();
			closeConnection();
		}
		
		httpClient.close();
	}

	protected int waitForSessionToFinish() throws InterruptedException {
		// TODO - should synchronize class variables
		int accruedTimeMs = 0;
		while(isSessionDone == false && accruedTimeMs < responseTimeout){
			Thread.sleep(testUpdateIntervalMs);
			accruedTimeMs += testUpdateIntervalMs;
		}
		results.setAccruedTestTime(accruedTimeMs);
		return accruedTimeMs;
	}

	protected ListenableFuture<WebSocket> createWebSocketFuture(AsyncHttpClient httpClient) throws IOException {
		WebSocketByteListener webSocketListener = createWebSocketListener();
		WebSocketUpgradeHandler.Builder builder = new WebSocketUpgradeHandler.Builder();
		BoundRequestBuilder boundRequestBuilder = httpClient.prepareGet(url);
		
		WebSocketUpgradeHandler upgradeHandler = builder.addWebSocketListener(webSocketListener).build();
		ListenableFuture<WebSocket> webSocketFuture;
		
		webSocketFuture = boundRequestBuilder.execute(upgradeHandler);
		return webSocketFuture;
	}
	
	public WebSocketByteListener createWebSocketListener(){
		WebSocketByteListener webSocketByteListener = new WebSocketByteListener() {

			public void onOpen(WebSocket websocket) {
				results.addToLogOfActivities("Connection opened.");
				results.addToLogOfActivities("Connection will close on timeout (" + responseTimeout + "ms) or when the following is received: '" + responseToEndComm + "'");
				results.addToLogOfActivities("Sending message: '" + messageToSend + "'");
				websocket.sendMessage(messageToSend.getBytes());
			}

			public void onClose(WebSocket websocket) {
				results.addToLogOfActivities("Connection closed.");
				isSessionDone = true;
			}

			public void onError(Throwable t) {
				results.addToLogOfActivities("Error occurred: " + t.getLocalizedMessage());
				results.setSuccessful(false);
				t.printStackTrace();
				closeConnection();
			}

			public void onMessage(byte[] message) {
				String messageStr = new String(message);
				results.addToDataReceivedFromServer(messageStr);
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
