#!/usr/bin/env groovy
import java.util.Date
import java.text.SimpleDateFormat

def call(String dev_hub = 'none') {
	
	def DEV_HUB_ORG_JWT_KEY_CRED_ID="sf-jwt-key"
	def DEV_HUB_ORG_USER="integration.jenkins@sfjenkins.poc.org01.ca"
	def DEV_HUB_ORG ="https://login.salesforce.com" 
	def DEV_HUB_ORG_CONNECTED_APP_CONSUMER_KEY="3MVG9ux34Ig8G5epoz.M1VfJxB82Qyj0J57NXfZmSeZWN5XytkVPTKSj7C9J.QYiwbdkPpmv9X0Efg0CKRXIX"
	
	sfdx.init()
	
	try{
		withCredentials([file(credentialsId: DEV_HUB_ORG_JWT_KEY_CRED_ID, variable: 'jwt_key_file')]) {
			
			connectToDevHub(DEV_HUB_ORG_CONNECTED_APP_CONSUMER_KEY, DEV_HUB_ORG DEV_HUB_ORG_USER)
			
		}
	} catch(Exception e) {
		currentBuild.result = 'FAILED'
		throw e
	}
}

def connectToDevHub(String CONNECTED_APP_CONSUMER_KEY, String DEV_HUB_USER, String INSTANCE_URL){
	def result = sfdx.cdm("sfdx force:auth:jwt:grant --clientid ${CONNECTED_APP_CONSUMER_KEY} --username ${DEV_HUB_USER} --setdefaultusername --jwtkeyfile ${jwt_key_file} --instanceurl ${INSTANCE_URL}")
	echo "${result}"
}
