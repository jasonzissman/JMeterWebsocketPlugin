package com.jasonzissman.jmeter;

import static org.junit.Assert.*;

import org.junit.Test;

public class WebSocketSessionExpandedTests {

	@Test
	public void testTimeoutDueToNeverReceivingExpectedResponse() {
		String url = "ws://echo.websocket.org";
		String messageToSend = "Please echo me back!";
		int connTimeout = 1000;
		int responseTimeout = 1000;
		String responseToEndComm = "THIS_WILL_NEVER_BE_SENT_BY_WEB_SOCKET_SERVER";

		WebSocketSession session = new WebSocketSession(url, connTimeout, responseTimeout, messageToSend,
				responseToEndComm);
		session.executeSession();

		WebSocketSessionResults sessionResults = session.getSessionResults();
		String logOfActivity = sessionResults.getLogOfActivity();
		assertTrue(sessionResults.didTimeOutWhileWaitingForResponse());
		assertFalse(sessionResults.didReceivedResponseToEndComm());
		assertTrue(sessionResults.wasSuccessful());
		assertTrue(logOfActivity.contains("Connecting to: '" + url + "'"));
		assertTrue(logOfActivity.contains("Connection opened"));
		assertTrue(logOfActivity.contains("Connection will close on timeout (" + responseTimeout
				+ "ms) or when the following is received: '" + responseToEndComm + "'"));
		assertTrue(logOfActivity.contains("Sending message: '" + messageToSend + "'"));
		assertTrue(logOfActivity.contains("Message received: '" + messageToSend + "'"));
		assertTrue(logOfActivity.contains("Never received response: '" + responseToEndComm + "'"));
		assertTrue(logOfActivity.contains("Test duration: "));
	}
}
