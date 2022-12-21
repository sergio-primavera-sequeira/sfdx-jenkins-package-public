pipeline {
    
    agent {
       	label 'salesforce'
    }
   	
    options {
        disableConcurrentBuilds()
    }
	
    libraries {
	  lib('pipeline-library')
    }
	    
    environment {
    	//SFDC ORG
    	SFDC_ORG_01_JWT_KEY_CRED_ID="sf-jwt-key"
        SFDC_ORG_01_USER="integration.jenkins@sfjenkins.poc.org01.ca"
        SFDC_ORG_01="https://login.salesforce.com" 
	SFDC_ORG_01_CONNECTED_APP_CONSUMER_KEY="3MVG9ux34Ig8G5epoz.M1VfJxB82Qyj0J57NXfZmSeZWN5XytkVPTKSj7C9J.QYiwbdkPpmv9X0Efg0CKRXIX"
	    
	//PACKAGE
	PACKAGE_ID='0HoDn000000sXzVKAU' //prerequisite -> sfdx force:package:create --path force-app/main/default/ --name "Jenkins" --description "Jenkins Package Example" --packagetype Unlocked
    	PACKAGE_VERSION_ID = ''
    }
    
    stages {
        stage('Build Salesforce Package') {
	    when {
		branch 'master*'
	    }
	    steps {		    
		    script {
			PACKAGE_VERSION_ID = salesforceBuildPackage(PACKAGE_ID, 
								    SFDC_ORG_01_JWT_KEY_CRED_ID,
								    SFDC_ORG_01_USER, 
								    SFDC_ORG_01,
								    SFDC_ORG_01_CONNECTED_APP_CONSUMER_KEY)
		    }
	    }
	}
	
	stage('Install Salesforce Package') {
	    when {
		branch 'master*'
	    }
	    steps {		    
		    script {
			def packageInstallStatus = salesforceInstallPackage(PACKAGE_VERSION_ID, 
									    SFDC_ORG_01_JWT_KEY_CRED_ID,
									    SFDC_ORG_01_USER, 
									    SFDC_ORG_01,
									    SFDC_ORG_01_CONNECTED_APP_CONSUMER_KEY)
		    }
	    }
	}
	    
    	stage('Promote Salesforce Package') {
	    when {
		branch 'master*'
	    }
	    steps {		    
		    script {
			salesforcePromotePackage(PACKAGE_VERSION_ID, 
					         SFDC_ORG_01_JWT_KEY_CRED_ID,
					         SFDC_ORG_01_USER, 
					         SFDC_ORG_01,
					         SFDC_ORG_01_CONNECTED_APP_CONSUMER_KEY)
		    }
	    }
	}
	    
	/*	
	stage('Validation - SFDC Org 01') {
             steps {
                echo 'Validation - SFDC Org 01..'
                script {
			def result = cdmSfdx("force:source:deploy -p ./force-app/main/default/ --checkonly --verbose --json")
			echo "${result}"
                }
            }
        }
        
        stage('Deployment - SFDC Org 01') {
             steps {
                echo 'Deployment - SFDC Org 01..'
                script {
			def result = cdmSfdx("force:source:deploy --sourcepath ./force-app/main/default/ --verbose --json")
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
	    				 	    
	 stage('Package Promotion - SFDC Org 01') {
            steps {
                echo 'Package Promotion - SFDC Org 01...'
		script {
		    //promotes a package from beta to a release ready
                    //only one <major.minor.patch> version of a package can be promoted
		    def result = cdmSfdx("force:package:version:promote --package ${PACKAGE_VERSION} --json --noprompt --targetdevhubusername ${SFDC_ORG_01_USER}", true)
		
		    if(result != null) {
		    	echo "${result}"
		    } else {
		    	echo 'Skipped the Package Promotion Stage due to an SFDX error...'
		    }
                }
            }
        }
	*/
    }
}
