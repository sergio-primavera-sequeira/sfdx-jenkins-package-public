#!/usr/bin/env groovy
import java.util.Date
import java.text.SimpleDateFormat

def call(String sourcePath, Boolean doValidationOnly, String jwtCredentialId, String username, String instanceUrl, String consumerKey, Boolean bypassError) {
		
	sfdx.init()

	try{
		withCredentials([file(credentialsId: jwtCredentialId, variable: 'jwt_key_file')]) {

			echo "=== SFDX AUTHENTICATION ==="
			authenticateSalesforceOrg(username, instanceUrl, consumerKey, jwt_key_file)
			
			echo "=== SFDX DEPLOY TO SALESFORCE ==="
			def deployResultsJson = deployToSalesforce(sourcePath, doValidationOnly, bypassError)

			return deployResultsJson
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

def deployToSalesforce(String sourcePath, Boolean doValidationOnly, Boolean bypassError){
	def checkOnly = doValidationOnly ? '--checkonly' : ''
	def result = sfdx.cmd("sfdx force:source:deploy --sourcepath ${sourcePath} ${checkOnly} --verbose --json", bypassError)
	
	if(result != null) {
		result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
		
		/*
		def testResultJson = json.convertStringIntoJSON(result)

		echo 'Outcome :: ' + testResultJson.result.summary.outcome
		echo 'Tests Ran :: ' + testResultJson.result.summary.testsRan
		echo 'Passing :: ' + testResultJson.result.summary.passing
		echo 'Failing :: ' + testResultJson.result.summary.failing
		echo 'Pass Rate :: ' + testResultJson.result.summary.passRate
		echo 'Fail Rate :: ' + testResultJson.result.summary.failRate
		echo 'Test Run Coverage :: ' + testResultJson.result.summary.testRunCoverage
		echo 'Org Wide Coverage :: ' + testResultJson.result.summary.orgWideCoverage
		*/
		echo "${result}"

		return result		
	} else {
		echo 'Skipped the Package Promotion Stage due to an SFDX error...'
		return null
	}
}
