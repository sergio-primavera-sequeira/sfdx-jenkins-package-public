pipeline {
    
    agent any
	    
    environment {
        //SFDX HOME
	SFDX_HOME = tool name: 'sfdx', type: 'com.cloudbees.jenkins.plugins.customtools.CustomTool' //the 'Tool Home' on custom tools needs to be configured...
        
    	//SFDC ORG01
    	SFDC_ORG_01_JWT_KEY_CRED_ID="sf-jwt-key"
        SFDC_ORG_01_USER="integration.jenkins@sfjenkins.poc.org01.ca"
        SFDC_ORG_01="https://login.salesforce.com" 
	SFDC_ORG_01_CONNECTED_APP_CONSUMER_KEY="3MVG9ux34Ig8G5epoz.M1VfJxB82Qyj0J57NXfZmSeZWN5XytkVPTKSj7C9J.QYiwbdkPpmv9X0Efg0CKRXIX"
    }
    
    stages {
        stage('Authentication - SFDC Org 01') {
            steps {
                echo 'Authentication - SFDC Org 01...'
		script {
                    withCredentials([file(credentialsId: SFDC_ORG_01_JWT_KEY_CRED_ID, variable: 'jwt_key_file')]) {
		    	def result = cdmSfdx("force:auth:jwt:grant --clientid ${SFDC_ORG_01_CONNECTED_APP_CONSUMER_KEY} --username ${SFDC_ORG_01_USER} --setdefaultusername --jwtkeyfile ${jwt_key_file}   --instanceurl ${SFDC_ORG_01}")
		        echo "${result}"
                    }
                }
            }
        }
        
        stage('Deployment - SFDC Org 01') {
             steps {
                echo 'Deployment - SFDC Org 01..'
                script {
			def result = cdmSfdx("force:source:deploy -p ./force-app/main/default/")
			echo "${result}"
                }
            }
        }
	    
	stage('Run Local Tests - SFDC Org 01') {
             steps {
                echo 'Run Local Tests - SFDC Org 01'
                script {
			def result = cdmSfdx("force:apex:test:run --testlevel RunLocalTests --synchronous --resultformat json  --codecoverage")
			result = result.readLines().drop(1).join(" ") //removes the first line of the output
			
			def resultJson = convertStringIntoJSON(result)
			
			def testStatus = resultJson.result.summary.outcome
			echo "${testStatus}"
                }
            }
        }
    }
}

def cdmSfdx(command) {
    def toolbelt = "\"${SFDX_HOME}\"" //adds '"' to the SFDX_HOME path in case there are spaces
	
    if (isUnix()) {
    	return sh(returnStdout: true, script: "${toolbelt}/sfdx ${command}")
    } else {
    	return bat(returnStdout: true, script: "${toolbelt}/sfdx ${command}").trim()
    }
}

def convertStringIntoJSON(jsonStr) {
    def json = readJSON text: jsonStr //need the Pipeline Utility Steps plugin
    return json
}
