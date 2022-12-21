#!/usr/bin/env groovy
import java.util.Date
import java.text.SimpleDateFormat

def call(String sourcePath, Boolean doValidationOnly, Boolean doRunLocalTests, String jwtCredentialId, String username, String instanceUrl, String consumerKey, Boolean bypassError) {
		
	sfdx.init()

	try{
		withCredentials([file(credentialsId: jwtCredentialId, variable: 'jwt_key_file')]) {

			echo "=== SFDX AUTHENTICATION ==="
			authenticateSalesforceOrg(username, instanceUrl, consumerKey, jwt_key_file)
			
			echo "=== SFDX DEPLOY TO SALESFORCE ==="
			def deployResultsJson = deployToSalesforce(sourcePath, doValidationOnly, doRunLocalTests, bypassError)

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

def deployToSalesforce(String sourcePath, Boolean doValidationOnly, Boolean doRunLocalTests, Boolean bypassError){
	def testlevel  = doRunLocalTests ? '--testlevel RunLocalTests' : ''
	def checkOnly = doValidationOnly ? '--checkonly' : ''
	
	def result = sfdx.cmd("sfdx force:source:deploy --sourcepath ${sourcePath} ${checkOnly} ${testlevel} --verbose --json", bypassError)
	
	if(result != null) {
		result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
		
		def deployResultJson = json.convertStringIntoJSON(result)

		echo 'Id :: ' + deployResultJson.result.id
		echo 'Status :: ' + deployResultJson.result.status
		echo 'Check Only :: ' + deployResultJson.result.checkOnly
		echo 'Number Components Deployed :: ' + deployResultJson.result.numberComponentsDeployed
		echo 'Number Components Total :: ' + deployResultJson.result.numberComponentsTotal
		echo 'Number Test Errors :: ' + deployResultJson.result.numberTestErrors
		echo 'Number Tests Completed :: ' + deployResultJson.result.numberTestsCompleted
		echo 'Number Tests Total :: ' + deployResultJson.result.numberTestsTotal
		echo 'Start Date :: ' + deployResultJson.result.startDate
		echo 'Completed Date :: ' + deployResultJson.result.completedDate
		echo 'Rollback On Error :: ' + deployResultJson.result.rollbackOnError	
	
		return deployResultJson		
	} else {
		echo 'Skipped the Package Promotion Stage due to an SFDX error...'
		return null
	}
}
