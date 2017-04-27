package com.llbean.jmeter.assertions;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.jmeter.assertions.gui.AbstractAssertionGui;
import org.apache.jmeter.assertions.gui.XMLSchemaAssertionGUI;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSONSchemaAssertionGUI.java
 * @author crksf
 *
 */
public class JSONSchemaAssertionGUI extends AbstractAssertionGui{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(XMLSchemaAssertionGUI.class);
	
	//private static final String JSON_SCHEMA_FIELD = "jsonSchemaField";
	
	//private static final String RESPONSE_CODE_FIELD = "responseCodeField";
	
	private static final String COMPONENT_TITLE = "(LLBQA) JSON Schema Assertion";
	
	private static final String JSON_PANEL_LABEL = "JSON File with array of schemas for supported response codes";
	
	private static final String JSON_SCHEMA_FIELD_LABEL = "Filename";
	
	private static final String RESPONSE_CODE_FIELD_LABEL = "Response Code";

	private JTextField jsonSchemaField;
	
	private JTextField responseCodeField;
	
	public JSONSchemaAssertionGUI(){
		init();
	}
	
	@Override
	public String getLabelResource() {
		return null;
	}
	
	@Override
	public String getStaticLabel() {
		return COMPONENT_TITLE;
	}

	@Override
	public TestElement createTestElement() {
        log.debug("JSONSchemaAssertionGUI.createTestElement() called");
        JSONSchemaAssertion el = new JSONSchemaAssertion();
        modifyTestElement(el);
		return el;
	}

	@Override
	public void modifyTestElement(TestElement element) {
        log.debug("JSONSchemaAssertionGUI.modifyTestElement() called");
		configureTestElement(element);
		((JSONSchemaAssertion)element).setSchemaFilename(jsonSchemaField.getText());
		((JSONSchemaAssertion)element).setExpectedResponseCode(responseCodeField.getText());
	}
	
	@Override
	public void clearGui(){
		super.clearGui();
		jsonSchemaField.setText("");
		responseCodeField.setText("");
	}
	
	@Override
	public void configure(TestElement el){
		super.configure(el);
		JSONSchemaAssertion assertion = (JSONSchemaAssertion)el;
		jsonSchemaField.setText(assertion.getSchemaFilename());
		responseCodeField.setText(assertion.getExpectedResponseCode());
	}
	
	/**
	 * Inits the GUI.
	 */
	private void init(){
		setLayout(new BorderLayout(0,10));
		setBorder(makeBorder());
		
		add(makeTitlePanel(),BorderLayout.NORTH);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		// USER_INPUT
		VerticalPanel assertionPanel = new VerticalPanel();
		assertionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), JSON_PANEL_LABEL));
		
		// doctype
        HorizontalPanel jsonSchemaPanel = new HorizontalPanel();

        jsonSchemaPanel.add(new JLabel(JSON_SCHEMA_FIELD_LABEL));
        jsonSchemaField = new JTextField("Replace with relative filepath",25);
        jsonSchemaPanel.add(jsonSchemaField);
        
        jsonSchemaPanel.add(new JLabel(RESPONSE_CODE_FIELD_LABEL));
        responseCodeField = new JTextField("(Replace with variable name)",5);
        jsonSchemaPanel.add(responseCodeField);
        
        assertionPanel.add(jsonSchemaPanel);
        
        mainPanel.add(assertionPanel, BorderLayout.NORTH);
        add(mainPanel,BorderLayout.CENTER);
        
	}

}
