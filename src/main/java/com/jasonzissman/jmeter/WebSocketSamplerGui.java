package com.jasonzissman.jmeter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.nio.channels.IllegalSelectorException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.protocol.http.gui.HTTPArgumentsPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;

/**
 * GUI for JMeter WebSocket Sampler
 * @author Jason Zissman
 */
@SuppressWarnings("serial")
public class WebSocketSamplerGui extends AbstractSamplerGui {

	public static final String RES_KEY_PFX = "[res_key=";
	public static final String testName = "Websocket Sampler";
	private boolean displayName = true;
	
	private JTextField wsUrlField;
	private JTextField wsDataToSendField;
	private JTextField wsMessageToEndCommField;
	private JTextField wsTimeoutField;

    private static final ResourceBundle resources;

    static {
        Locale loc = JMeterUtils.getLocale();
        resources = ResourceBundle.getBundle(WebSocketSampler.class.getName() + "Resources", loc);
    }
	
    public WebSocketSamplerGui() {
        this(true);
    }

    public WebSocketSamplerGui(boolean displayName) {
        this.displayName = displayName;
        init();
    }

    private void init() {
    	
    	setLayout(new BorderLayout(0, 5));
    	if (displayName) {
    		setBorder(makeBorder());
    		add(makeTitlePanel(), BorderLayout.NORTH);
    	}
    	
    	add(createMainPanel(), BorderLayout.CENTER);
    	setDefaultValues();
    }
    
    private void setDefaultValues() {
		wsUrlField.setText("ws://echo.websocket.org");
		wsDataToSendField.setText("Hello World!");
		wsMessageToEndCommField.setText("Hello World!");
		wsTimeoutField.setText("2000");
	}

	@Override
	public String getLabelResource() {
    	throw new IllegalStateException("This shouldn't be called");
	}    
    
	@Override
	public String getStaticLabel() {
		return getResString("websocket_sample_title");
	}

	protected VerticalPanel createMainPanel() {
		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.add(createWsUrlPanel());
		mainPanel.add(createWsMessageToSendPanel());
		mainPanel.add(createWsMessageToEndCommPanel());
		mainPanel.add(createWsTimeoutPanel());
		return mainPanel;
	}
	
	@Override
	public void configure(TestElement el) {
		super.configure(el);
		wsUrlField.setText(el.getPropertyAsString(WebSocketSampler.WS_ENDPOINT_URL));
		wsDataToSendField.setText(el.getPropertyAsString(WebSocketSampler.WS_DATA_TO_SEND));
		wsMessageToEndCommField.setText(el.getPropertyAsString(WebSocketSampler.WS_TERMINATING_MESSAGE));
		wsTimeoutField.setText(el.getPropertyAsString(WebSocketSampler.WS_TIMEOUT));
	}
	
	@Override
	public TestElement createTestElement() {
		WebSocketSampler sampler = new WebSocketSampler();
		sampler.setName(getName());
		sampler.setComment(getComment());
		sampler.setProperty(TestElement.GUI_CLASS, this.getClass().getName());
		sampler.setProperty(TestElement.TEST_CLASS, sampler.getClass().getName());		
		modifyTestElement(sampler);
		return sampler;
	}
	
	@Override
	public void modifyTestElement(TestElement el) {
		configureTestElement(el);
		el.setProperty(WebSocketSampler.WS_ENDPOINT_URL, wsUrlField.getText());
		el.setProperty(WebSocketSampler.WS_DATA_TO_SEND, wsDataToSendField.getText());
		el.setProperty(WebSocketSampler.WS_TERMINATING_MESSAGE, wsMessageToEndCommField.getText());
		el.setProperty(WebSocketSampler.WS_TIMEOUT, wsTimeoutField.getText());
	}

	private Component createWsTimeoutPanel() {
		JLabel wsUrlLabel = new JLabel("Connection timeout (ms)");
		wsTimeoutField = new JTextField(25);
		wsUrlLabel.setLabelFor(wsUrlField);

		JPanel urlPanel = new JPanel(new BorderLayout(5, 0));
		urlPanel.add(wsUrlLabel, BorderLayout.WEST);
		urlPanel.add(wsTimeoutField, BorderLayout.CENTER);
		return urlPanel;
	}

	private Component createWsMessageToEndCommPanel() {
		JLabel wsUrlLabel = new JLabel("Close connection upon receiving");
		wsMessageToEndCommField = new JTextField(25);
		wsUrlLabel.setLabelFor(wsUrlField);

		JPanel urlPanel = new JPanel(new BorderLayout(5, 0));
		urlPanel.add(wsUrlLabel, BorderLayout.WEST);
		urlPanel.add(wsMessageToEndCommField, BorderLayout.CENTER);
		return urlPanel;
	}

	private Component createWsMessageToSendPanel() {
		JLabel wsUrlLabel = new JLabel("Send following data");
		wsDataToSendField = new JTextField(25);
		wsUrlLabel.setLabelFor(wsUrlField);

		JPanel urlPanel = new JPanel(new BorderLayout(5, 0));
		urlPanel.add(wsUrlLabel, BorderLayout.WEST);
		urlPanel.add(wsDataToSendField, BorderLayout.CENTER);
		return urlPanel;
	}

	private JPanel createWsUrlPanel() {
		JLabel wsUrlLabel = new JLabel("WebSocket endpoint full URL");
		wsUrlField = new JTextField(25);
		wsUrlLabel.setLabelFor(wsUrlField);

		JPanel urlPanel = new JPanel(new BorderLayout(5, 0));
		urlPanel.add(wsUrlLabel, BorderLayout.WEST);
		urlPanel.add(wsUrlField, BorderLayout.CENTER);
		return urlPanel;
	}
	
    public static String getResString(String key) {
        return getResStringDefault(key, RES_KEY_PFX + key + "]"); 
    }

    private static String getResStringDefault(String key, String defaultValue) {
        if (key == null) {
            return null;
        }

        key = key.replace(' ', '_'); 
        key = key.toLowerCase(java.util.Locale.ENGLISH);
        String resString = null;
        
        try {
            resString = resources.getString(key);
        } catch (MissingResourceException mre) {
            resString = defaultValue;
        }
        
        return resString;
    }
	
}
