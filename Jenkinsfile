pipeline {
    
    agent any
    
    environment {
        TOOLBELT = "C:\\Program Files\\sfdx\\bin"
        
    	SFDC_ORG_01_JWT_KEY_CRED_ID="sf-jwt-key"
        SFDC_ORG_01_USER="integration.jenkins@sfjenkins.poc.org01.ca"
        SFDC_ORG_01="https://login.salesforce.com" 
	SFDC_ORG_01_CONNECTED_APP_CONSUMER_KEY="3MVG9ux34Ig8G5epoz.M1VfJxB82Qyj0J57NXfZmSeZWN5XytkVPTKSj7C9J.QYiwbdkPpmv9X0Efg0CKRXIX"
    }
    
    stages {
        stage('Build') {
            steps {
                echo 'Building..'
            }
        }
        
        stage('Deploy') {
             steps {
                echo 'Testing..'
                //echo "${toolbelt}/sfdx help"
                //bat "${toolbelt}/sfdx help"
                script {
                    withCredentials([file(credentialsId: SFDC_ORG_01_JWT_KEY_CRED_ID, variable: 'jwt_key_file')]) {
                        
			    rec = cmd_sfdx("force:auth:jwt:grant --clientid ${SFDC_ORG_01_CONNECTED_APP_CONSUMER_KEY} --username ${SFDC_ORG_01_USER} --setdefaultusername --jwtkeyfile ${jwt_key_file}   --instanceurl ${SFDC_ORG_01}")
			    echo "${rec}"
			    
			    rec = cmd_sfdx("force:source:deploy -p ./force-app/main/default/")
			    echo "${rec}"
                    }
                }
            }
        }
    }
}

def cmd_sfdx(command) {
    //echo "${TOOLBELT}/sfdx ${command}"
    return bat(returnStdout: true, script: "${toolbelt}/sfdx ${command}")
}
