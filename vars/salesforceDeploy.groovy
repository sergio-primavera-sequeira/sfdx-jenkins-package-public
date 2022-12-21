#!/usr/bin/env groovy
import java.util.Date
import java.text.SimpleDateFormat

def call(Boolean doValidationOnly, String jwtCredentialId, String username, String instanceUrl, String consumerKey, Boolean bypassError) {
		
	sfdx.init()

	try{
		withCredentials([file(credentialsId: jwtCredentialId, variable: 'jwt_key_file')]) {

			echo "=== SFDX AUTHENTICATION ==="
			authenticateSalesforceOrg(username, instanceUrl, consumerKey, jwt_key_file)
			
			echo "=== SFDX RUN LOCAL TESTS ==="
			def testResultsJson = runLocalTests(bypassError)

			return testResultsJson
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
