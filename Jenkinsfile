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
	PACKAGE_NAME='0HoDn000000sXzVKAU' //prerequisite -> sfdx force:package:create --path force-app/main/default/ --name "Jenkins" --description "Jenkins Package Example" --packagetype Unlocked
    	PACKAGE_VERSION = ''
	PACKAGE_VERSION_INSTALL_ID = ''
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
	    
	stage('Create Package Version - SFDC Org 01') {
             steps {
                echo 'Create Package Version - SFDC Org 01..'
                script {
			
			def result = cdmSfdx("force:package:version:create --package ${PACKAGE_NAME} --installationkeybypass --wait 1 --json --codecoverage --targetdevhubusername ${SFDC_ORG_01_USER}")
			result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
			
			def packageVersionResultJson = convertStringIntoJSON(result)
			echo "${packageVersionResultJson}"
                }
            }
	 }
		
	 stage('Lastest Package Creation - SFDC Org 01') {
             steps {
                echo 'Lastest Package Creation - SFDC Org 01..'
                script {
			def result = cdmSfdx("force:package:version:create:list -c 1 --json --targetdevhubusername ${SFDC_ORG_01_USER}")
			result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
			
			def packageCreationListResultJson = convertStringIntoJSON(result)
			def latestPackageCreation= packageCreationListResultJson.result.last()
						
			echo 'Id :: ' + latestPackageCreation.Id
			echo 'Status :: ' + latestPackageCreation.Status
			echo 'Package2Id :: ' + latestPackageCreation.Package2Id
			echo 'Package2VersionId :: ' + latestPackageCreation.Package2VersionId
			echo 'SubscriberPackageVersionId :: ' + latestPackageCreation.SubscriberPackageVersionId
			echo 'Tag :: ' + latestPackageCreation.Tag
			echo 'Branch :: ' + latestPackageCreation.Branch
			echo 'Error :: ' + latestPackageCreation.Error
			echo 'CreatedDate :: ' + latestPackageCreation.CreatedDate
			echo 'HasMetadataRemoved :: ' + latestPackageCreation.HasMetadataRemoved
			echo 'CreatedBy :: ' + latestPackageCreation.CreatedBy
			
			PACKAGE_VERSION = latestPackageCreation.SubscriberPackageVersionId
			echo 'PACKAGE_VERSION :: ' + "${PACKAGE_VERSION}"
                }
            }
	 }
		 
         stage('Lastest Package Version - SFDC Org 01') {
             steps {
                echo 'Lastest Package Version - SFDC Org 01..'
                script {
			def result = cdmSfdx("force:package:version:list --verbose --json --targetdevhubusername ${SFDC_ORG_01_USER}")
			result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
			
			def packageVersionListResultJson = convertStringIntoJSON(result)
			def latestPackageVersion = packageVersionListResultJson.result.last()
						
			echo 'Package2Id :: ' + latestPackageVersion.Package2Id
			echo 'Branch :: ' + latestPackageVersion.Branch
			echo 'Tag :: ' + latestPackageVersion.Tag
			echo 'MajorVersion :: ' + latestPackageVersion.MajorVersion
			echo 'MinorVersion :: ' + latestPackageVersion.MinorVersion
			echo 'PatchVersion :: ' + latestPackageVersion.PatchVersion
			echo 'BuildNumber :: ' + latestPackageVersion.BuildNumber
			echo 'Id :: ' + latestPackageVersion.Id
			echo 'SubscriberPackageVersionId :: ' + latestPackageVersion.SubscriberPackageVersionId
			echo 'ConvertedFromVersionId :: ' + latestPackageVersion.ConvertedFromVersionId
			echo 'Name :: ' + latestPackageVersion.Name
			echo 'NamespacePrefix :: ' + latestPackageVersion.NamespacePrefix
			echo 'Package2Name :: ' + latestPackageVersion.Package2Name
			echo 'Description :: ' + latestPackageVersion.Description
			echo 'Version :: ' + latestPackageVersion.Version
			echo 'IsPasswordProtected :: ' + latestPackageVersion.IsPasswordProtected
			echo 'IsReleased :: ' + latestPackageVersion.IsReleased
			echo 'CreatedDate :: ' + latestPackageVersion.CreatedDate
			echo 'LastModifiedDate :: ' + latestPackageVersion.LastModifiedDate
			echo 'InstallUrl :: ' + latestPackageVersion.InstallUrl
			echo 'CodeCoverage :: ' + latestPackageVersion.CodeCoverage
			echo 'HasPassedCodeCoverageCheck :: ' + latestPackageVersion.HasPassedCodeCoverageCheck
			echo 'ValidationSkipped :: ' + latestPackageVersion.ValidationSkipped
			echo 'AncestorId :: ' + latestPackageVersion.AncestorId
			echo 'AncestorVersion :: ' + latestPackageVersion.AncestorVersion
			echo 'Alias :: ' + latestPackageVersion.Alias
			echo 'IsOrgDependent :: ' + latestPackageVersion.IsOrgDependent
			echo 'ReleaseVersion :: ' + latestPackageVersion.ReleaseVersion
			echo 'BuildDurationInSeconds :: ' + latestPackageVersion.BuildDurationInSeconds
			echo 'ValidationSkipped :: ' + latestPackageVersion.ValidationSkipped
			echo 'HasMetadataRemoved :: ' + latestPackageVersion.HasMetadataRemoved
			echo 'CreatedBy :: ' + latestPackageVersion.CreatedBy
                }
            }
        }
	    
	stage('Install Package Version - SFDC Org 01') {
             steps {
                echo 'Install Package Version - SFDC Org 01..'
                script {			
			def result = cdmSfdx("force:package:install --package ${PACKAGE_VERSION} --wait 1 --apexcompile package --securitytype AdminsOnly --upgradetype Mixed --json --noprompt --targetusername ${SFDC_ORG_01_USER}")
			result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
			
			def packageVersionInstallResultJson = convertStringIntoJSON(result)
			
			echo 'status :: ' + packageVersionInstallResultJson.status
			echo 'type :: ' + packageVersionInstallResultJson.result.type
			echo 'url :: ' + packageVersionInstallResultJson.result.url
			echo 'Id :: ' + packageVersionInstallResultJson.result.Id
			echo 'IsDeleted :: ' + packageVersionInstallResultJson.result.IsDeleted
			echo 'CreatedDate :: ' + packageVersionInstallResultJson.result.CreatedDate
			echo 'CreatedById :: ' + packageVersionInstallResultJson.result.CreatedById
			echo 'LastModifiedDate :: ' + packageVersionInstallResultJson.result.LastModifiedDate
			echo 'LastModifiedById :: ' + packageVersionInstallResultJson.result.LastModifiedById
			echo 'SystemModstamp :: ' + packageVersionInstallResultJson.result.SystemModstamp
			echo 'SubscriberPackageVersionKey :: ' + packageVersionInstallResultJson.result.SubscriberPackageVersionKey
			echo 'NameConflictResolution :: ' + packageVersionInstallResultJson.result.NameConflictResolution
			echo 'PackageInstallSource :: ' + packageVersionInstallResultJson.result.PackageInstallSource
			echo 'ProfileMappings :: ' + packageVersionInstallResultJson.result.ProfileMappings
			echo 'Password :: ' + packageVersionInstallResultJson.result.Password
			echo 'EnableRss :: ' + packageVersionInstallResultJson.result.EnableRss
			echo 'UpgradeType :: ' + packageVersionInstallResultJson.result.UpgradeType
			echo 'ApexCompileType :: ' + packageVersionInstallResultJson.result.ApexCompileType
			echo 'Status :: ' + packageVersionInstallResultJson.result.Status
			echo 'Errors :: ' + packageVersionInstallResultJson.result.Errors
			
			PACKAGE_VERSION_INSTALL_ID = packageVersionInstallResultJson.result.Id
			echo 'PACKAGE_VERSION_INSTALL_ID :: ' + "${PACKAGE_VERSION_INSTALL_ID}"
                }
            }
	 }
	    
	stage('Package Install Report - SFDC Org 01') {
             steps {
                echo 'Package Install Reportn - SFDC Org 01..'
                script {			
			def result = cdmSfdx("force:package:install:report -i ${PACKAGE_VERSION_INSTALL_ID} --json --targetusername ${SFDC_ORG_01_USER}")
			result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
			
			def packageVersionInstallResultJson = convertStringIntoJSON(result)
			
			echo 'status :: ' + packageVersionInstallResultJson.status
			echo 'type :: ' + packageVersionInstallResultJson.result.type
			echo 'url :: ' + packageVersionInstallResultJson.result.url
			echo 'Id :: ' + packageVersionInstallResultJson.result.Id
			echo 'IsDeleted :: ' + packageVersionInstallResultJson.result.IsDeleted
			echo 'CreatedDate :: ' + packageVersionInstallResultJson.result.CreatedDate
			echo 'CreatedById :: ' + packageVersionInstallResultJson.result.CreatedById
			echo 'LastModifiedDate :: ' + packageVersionInstallResultJson.result.LastModifiedDate
			echo 'LastModifiedById :: ' + packageVersionInstallResultJson.result.LastModifiedById
			echo 'SystemModstamp :: ' + packageVersionInstallResultJson.result.SystemModstamp
			echo 'SubscriberPackageVersionKey :: ' + packageVersionInstallResultJson.result.SubscriberPackageVersionKey
			echo 'NameConflictResolution :: ' + packageVersionInstallResultJson.result.NameConflictResolution
			echo 'PackageInstallSource :: ' + packageVersionInstallResultJson.result.PackageInstallSource
			echo 'ProfileMappings :: ' + packageVersionInstallResultJson.result.ProfileMappings
			echo 'Password :: ' + packageVersionInstallResultJson.result.Password
			echo 'EnableRss :: ' + packageVersionInstallResultJson.result.EnableRss
			echo 'UpgradeType :: ' + packageVersionInstallResultJson.result.UpgradeType
			echo 'ApexCompileType :: ' + packageVersionInstallResultJson.result.ApexCompileType
			echo 'Status :: ' + packageVersionInstallResultJson.result.Status
			echo 'Errors :: ' + packageVersionInstallResultJson.result.Errors
                }
            }
	 }
    }
}

def cdmSfdx(String command) {
    	def path = "\"${SFDX_HOME}\"" //adds '"' to the SFDX_HOME path in case there are spaces inside the path
	def output = ''
	
	try {
	    if (isUnix()) {
		output =  sh(returnStdout: true, script: "${path}/sfdx ${command}")
	    } else {
		output =  bat(returnStdout: true, script: "${path}/sfdx ${command}").trim()
	    }
	} catch (Exception ex) {
		
		output = output.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
		//def errorJson = convertStringIntoJSON(output)
				
		echo '==== SFDX ERROR ===='
		//echo 'SPS 01' + "${output}"
		echo 'SPS 02' + ex.toString()
    		
	} finally {
		return output
	}	
}

def convertStringIntoJSON(String jsonStr) {
    def json = readJSON text: jsonStr //need the Pipeline Utility Steps plugin
    return json
}
