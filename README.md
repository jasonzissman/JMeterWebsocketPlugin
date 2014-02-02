JMeterWebsocketPlugin
=====================

A WebSocket plugin for JMeter.

=====================

This plugin is intended to be used with JMeter to allow for communication with a web server via WebSockets.  The plugin handles the initial HTTP request as well as the upgrade to a WebSocket connection.  A detailed log of connection events is recorded, as is all data sent from the server.

To install this plugin, simply take the output jar with dependencies (target/JMeterWebSocketPlugin-1.0.0-jar-with-dependencies.jar)and place it in the 'lib/ext/' folder of JMeter.

=====================

How to use the JMeter WebSocket plugin

=====================

1. Install the plugin by placing the jar with depedencies in JMeter's 'lib/ext/' folder.
2. Run JMeter, and start a new thread group.
3. Add both a 'View Results Tree' listener and a WebSocket sampler to the thread group.  The WebSocket Sampler will be listed at the bottom of the 'Add > Sampler' menu.
4. That's it!  The default values will conduct a test that pings a generic websocket echo endpoint at echo.websocket.org.  You can run the test and see the corresponding results in the results tree that you created.  You should see a complete log of communication activity (in the 'request' tab) as well as a complete log of everything the WebSocket server sent down to JMeter (in the 'Response Data' tab).

=====================

What you can configure

=====================

In the WebSocket Sampler, you can modify four settings:

1. WebSocket endpoint full url:  This is the fully-qualified URL with which you will connect.  Notice that it must contain a 'ws' or 'wss' protocol.
2. Send following data: This is the data that will be sent to the WebSocket endpoint as soon as a connection is established.
3. Close connection upon receiving: This is the data that JMeter will look for and subsequently end the test upon receiving.  In our echo demo, we simply waited to receive 'Hello World!' from the echo server and ended the test at that point.
4. Connection timeout: This is the timeout (in milliseconds) that we wait for a response from an open WebSocket connection.  If we do not receive a resposne containing our 'close connection' message within this timeframe, we forcefully close the connection.