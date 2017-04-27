package com.llbean.jmeter.assertions;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.apache.jmeter.assertions.Assertion;
import org.apache.jmeter.assertions.AssertionResult;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.AbstractTestElement;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

/**
 * JSONSchemaAssertion.java
 * @author crksf
 *
 */
public class JSONSchemaAssertion extends AbstractTestElement implements Assertion, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2795118814285850597L;
	
	private static final String FILE_NAME_IS_REQUIRED = "FileName is required";
	private static final String RESPONSE_CODE_IS_REQUIRED = "Expected response code is required";
	
	//private static final Logger log = LoggerFactory.getLogger(JSONSchemaAssertion.class);
	
	public static final String JSON_FILENAME_KEY = "jsonschema_assertion_filename";
	public static final String EXPECTED_RESPONSE_CODE = "expected_response_code";

	/**
     * getResult
     * 
     */
    @Override
    public AssertionResult getResult(SampleResult response) {
    	AssertionResult result = new AssertionResult(getName());
    	
    	String resultData = response.getResponseDataAsString();
    	
        if (resultData.length() == 0) {
            return result.setResultForNull();
        }
        
        String responseCode = getExpectedResponseCode();
        
        if (responseCode == null || responseCode.length() == 0){
        	return result.setResultForFailure(RESPONSE_CODE_IS_REQUIRED);
        }
        
        String jsonSchemaFilename = getSchemaFilename();
        
        if (jsonSchemaFilename == null || jsonSchemaFilename.length() == 0) {
            result.setResultForFailure(FILE_NAME_IS_REQUIRED);
        } else {
			try {
				
				ProcessingReport report = getValidationReport(resultData, jsonSchemaFilename, responseCode);
				
				if(!report.isSuccess()){
					//result.setResultForFailure(report.toString());
					result.setError(true);
	                result.setFailureMessage(report.toString());
				}
				
			} catch (IOException | ProcessingException e) {
				result.setResultForFailure(e.getMessage());
			}
			
        }
    	
    	return result;
    }
    
    public void setSchemaFilename(String jsonSchemaFileName){
    	setProperty(JSON_FILENAME_KEY,jsonSchemaFileName);
    }
    
    public String getSchemaFilename(){
    	return getPropertyAsString(JSON_FILENAME_KEY);
    }
    
    public void setExpectedResponseCode(String expectedResponseCode){
    	setProperty(EXPECTED_RESPONSE_CODE,expectedResponseCode);
    }
    
    public String getExpectedResponseCode(){
    	return getPropertyAsString(EXPECTED_RESPONSE_CODE);
    }
    
    /**
     * Returns Report with validation results.
     * @param resultData: JSON string to be validated
     * @param schemaFileName: Path to file containing the schemas
     * @return
     * @throws IOException 
     * @throws JsonProcessingException 
     * @throws ProcessingException 
     */
    private ProcessingReport getValidationReport(String resultData, String schemaFileName, String responseCode) throws JsonProcessingException, IOException, ProcessingException{
    	
    	JsonNode responseObject = getJsonNode(resultData);
    	JsonNode schemaObject = getSchema(responseCode, schemaFileName);
    	
    	JsonSchemaFactory factory 	 = JsonSchemaFactory.byDefault();
    	JsonSchema        jsonSchema  = factory.getJsonSchema(schemaObject); 
    	
    	ProcessingReport report = jsonSchema.validate(responseObject);
    	
    	return report;
    }
    
    /**
     * 
     * @param jsonString
     * @return
     * @throws JsonProcessingException
     * @throws IOException
     */
    private JsonNode getJsonNode(String jsonString) throws JsonProcessingException, IOException{   
		ObjectMapper mapper = new ObjectMapper();
		JsonNode 	 node 	= mapper.readTree(jsonString);
		return node;
	}

    /**
     * 
     * @param responseCode
     * @param filePath
     * @return
     * @throws JsonProcessingException
     * @throws IOException
     */
	private JsonNode getSchema(String responseCode, String filePath) throws JsonProcessingException, IOException{
		ObjectMapper mapper   = new ObjectMapper();
		ArrayNode schemas  = (ArrayNode) mapper.readTree(new File(filePath));
		JsonNode schema = new ObjectMapper().createObjectNode();

		for(int i=0;i<schemas.size();i++){
			JsonNode schemaNode = schemas.get(i);
			if(responseCode.equals(schemaNode.get("code").asText())){
				schema = schemaNode.get("schema");
			}
		}
		return (JsonNode)schema;
	}
}