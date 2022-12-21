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
	 
    	stage('Validate Salesforce Deployment') {
	    when {
		branch 'master*'
	    }
	    steps {		    
		    script {
			def resultsJson = salesforceDeployComponent('./force-app/main/default/',
								    true,
				                                    SFDC_ORG_01_JWT_KEY_CRED_ID,
								    SFDC_ORG_01_USER, 
								    SFDC_ORG_01,
								    SFDC_ORG_01_CONNECTED_APP_CONSUMER_KEY,
								    false)
			    
		       //def testOutcome = resultsJson.result.summary.outcome
		       //echo 'TESTS OUTCOME :: ' + "${testOutcome}"
		    }
	    }
	}
	    
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
		       echo 'TESTS OUTCOME :: ' + "${testOutcome}"
		    }
	    }
	}
	    
        stage('Build Salesforce Package') {
	    when {
		branch 'master*'
	    }
	    steps {		    
		    script {
			def resultsJson = salesforceBuildPackage(PACKAGE_ID, 
								 SFDC_ORG_01_JWT_KEY_CRED_ID,
								 SFDC_ORG_01_USER, 
								 SFDC_ORG_01,
								 SFDC_ORG_01_CONNECTED_APP_CONSUMER_KEY,
								 true)
			
			
		        def subscriberPackageVersionId = resultsJson.SubscriberPackageVersionId
		    	echo 'SUBSCRIBER PACKAGE VERSION ID :: ' + "${subscriberPackageVersionId}"
		        PACKAGE_VERSION_ID = subscriberPackageVersionId
			    
			def packageName = resultsJson.Package2Name
		    	echo 'PACKAGE NAME :: ' + "${packageName}"
			    
			def packageVersion = resultsJson.Version
		    	echo 'PACKAGE VERSION :: ' + "${packageVersion}"
			    
		        def installUrl = resultsJson.InstallUrl
		    	echo 'INSTALL URL :: ' + "${installUrl}"
			    
			def buildDescription = packageName + ' v.' + packageVersion +  '\nINSTALL URL : ' + installUrl
			    
		       //displays the package information directly on the build description
		       currentBuild.description = currentBuild.description != null ? (currentBuild.description + "\n" + buildDescription) : (buildDescription)
		    }
	    }
	}
	
	stage('Install Salesforce Package') {
	    when {
		branch 'master*'
	    }
	    steps {		    
		    script {
			def resultsJson = salesforceInstallPackage(PACKAGE_VERSION_ID, 
								   SFDC_ORG_01_JWT_KEY_CRED_ID,
								   SFDC_ORG_01_USER, 
								   SFDC_ORG_01,
								   SFDC_ORG_01_CONNECTED_APP_CONSUMER_KEY,
								   false)
			    
		    	def packageInstallStatus = resultsJson.result.Status
			echo 'PACKAGE INSTALL STATUS :: ' + "${packageInstallStatus}"
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
			    
		       def promotionSuccessOutcome = resultsJson != null ? resultsJson.result.success : 'false'
		       echo 'PACKAGE PROMOTION SUCCES :: ' + "${promotionSuccessOutcome}"
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
