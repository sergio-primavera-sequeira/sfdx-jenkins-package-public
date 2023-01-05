pipeline {
    
    agent {
       	label 'salesforce'
    }
   	
    options {
        disableConcurrentBuilds()
    }
	
    libraries {
	  lib('salesforce-utils')
    }
	    
    environment {
    	//SFDC ORG
    	SFDC_ORG_01_JWT_KEY_CRED_ID="sf-jwt-key"
        SFDC_ORG_01_USER="integration.jenkins@sfjenkins.poc.org01.ca"
        SFDC_ORG_01="https://login.salesforce.com" 
	SFDC_ORG_01_CONNECTED_APP_CONSUMER_KEY="3MVG9ux34Ig8G5epoz.M1VfJxB82Qyj0J57NXfZmSeZWN5XytkVPTKSj7C9J.QYiwbdkPpmv9X0Efg0CKRXIX"
	    
	//PACKAGE: prerequisite -> sfdx force:package:create --path force-app/main/default/ --name "Jenkins" --description "Jenkins Package Example" --packagetype Unlocked
	PACKAGE_ID='0HoDn000000sXzVKAU'
    	PACKAGE_VERSION_ID = ''
    }
    
    stages {
	 stage('Connect to Salesforce') {
	    steps {		    
		    script {
			try {
				withCredentials([file(credentialsId: SFDC_ORG_01_JWT_KEY_CRED_ID, variable: 'jwt_key_file')]) {

					echo "=== SFDX AUTHENTICATION ==="
					authenticateSalesforceOrg(SFDC_ORG_01_USER, SFDC_ORG_01, SFDC_ORG_01_CONNECTED_APP_CONSUMER_KEY, jwt_key_file)
				}

			} catch (Exception e) {
				currentBuild.result = 'FAILED'
				throw e
			}
		    }
	    }
	}
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
