#!/usr/bin/env groovy
import java.util.Date
import java.text.SimpleDateFormat

def call(String jwtCredentialId, String username, String instanceUrl, String consumerKey, Boolean bypassError) {
		
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

def runLocalTests(Boolean bypassError){
	def result = sfdx.cmd("sfdx force:apex:test:run --testlevel RunLocalTests --synchronous --resultformat json --detailedcoverage  --codecoverage", bypassError)
	
	if(result != null) {
		
		if (!isUnix()) {
			result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
		}
		
		def testResultJson = json.convertStringIntoJSON(result)

		echo 'Outcome :: ' + testResultJson.result.summary.outcome
		echo 'Tests Ran :: ' + testResultJson.result.summary.testsRan
		echo 'Passing :: ' + testResultJson.result.summary.passing
		echo 'Failing :: ' + testResultJson.result.summary.failing
		echo 'Pass Rate :: ' + testResultJson.result.summary.passRate
		echo 'Fail Rate :: ' + testResultJson.result.summary.failRate
		echo 'Test Run Coverage :: ' + testResultJson.result.summary.testRunCoverage
		echo 'Org Wide Coverage :: ' + testResultJson.result.summary.orgWideCoverage

		return testResultJson		
	} else {
		echo 'Skipped the Package Promotion Stage due to an SFDX error...'
		return null
	}
}
