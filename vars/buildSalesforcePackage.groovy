#!/usr/bin/env groovy
import java.util.Date
import java.text.SimpleDateFormat

def call(String dev_hub = 'none') {
	
	def SFDC_ORG_01_JWT_KEY_CRED_ID="sf-jwt-key"
        def SFDC_ORG_01_USER="integration.jenkins@sfjenkins.poc.org01.ca"
        def SFDC_ORG_01="https://login.salesforce.com" 
	def SFDC_ORG_01_CONNECTED_APP_CONSUMER_KEY="3MVG9ux34Ig8G5epoz.M1VfJxB82Qyj0J57NXfZmSeZWN5XytkVPTKSj7C9J.QYiwbdkPpmv9X0Efg0CKRXIX"
	
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
