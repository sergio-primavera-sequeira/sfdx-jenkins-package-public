#!/usr/bin/env groovy
import java.util.Date
import java.text.SimpleDateFormat

def SFDC_ORG_01_JWT_KEY_CRED_ID
def SFDC_ORG_01_USER
def SFDC_ORG_01
def SFDC_ORG_01_CONNECTED_APP_CONSUMER_KEY

def call(String name = 'human') {
	return "STSTST"
}

def init(String dev_hub = 'none') {
	
	SFDC_ORG_01_JWT_KEY_CRED_ID="sf-jwt-key"
	SFDC_ORG_01_USER="integration.jenkins@sfjenkins.poc.org01.ca"
	SFDC_ORG_01="https://login.salesforce.com" 
	SFDC_ORG_01_CONNECTED_APP_CONSUMER_KEY="3MVG9ux34Ig8G5epoz.M1VfJxB82Qyj0J57NXfZmSeZWN5XytkVPTKSj7C9J.QYiwbdkPpmv9X0Efg0CKRXIX"
}

def buildPackage(){
	
	sfdx.init()
	
	try{
		withCredentials([file(credentialsId: SFDC_ORG_01_JWT_KEY_CRED_ID, variable: 'jwt_key_file')]) {
			return '!!!TEST SPS!!!'
		}
	} catch(Exception e) {
		currentBuild.result = 'FAILED'
		throw e
	}
}
