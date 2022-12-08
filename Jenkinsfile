pipeline {
    
    agent any
    
    environment {
        //SFDX toolbelt
	TOOLBELT = '"C:\\Program Files\\sfdx\\bin"'
        
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
		    	def result = cmd_sfdx("force:auth:jwt:grant --clientid ${SFDC_ORG_01_CONNECTED_APP_CONSUMER_KEY} --username ${SFDC_ORG_01_USER} --setdefaultusername --jwtkeyfile ${jwt_key_file}   --instanceurl ${SFDC_ORG_01}")
		        echo "${result}"
                    }
                }
            }
        }
        
        stage('Deployment - SFDC Org 01') {
             steps {
                echo 'Deployment - SFDC Org 01..'
                script {
			def result = cmd_sfdx("force:source:deploy -p ./force-app/main/default/")
			echo "${result}"
                }
            }
        }
	    
	stage('Run Local Tests - SFDC Org 01') {
             steps {
                echo 'Run Local Tests - SFDC Org 01'
                script {
			def result = cmd_sfdx("force:apex:test:run --testlevel RunLocalTests --synchronous --resultformat json  --codecoverage")
			echo '-------------------------'
			echo "${result}"
			echo '-------------------------'
			
			def bb = convertTestResultsIntoJSON(result)
			
			echo "${bb}"
			echo '-------------------------'
			
			//def jsonResult = readJSON text: '"${result}"'
			def pr = result[0]
			echo "${pr}"
			def pr1 = result[1]
			echo "${pr1}"
                }
            }
        }
    }
}

def cmd_sfdx(command) {
    return bat(returnStdout: true, script: "${TOOLBELT}/sfdx ${command}").trim()
}

def convertTestResultsIntoJSON(sfdxTestResult) {
    def jsonStr = sfdxTestResult.substring(sfdxTestResult.indexOf('{'), sfdxTestResult.lastIndexOf('}'))
    def json = readJSON text: jsonStr
    return json
}
