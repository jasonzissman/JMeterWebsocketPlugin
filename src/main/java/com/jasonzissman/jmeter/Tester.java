package com.jasonzissman.jmeter;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.websocket.WebSocket;
import com.ning.http.client.websocket.WebSocketByteListener;
import com.ning.http.client.websocket.WebSocketUpgradeHandler;

public class Tester {

	static WebSocket websocket = null;
	public static void main(String[] args) throws Exception {

		AsyncHttpClient c = new AsyncHttpClient();
		websocket = c.prepareGet("ws://echo.websocket.org")
				.execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(new WebSocketByteListener() {

					public void onOpen(WebSocket websocket) {
						System.out.println("Opened.  Sending Message");
						websocket.sendMessage("Hello!".getBytes());
					}

					public void onClose(WebSocket websocket) {
						System.out.println("Closed");
					}

					public void onError(Throwable t) {
						System.out.println("Error");
						t.printStackTrace();
						websocket.close();
					}

					public void onMessage(byte[] message) {
						System.out.println("Message: " + new String(message));
						websocket.close();
					}

					public void onFragment(byte[] fragment, boolean last) {
						System.out.println("fragment?");
					}
				}).build()).get();
	}

	// static Connection connection = null;
	// public static void main(String[] args) {
	// WebSocketClientFactory webSocketClientFactory = null;
	// try {
	// webSocketClientFactory = new WebSocketClientFactory();
	// webSocketClientFactory.start();
	// WebSocketClient webSocketClient =
	// webSocketClientFactory.newWebSocketClient();
	// OnTextMessage messageHandler = new OnTextMessage() {
	// public void onMessage(String s) {
	// System.out.println("Message received: " + s);
	// connection.close();
	// }
	//
	// public void onOpen(Connection connection) {
	// System.out.println("Opening connection");
	// }
	//
	// public void onClose(int i, String s) {
	// System.out.println("Closing connection");
	// }
	// };
	//
	// // new URI()
	// // URI uri = URI("ws://echo.websocket.org");
	// URI uri = new URI("ws", null, "echo.websocket.org", -1, "", "", null);
	// Future<Connection> futureConnection = webSocketClient.open(uri,
	// messageHandler);
	// connection = futureConnection.get();
	// } catch(Exception ex){
	// ex.printStackTrace();
	// } finally{
	// try {
	// webSocketClientFactory.stop();
	// } catch (Exception e) {
	// }
	// }
	//
	// }
}
