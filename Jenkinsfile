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
	    
	//PACKAGE: prerequisite -> sfdx force:package:create --path force-app/main/default/ --name "Jenkins" --description "Jenkins Package Example" --packagetype Unlocked
	PACKAGE_ID='0HoDn000000sXzVKAU'
    	PACKAGE_VERSION_ID = ''
    }
    
    stages {
	 stage('Run Salesforce Local Tests') {
	    when {
		branch 'master*'
	    }
	    steps {		    
		    script {
			def resultsJson = salesforceRunLocalTests(SFDC_ORG_01_JWT_KEY_CRED_ID,
							          SFDC_ORG_01_USER, 
							          SFDC_ORG_01,
							          SFDC_ORG_01_CONNECTED_APP_CONSUMER_KEY,
								  false)
			    
		       def testOutcome = resultsJson.result.summary.outcome
		       echo 'TESTS OUTCOME: ' + "${testOutcome}"
		    }
	    }
	}
	    
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
			def resultsJson = salesforcePromotePackage(PACKAGE_VERSION_ID, 
								   SFDC_ORG_01_JWT_KEY_CRED_ID,
								   SFDC_ORG_01_USER, 
								   SFDC_ORG_01,
								   SFDC_ORG_01_CONNECTED_APP_CONSUMER_KEY,
								   true)
			    
		       def promotionOutcome = resultsJson != null ? resultsJson.result.success : 'false'
		       echo 'PROMOTION SUCCES: ' + "${promotionOutcome}"
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
	*/
    }
}
