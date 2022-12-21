#!/usr/bin/env groovy
import java.util.Date
import java.text.SimpleDateFormat

/*
* promotes a package from beta to a release ready
* only one <major.minor.patch> version of a package can be promoted
*/
def call(String subscriberPackageVersionId, String jwtCredentialId, String devHubUsername, String devHubInstanceUrl, String devHubConsumerKey, Boolean bypassError) {
		
	sfdx.init()
	
	try{
		withCredentials([file(credentialsId: jwtCredentialId, variable: 'jwt_key_file')]) {
			
			echo "=== SFDX AUTHENTICATION ==="
			authenticateSalesforceOrg(devHubUsername, devHubInstanceUrl, devHubConsumerKey, jwt_key_file)
			
			echo "=== SFDX PROMOTE PACKAGE VERSION ==="
			promotePackageVersion(subscriberPackageVersionId, devHubUsername, bypassError)
		}
		
	} catch(Exception e) {
		currentBuild.result = 'FAILED'
		throw e
	}
}

def authenticateSalesforceOrg(String username, String instanceUrl, String connectedAppConsumerkey, Object jwtKeyfile){
	def result = sfdx.cmd("sfdx force:auth:jwt:grant --clientid ${connectedAppConsumerkey} --username ${username} --setdefaultusername --jwtkeyfile ${jwtKeyfile} --instanceurl ${instanceUrl}")
	echo "${result}"
}

def promotePackageVersion(String subscriberPackageVersionId, String devHubUsername, Boolean bypassError){
	def result = sfdx.cmd("sfdx force:package:version:promote --package ${subscriberPackageVersionId} --json --noprompt --targetdevhubusername ${devHubUsername}", bypassError)
	
	if(result != null) {
		result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
		def resultJson = json.convertStringIntoJSON(result)
		
		echo 'status :: ' + resultJson.status
		echo 'Id :: ' + resultJson.result.id
		echo 'success :: ' + resultJson.result.success
		echo 'errors :: ' + resultJson.result.errors
		
		return resultJson
	} else {
		echo 'Skipped the Package Promotion Stage due to an SFDX error...'
		return null
	}
}

