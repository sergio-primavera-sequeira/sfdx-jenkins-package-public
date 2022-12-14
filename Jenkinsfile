pipeline {
    
    agent any
	    
    environment {
        //SFDX HOME: an SFDX custom tool needs to be configured and the 'Tool Home' (when 'Install automatically' is checked) on custom tools needs to be configured
	SFDX_HOME = tool name: 'sfdx', type: 'com.cloudbees.jenkins.plugins.customtools.CustomTool' 
        
    	//SFDC ORG01
    	SFDC_ORG_01_JWT_KEY_CRED_ID="sf-jwt-key"
        SFDC_ORG_01_USER="integration.jenkins@sfjenkins.poc.org01.ca"
        SFDC_ORG_01="https://login.salesforce.com" 
	SFDC_ORG_01_CONNECTED_APP_CONSUMER_KEY="3MVG9ux34Ig8G5epoz.M1VfJxB82Qyj0J57NXfZmSeZWN5XytkVPTKSj7C9J.QYiwbdkPpmv9X0Efg0CKRXIX"
	    
	//PACKAGE
	PACKAGE_NAME='0HoDn000000sXzVKAU'
    	PACKAGE_VERSION = ''
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
			def result = cdmSfdx("force:apex:test:run --testlevel RunLocalTests --synchronous --resultformat json --detailedcoverage  --codecoverage")
			result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
			
			def testResultJson = convertStringIntoJSON(result)
			
			echo 'Outcome :: ' + testResultJson.result.summary.outcome
			echo 'Tests Ran :: ' + testResultJson.result.summary.testsRan
			echo 'Passing :: ' + testResultJson.result.summary.passing
			echo 'Failing :: ' + testResultJson.result.summary.failing
			echo 'Pass Rate :: ' + testResultJson.result.summary.passRate
			echo 'Fail Rate :: ' + testResultJson.result.summary.failRate
			echo 'Test Run Coverage :: ' + testResultJson.result.summary.testRunCoverage
			echo 'Org Wide Coverage :: ' + testResultJson.result.summary.orgWideCoverage
                }
            }
        }
	    
	stage('Create Package Version - SFDC Org 01') {
             steps {
                echo 'Create Package Version - SFDC Org 01..'
                script {			
			echo "Skipped because it exceeded the org limit of creating packages"
			//CREATE PACKAGE VERSION
			//def result = cdmSfdx("force:package:version:create --package ${PACKAGE_NAME} --installationkeybypass --wait 1 --json --codecoverage --targetdevhubusername ${SFDC_ORG_01_USER}")
			//result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
			
			//def packageVersionResultJson = convertStringIntoJSON(result)
			//echo "${packageVersionResultJson}"
                }
            }
	 }
		
	 stage('List Lastest Package Version - SFDC Org 01') {
             steps {
                echo 'List Lastest Package Version - SFDC Org 01..'
                script {
			def result = cdmSfdx("force:package:version:create:list -c 1 --json --targetdevhubusername ${SFDC_ORG_01_USER}")
			result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
			
			def packageVersionListResultJson = convertStringIntoJSON(result)
			def latestPackageVersion = packageVersionListResultJson.result.last()
						
			echo 'Id :: ' + latestPackageVersion.Id
			echo 'Status :: ' + latestPackageVersion.Status
			echo 'Package2Id :: ' + latestPackageVersion.Package2Id
			echo 'Package2VersionId :: ' + latestPackageVersion.Package2VersionId
			echo 'SubscriberPackageVersionId :: ' + latestPackageVersion.SubscriberPackageVersionId
			echo 'Tag :: ' + latestPackageVersion.Tag
			echo 'Branch :: ' + latestPackageVersion.Branch
			echo 'Error :: ' + latestPackageVersion.Error
			echo 'CreatedDate :: ' + latestPackageVersion.CreatedDate
			echo 'HasMetadataRemoved :: ' + latestPackageVersion.HasMetadataRemoved
			echo 'CreatedBy :: ' + latestPackageVersion.CreatedBy
                }
            }
        }
    }
}

def cdmSfdx(String command) {
    def path = "\"${SFDX_HOME}\"" //adds '"' to the SFDX_HOME path in case there are spaces inside the path
	
    if (isUnix()) {
    	return sh(returnStdout: true, script: "${path}/sfdx ${command}")
    } else {
    	return bat(returnStdout: true, script: "${path}/sfdx ${command}").trim()
    }
}

def convertStringIntoJSON(String jsonStr) {
    def json = readJSON text: jsonStr //need the Pipeline Utility Steps plugin
    return json
}
