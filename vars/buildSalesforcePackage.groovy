#!/usr/bin/env groovy
import java.util.Date
import java.text.SimpleDateFormat

def call(String dev_hub = 'none') {
	
	def SFDC_ORG_01_JWT_KEY_CRED_ID="sf-jwt-key"
        def SFDC_ORG_01_USER="integration.jenkins@sfjenkins.poc.org01.ca"
        def SFDC_ORG_01="https://login.salesforce.com" 
	def SFDC_ORG_01_CONNECTED_APP_CONSUMER_KEY="3MVG9ux34Ig8G5epoz.M1VfJxB82Qyj0J57NXfZmSeZWN5XytkVPTKSj7C9J.QYiwbdkPpmv9X0Efg0CKRXIX"
	
	configSfdx()
	
	try{
		withCredentials([file(credentialsId: SFDC_ORG_01_JWT_KEY_CRED_ID, variable: 'jwt_key_file')]) {
			return '!!!TEST SPS!!!'
		}
	}
	}catch(Exception e) {
		currentBuild.result = 'FAILED'
		throw e
	}
}

def configSfdx(){
	
	echo 'Starting sfdx'
	
	//SFDX HOME: an SFDX custom tool needs to be configured and the 'Tool Home' (when 'Install automatically' is checked) on custom tools needs to be configured
	env.SFDX_HOME = tool name: 'sfdx', type: 'com.cloudbees.jenkins.plugins.customtools.CustomTool' 
	
	def result = cdmSfdx("--version")
	echo "${result}"
}

def cdmSfdx(String command, Boolean bypassError = false) {
	
    	def path = "\"${env.SFDX_HOME}\"" //adds '"' to the SFDX_HOME path in case there are spaces inside the path
	
	try {
	    if (isUnix()) {
		return sh(returnStdout: true, script: "${path}/sfdx ${command}")
	    } else {
		return bat(returnStdout: true, script: "${path}/sfdx ${command}").trim()
	    }
	} catch (Exception ex) {				
		echo '==== SFDX ERROR ===='
		echo ex.toString()
		echo '===================='
				
		if(!bypassError){
			throw ex
		}
		
		return null
	}	
}

def convertStringIntoJSON(String jsonStr) {
    def json = readJSON text: jsonStr //need the Pipeline Utility Steps plugin
    return json
}