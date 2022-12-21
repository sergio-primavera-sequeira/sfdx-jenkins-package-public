#!/usr/bin/env groovy
import java.util.Date
import java.text.SimpleDateFormat

def call(String packageId, String jwtCredentialId, String devHubUsername, String devHubInstanceUrl, String devHubConsumerKey) {
		
	sfdx.init()
	
	try{
		withCredentials([file(credentialsId: jwtCredentialId, variable: 'jwt_key_file')]) {
			
			echo "=== SFDX AUTHENTICATION ==="
			authenticateToDevHub(devHubUsername, devHubInstanceUrl, devHubConsumerKey, jwt_key_file)
			
			echo "=== SFDX CREATE PACKAGE VERSION ==="
			createPackageVersion(packageId, devHubUsername)
			
			echo "=== SFDX LATEST PACKAGE VERSION ==="
			def subscriberPackageVersionId  = getLastestPackageVersionCreated(packageId, devHubUsername)
			echo 'Subscriber Package Version ID :: ' + "${subscriberPackageVersionId}"
			
			echo "=== SFDX LATEST PACKAGE VERSION INSTALL URL ==="
			def installUrl = getInstallUrl(subscriberPackageVersionId, devHubUsername)
			echo 'install URL :: ' + "${installUrl}"
			
			//displays the install URL directly in the description
			currentBuild.description = currentBuild.description != null ?  (currentBuild.description + "\nINSTALL URL : " + installUrl) : ("INSTALL URL : " + installUrl)
			
			return subscriberPackageVersionId  
		}
		
	} catch(Exception e) {
		currentBuild.result = 'FAILED'
		throw e
	}
}

def authenticateToDevHub(String username, String instanceUrl, String connectedAppConsumerkey, Object jwtKeyfile){
	def result = sfdx.cmd("sfdx force:auth:jwt:grant --clientid ${connectedAppConsumerkey} --username ${username} --setdefaultusername --jwtkeyfile ${jwtKeyfile} --instanceurl ${instanceUrl}")
	echo "${result}"
}
