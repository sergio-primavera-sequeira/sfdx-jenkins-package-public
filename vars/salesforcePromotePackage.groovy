#!/usr/bin/env groovy
import java.util.Date
import java.text.SimpleDateFormat

/*
* promotes a package from beta to a release ready
* only one <major.minor.patch> version of a package can be promoted
*/
def call(String subscriberPackageVersionId, String jwtCredentialId, String devHubUsername, String devHubInstanceUrl, String devHubConsumerKey) {
		
	sfdx.init()
	
	try{
		withCredentials([file(credentialsId: jwtCredentialId, variable: 'jwt_key_file')]) {
			
			echo "=== SFDX AUTHENTICATION ==="
			authenticateToDevHub(devHubUsername, devHubInstanceUrl, devHubConsumerKey, jwt_key_file)
			
			echo "=== SFDX PROMOTE PACKAGE VERSION ==="
			promotePackageVersion(subscriberPackageVersionId, devHubUsername)
			

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

def promotePackageVersion(String subscriberPackageVersionId, String devHubUsername){
	def result = sfdx.cmd("force:package:version:promote --package ${subscriberPackageVersionId} --json --noprompt --targetdevhubusername ${devHubUsername}", true)

	if(result != null) {
		echo "${result}"
	} else {
		echo 'Skipped the Package Promotion Stage due to an SFDX error...'
	}
}

