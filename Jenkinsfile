def PACKAGE_NAME = ''
def PACKAGE_VERSION = ''
def PACKAGE_INSTALL_URL = ''

pipeline {    
    
	agent {
       	label 'salesforce'
    }
	
    environment {
    	//SFDC ORG
    	SFDC_ORG_01_JWT_KEY_CRED_ID="sf-jwt-key"
        SFDC_ORG_01_USER="integration.jenkins@sfjenkins.poc.org01.ca"
        SFDC_ORG_01="https://login.salesforce.com" 
	SFDC_ORG_01_CONNECTED_APP_CONSUMER_KEY="3MVG9ux34Ig8G5epoz.M1VfJxB82Qyj0J57NXfZmSeZWN5XytkVPTKSj7C9J.QYiwbdkPpmv9X0Efg0CKRXIX"
	EMAIL_RECIPIENTS = 'sprimaverasequeira@deloitte.ca;chuyu@deloitte.ca'
	SFDC_SPS_TEST = "${env.GLOBAL_SFDC_SPS_TEST}"
    }
    
    stages {
	    
	 stage('Run Salesforce Local Tests') {
            when {
                branch 'master*'
            }
            steps {
                script {
		    echo 'SFDC_SPS_TEST :: ' + "${env.SFDC_SPS_TEST}"
			PACKAGE_NAME = 'citm'
			PACKAGE_VERSION = '1.2.3.0'
			PACKAGE_INSTALL_URL = 'wwww.installurl.ca'
                    def resultsJson = salesforceRunLocalTests(env.SFDC_ORG_01_JWT_KEY_CRED_ID,
							      env.SFDC_ORG_01_USER,
							      env.SFDC_ORG_01,
							      env.SFDC_ORG_01_CONNECTED_APP_CONSUMER_KEY,
							      true)

		    if(resultsJson != null) {
		    	def testOutcome = resultsJson.result.summary.outcome
                    	echo 'TESTS OUTCOME :: ' + "${testOutcome}"
		    }
                }
            }
        }
    }

	post {
		success {
			script {
				notifySuccessOnBuild(env.PACKAGE_NAME, env.PACKAGE_VERSION, env.PACKAGE_INSTALL_URL)
			}
		}
	}
}

def salesforceRunLocalTests(String jwtCredentialId, String username, String instanceUrl, String consumerKey, Boolean bypassError) {
	try {
		withCredentials([file(credentialsId: jwtCredentialId, variable: 'jwt_key_file')]) {
			
			echo "=== SFDX AUTHENTICATION ==="
			authenticateSalesforceOrg(username, instanceUrl, consumerKey, jwt_key_file)

			echo "=== SFDX RUN LOCAL TESTS ==="
			def testResultsJson = runLocalTests(bypassError)

			return testResultsJson
		}
	} catch (Exception e) {
		currentBuild.result = 'FAILED'
		notifyErrorOnBuild(e)
		throw e
	}
}

def runLocalTests(Boolean bypassError) {
	def result = cmd("sfdx force:apex:test:run --testlevel RunLocalTests --resultformat json --detailedcoverage  --codecoverage --synchronous", bypassError)

	if (result != null) {

		if (!isUnix()) {
			result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
		}

		def testResultJson = convertStringIntoJSON(result)

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
		echo 'Skipped the Run Local Tests due to an SFDX error...'
		return null
	}
}

def authenticateSalesforceOrg(String username, String instanceUrl, String connectedAppConsumerkey, Object jwtKeyfile) {
        def result = cmd("sfdx force:auth:jwt:grant --clientid ${connectedAppConsumerkey} --username ${username} --setdefaultusername --jwtkeyfile ${jwtKeyfile} --instanceurl ${instanceUrl}")
        echo "${result}"
}


def cmd(String command, Boolean bypassError = false) {

	env.SFDX_HOME = tool name: 'sfdx', type: 'com.cloudbees.jenkins.plugins.customtools.CustomTool'
        def path = "\"${env.SFDX_HOME}\"" //adds '"' to the SFDX_HOME path in case there are spaces inside the path

        try {
                if (isUnix()) {
                        return sh(returnStdout: true, script: "${path}/${command}")
                } else {
                        return bat(returnStdout: true, script: "${path}/${command}").trim()
                }
        } catch (Exception ex) {
                echo '==== SFDX ERROR ===='
                echo ex.toString()
                echo '===================='

                if (!bypassError) {
                        throw ex
                }

                return null
        }
}

def notifySuccessOnBuild(String packageName, String packageVersion, String packageInstallUrl){
	def subject = 'SUCCESS: JENKINS Build Successful'
	def body = """<h1 style="background-color:green;font-size:42px;color:white;padding:10px;">Build Successful</h1>
		      <p>Job '${env.JOB_NAME} [${env.BRANCH_NAME} - ${env.BUILD_NUMBER}]':</p>
		      <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BRANCH_NAME} - ${env.BUILD_NUMBER}]</a>&QUOT;</p>
		      <p>Package: ${packageName}</p>
		      <p>Version: v.${packageVersion}</p>
		      <p>Install URL: ${packageInstallUrl}</p>"""

	echo "${body}"

	notifyByEmail(subject, body, env.EMAIL_RECIPIENTS)
}

def notifyErrorOnBuild(Exception e){ 
	def subject = "ERROR: JENKINS Build Failed"
	def body = """<h1 style="background-color:red;font-size:42px;color:white;padding:10px;">Build Failed</h1>
		      <p>Job '${env.JOB_NAME} [${env.BRANCH_NAME} - ${env.BUILD_NUMBER}]':</p>
		      <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BRANCH_NAME} - ${env.BUILD_NUMBER}]</a>&QUOT;</p>
		      <p>Error: ${e}</p>"""

	echo "${body}"

	notifyByEmail(subject, body, env.EMAIL_RECIPIENTS)
}

def notifyByEmail(String subject, String body, String recipients = ''){ 
	emailext (
		subject: "${subject}",
		body: "${body}",
		mimeType: "text/html",
		to: "${recipients}",
		recipientProviders: [developers()]
	)
}
