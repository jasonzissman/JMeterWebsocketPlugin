package com.jasonzissman.jmeter;

import static org.junit.Assert.*;

import org.junit.Test;

public class WebSocketSessionTest {

	// TODO - we need to mock the echo server so that we don't rely on an Internet connection
	
	@Test
	public void testBasicEcho() {
		String url = "ws://echo.websocket.org";
		String messageToSend = "Please echo me back!";
		int connTimeout = 1000;
		int responseTimeout = 1000;
		String responseToEndComm = messageToSend;
		
		WebSocketSession session = new WebSocketSession(url,connTimeout, responseTimeout, messageToSend, responseToEndComm);
		session.executeSession();
		
		WebSocketSessionResults sessionResults = session.getSessionResults();
		String logOfActivity = sessionResults.getLogOfActivity();
		assertFalse(sessionResults.didTimeOutWhileWaitingForResponse());
		assertTrue(sessionResults.didReceivedResponseToEndComm());
		assertTrue(sessionResults.wasSuccessful());
		assertTrue(logOfActivity.contains("Connecting to: '" + url + "'"));
		assertTrue(logOfActivity.contains("Connection opened"));
		assertTrue(logOfActivity.contains("Connection will close on timeout (" + responseTimeout + "ms) or when the following is received: '" + responseToEndComm + "'"));
		assertTrue(logOfActivity.contains("Sending message: '" + messageToSend + "'"));
		assertTrue(logOfActivity.contains("Message received: '" + messageToSend + "'"));
		assertTrue(logOfActivity.contains("Test duration: "));
		assertTrue(sessionResults.getDataReceivedFromServer().contains(responseToEndComm));
	}
	
	@Test
	public void testBadUrl() {
		String url = "ws://I_really_hope_this_sub_domain_doesnt_exist.websocket.org";
		String messageToSend = "Please echo me back!";
		int connTimeout = 1000;
		int responseTimeout = 1000;
		String responseToEndComm = messageToSend;
		
		WebSocketSession session = new WebSocketSession(url,connTimeout, responseTimeout, messageToSend, responseToEndComm);
		session.executeSession();
		
		WebSocketSessionResults sessionResults = session.getSessionResults();
		String logOfActivity = sessionResults.getLogOfActivity();
		assertFalse(sessionResults.wasSuccessful());
		assertFalse(sessionResults.didTimeOutWhileWaitingForResponse());
		assertFalse(sessionResults.didReceivedResponseToEndComm());
		assertTrue(logOfActivity.contains("Connecting to: '" + url + "'"));
		assertTrue(logOfActivity.contains("Error occurred"));
		assertTrue(logOfActivity.contains("Test duration: "));
	}

	
	
}
