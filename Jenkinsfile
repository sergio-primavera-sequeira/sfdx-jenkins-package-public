/* GLOBAL VARIABLES */

def PACKAGE_VERSION_ID = ''
def PACKAGE_NAME = ''
def PACKAGE_VERSION = ''
def PACKAGE_INSTALL_URL = ''

pipeline {
    
    agent {
       	label 'salesforce'
    }
	
	/*
	* TODO: Replace all hardcoded values by global properties set in Jenkins.
	*       For example:
	*       a) Create a global Jenkins property 'SF_CITM_PACKAGE_ID', and reference it inside 
    *  	    the local environnement variable : PACKAGE_ID  = "${env.SF_CITM_PACKAGE_ID}"
	*       b) Create a global Jenkins property 'SF_INSTANCE_URL_PROD', and reference it inside 
    *  	    the local environnement variable : SFDC_DEVHUB_INSTANCE_URL  = "${env.SF_INSTANCE_URL_PROD}"
	*       c) Create a global Jenkins property 'SF_INSTANCE_URL_SANDBOX', and reference it inside 
    *  	    the local environnement variable : SFDC_SANDBOX_INSTANCE_URL  = "${env.SF_INSTANCE_URL_SANDBOX}"
	*       d) Create a global Jenkins property 'SF_SERVER_KEY_CREDENTALS_ID', and reference it inside 
    *  	    the local environnement variable : SFDC_JWT_KEY_CRED_ID  = "${env.SF_SERVER_KEY_CREDENTALS_ID}"
	*       e) Create a global Jenkins property 'SF_BUILD_EMAIL_RECIPIENTS', and reference it inside 
    *  	    the local environnement variable : EMAIL_RECIPIENTS  = "${env.SF_BUILD_EMAIL_RECIPIENTS}"
	*/
    environment {
		//SFDX TOOL
		SFDX = tool name: 'sfdx', type: 'com.cloudbees.jenkins.plugins.customtools.CustomTool'
		
		//SFDC PACKAGE
		PACKAGE_ID = '0HoDn000000sXzVKAU'
		PACKAGE_FOLDER = './force-app/main/default/' 
	
    	//SFDC CREDENTIALS ID
    	SFDC_JWT_KEY_CRED_ID = "sf-jwt-key"
		
		//SFDC DEVHUB
        SFDC_DEVHUB_USER = "integration.jenkins@sfjenkins.poc.org01.ca"
        SFDC_DEVHUB_INSTANCE_URL = "https://login.salesforce.com" 
		SFDC_DEVHUB_CONNECTED_APP_CONSUMER_KEY = "3MVG9ux34Ig8G5epoz.M1VfJxB82Qyj0J57NXfZmSeZWN5XytkVPTKSj7C9J.QYiwbdkPpmv9X0Efg0CKRXIX"
		
		//SFDC SANDBOX
		SFDC_SANDBOX_USER = "integration.jenkins@sfjenkins.poc.org01.ca"
        SFDC_SANDBOX_INSTANCE_URL = "https://login.salesforce.com" 
		SFDC_SANDBOX_CONNECTED_APP_CONSUMER_KEY = "3MVG9ux34Ig8G5epoz.M1VfJxB82Qyj0J57NXfZmSeZWN5XytkVPTKSj7C9J.QYiwbdkPpmv9X0Efg0CKRXIX"
		
		//EMAIL
		EMAIL_RECIPIENTS = 'sprimaverasequeira@deloitte.ca'
    }
    
    stages {
		stage('Run Salesforce Local Tests') {
			when {
				branch 'master.skip*'
			}
			steps {
				script {
					echo "=== RUN SALESFORCE LOCAL TESTS ==="
				
					def resultsJson = salesforceRunLocalTests(env.SFDC_JWT_KEY_CRED_ID,
															  env.SFDC_SANDBOX_USER,
															  env.SFDC_SANDBOX_INSTANCE_URL,
															  env.SFDC_SANDBOX_CONNECTED_APP_CONSUMER_KEY,
															  true)

					if(resultsJson != null) {
						def testOutcome = resultsJson.result.summary.outcome
						echo 'TESTS OUTCOME :: ' + "${testOutcome}"
					}
				}
			}
		}
		
		stage('Validate Salesforce Metadata') {
			when {
				branch 'master.skip*'
			}
			steps {
				script {
					echo "=== VALIDATE SALESFORCE METADATA ==="
				
					def resultsJson = salesforceDeployComponent(env.PACKAGE_FOLDER,
																true, //validation only
																false,
																env.SFDC_JWT_KEY_CRED_ID,
																env.SFDC_SANDBOX_USER,
																env.SFDC_SANDBOX_INSTANCE_URL,
																env.SFDC_SANDBOX_CONNECTED_APP_CONSUMER_KEY,
																false)

					if(resultsJson != null) {
						def checkOnly = resultsJson.result.checkOnly
						echo 'CHECK ONLY :: ' + "${checkOnly}"

						def validationStatus = resultsJson.result.status
						echo 'VALIDATION STATUS :: ' + "${validationStatus}"
					}
				}
			}
		}
		
		stage('Build Salesforce Package') {
			when {
                branch 'master*'
            }
            steps {
                script {
					echo "=== BUILD SALESFORCE PACKAGE ==="
				
                    def resultsJson = salesforceBuildPackage(env.PACKAGE_ID,
															 env.SFDC_JWT_KEY_CRED_ID,
															 env.SFDC_DEVHUB_USER,
														     env.SFDC_DEVHUB_INSTANCE_URL,
															 env.SFDC_DEVHUB_CONNECTED_APP_CONSUMER_KEY,
															 false)
															 
					if(resultsJson != null) {
						PACKAGE_VERSION_ID = resultsJson.SubscriberPackageVersionId
						echo 'SUBSCRIBER PACKAGE VERSION ID :: ' + "${PACKAGE_VERSION_ID}"

						PACKAGE_NAME = resultsJson.Package2Name
						echo 'PACKAGE NAME :: ' + "${PACKAGE_NAME}"

						PACKAGE_VERSION = resultsJson.Version
						echo 'PACKAGE VERSION :: ' + "${PACKAGE_VERSION}"

						PACKAGE_INSTALL_URL = resultsJson.InstallUrl
						echo 'INSTALL URL :: ' + "${PACKAGE_INSTALL_URL}"

						def buildDescription = PACKAGE_NAME + ' v.' + PACKAGE_VERSION + ', INSTALL URL : ' + PACKAGE_INSTALL_URL

						//displays the package information directly on the build description
						currentBuild.description = currentBuild.description != null ? (currentBuild.description + "\n" + buildDescription) : (buildDescription)
					}
                }
            }
		}
		
		stage('Install Salesforce Package') {
            when {
                branch 'master.skip*'
            }
            steps {
                script {
					echo "=== INSTALL SALESFORCE PACKAGE ==="
					
                    def resultsJson = salesforceInstallPackage(PACKAGE_VERSION_ID,
															   env.SFDC_JWT_KEY_CRED_ID,
															   env.SFDC_SANDBOX_USER,
															   env.SFDC_SANDBOX_INSTANCE_URL,
															   env.SFDC_SANDBOX_CONNECTED_APP_CONSUMER_KEY,
															   false)

					if(resultsJson != null) {
						def packageInstallStatus = resultsJson.result.Status
						echo 'PACKAGE INSTALL STATUS :: ' + "${packageInstallStatus}"
					}
                }
            }
        }
    }
	
	post {
		success {
			script {
				notifySuccessOnBuild(PACKAGE_NAME, PACKAGE_VERSION, PACKAGE_INSTALL_URL)
			}
		}
	}
}

/* SDFX: CONNECT TO ORG METHOD (JWT AUTH FLOW) */

def authenticateSalesforceOrg(String username, String instanceUrl, String connectedAppConsumerkey, Object jwtKeyfile) {
	def result = cmd("sfdx force:auth:jwt:grant --clientid ${connectedAppConsumerkey} --username ${username} --setdefaultusername --jwtkeyfile ${jwtKeyfile} --instanceurl ${instanceUrl}")
	echo "${result}"
}

/* SDFX: BUILD PACKAGE METHODS */

def salesforceBuildPackage(String packageId, String jwtCredentialId, String devHubUsername, String devHubInstanceUrl, String devHubConsumerKey, Boolean bypassError){
	try {
		withCredentials([file(credentialsId: jwtCredentialId, variable: 'jwt_key_file')]) {
			
			echo "=== SFDX AUTHENTICATION ==="
			authenticateSalesforceOrg(devHubUsername, devHubInstanceUrl, devHubConsumerKey, jwt_key_file)
			
			/*
			echo "=== SFDX CREATE PACKAGE VERSION ==="
			def packageCreateVersionJson = createPackageVersion(packageId, devHubUsername, bypassError)
			def packageVersionCreateId = packageCreateVersionJson.result.Id
			echo 'Package Create Version ID :: ' + "${packageVersionCreateId}"

			if(packageVersionCreateId == null || packageVersionCreateId.equalsIgnoreCase("null")) 
			{
				throw new Exception("SFDX error, could not generate a package version create Id.")
			}
			*/
			
			def packageVersionCreateId = '08cDn000000sYAsIAM'

			echo "=== SFDX LATEST PACKAGE VERSION ==="
			def lastestPackageVersionJson = getLastestPackageVersionCreationStatus(packageVersionCreateId, devHubUsername, bypassError)
			def subscriberPackageVersionId = (String)lastestPackageVersionJson.Branch//lastestPackageVersionJson.SubscriberPackageVersionId
			
			echo 'Class :: ' + "${subscriberPackageVersionId.getClass()}"
			echo 'Subscriber Package Version ID :: ' + "${subscriberPackageVersionId}"

			if(subscriberPackageVersionId == null || subscriberPackageVersionId.equalsIgnoreCase("null")) 
			{
				throw new Exception("SFDX error, could not generate a subscriber package version Id.")
			}
			
			echo "=== SFDX LATEST PACKAGE VERSION INFORMATION ==="
			def latestPackageInformationJson = getLastestPackageVersionInformation(subscriberPackageVersionId, devHubUsername, bypassError)

			return latestPackageInformationJson
		}
	} catch (Exception e) {
		currentBuild.result = 'FAILED'
		notifyErrorOnBuild(e)
		throw e
	}
}

def createPackageVersion(String packageId, String devHubUsername, Boolean bypassError) {
	//--versionnumber parameter to override the sfdx-project.json value
	def result = cmd("sfdx force:package:version:create --package ${packageId} --installationkeybypass --wait 0 --json --codecoverage --targetdevhubusername ${devHubUsername}", bypassError) //waiting 0 minute(s) for the package to be created. Waiting 0 minutes because timeouts were occuring -_-.

	if (result != null) {
		if (!isUnix()) {
			result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
		}

		def packageVersionResultJson = convertStringIntoJSON(result)

		echo 'Id :: ' + packageVersionResultJson.result.Id
		echo 'Status :: ' + packageVersionResultJson.result.Status
		echo 'Package2Id :: ' + packageVersionResultJson.result.Package2Id
		echo 'Package2VersionId :: ' + packageVersionResultJson.result.Package2VersionId
		echo 'SubscriberPackageVersionId :: ' + packageVersionResultJson.result.SubscriberPackageVersionId
		echo 'Tag :: ' + packageVersionResultJson.result.Tag
		echo 'Branch :: ' + packageVersionResultJson.result.Branch
		echo 'Error :: ' + packageVersionResultJson.result.Error
		echo 'CreatedDate :: ' + packageVersionResultJson.result.CreatedDate
		echo 'HasMetadataRemoved :: ' + packageVersionResultJson.result.HasMetadataRemoved
		echo 'CreatedBy :: ' + packageVersionResultJson.result.CreatedBy

		return packageVersionResultJson
	} else {
		echo 'Skipped the Create Package Version due to an SFDX error...'
		return null
	}
}


def getLastestPackageVersionCreationStatus(String packageVersionCreateId, String devHubUsername, Boolean bypassError) {
	def result
	def packageCreationListResultJson
	def latestPackageCreation
	def currrentStatus

	while (true) {
		result = cmd("sfdx force:package:version:create:list -c 1 --json --targetdevhubusername ${devHubUsername}", bypassError)

		if (result != null) {

			if (!isUnix()) {
				result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
			}

			packageCreationListResultJson = convertStringIntoJSON(result)
			
			latestPackageCreation = packageCreationListResultJson.result.findAll {
					r -> r.Id.equalsIgnoreCase(packageVersionCreateId)
			}.last()
			
			currrentStatus = latestPackageCreation.Status

			echo '=== LASTEST PACKAGE CREATION STATUS ==='

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

			if (currrentStatus.equalsIgnoreCase('Success') || currrentStatus.equalsIgnoreCase('Error')) {
					break
			}

			sleep(time: 120, unit: "SECONDS") //wait additional minute(s) until package is created
		} else {
			echo 'Skipped the Get Latest Package Version due to an SFDX error...'
			return null
		}
	}

	return latestPackageCreation
}

def getLastestPackageVersionInformation(String subscriberPackageVersionId, String devHubUsername, Boolean bypassError) {
	def result = cmd("sfdx force:package:version:list --verbose --json --targetdevhubusername ${devHubUsername}")

	if (result != null) {
		if (!isUnix()) {
				result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
		}

		def packageVersionListResultJson = convertStringIntoJSON(result)
		def latestPackageVersion = packageVersionListResultJson.result.findAll {
				r -> r.SubscriberPackageVersionId.equalsIgnoreCase(subscriberPackageVersionId)
		}.last()

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

		return latestPackageVersion
	} else {
		echo 'Skipped the Get Lastest Package Version Information due to an SFDX error...'
		return null
	}
}

/* SDFX: RUN LOCAL TESTS METHODS */

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
	def result = cmd("sfdx force:apex:test:run --testlevel RunLocalTests --synchronous --resultformat json --detailedcoverage  --codecoverage", bypassError)

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

/* SDFX: INSTALL PACKAGE METHODS */

def salesforceInstallPackage(String subscriberPackageVersionId, String jwtCredentialId, String username, String instanceUrl, String consumerKey, Boolean bypassError) {
	try {
		withCredentials([file(credentialsId: jwtCredentialId, variable: 'jwt_key_file')]) {

			echo "=== SFDX AUTHENTICATION ==="
			authenticateSalesforceOrg(username, instanceUrl, consumerKey, jwt_key_file)

			echo "=== SFDX INSTALL PACKAGE ==="
			def packageVersionInstallInitResultJson = initiatePackageInstallation(subscriberPackageVersionId, username, bypassError)
			def packageInstallId = packageVersionInstallInitResultJson.result.Id
			echo 'Package Install ID :: ' + "${packageInstallId}"
			
			if(packageInstallId == null || packageInstallId.equalsIgnoreCase("null")) 
			{
				throw new Exception("SFDX error, could not generate a package install Id.")
			}

			echo "=== SFDX INSTALL PACKAGE STATUS ==="
			def packageVersionInstallResultJson = getPackageInstallationStatus(packageInstallId, username, bypassError)
			def packageInstallStatus = packageVersionInstallResultJson.result.Status
			echo 'Package Install Status :: ' + "${packageInstallStatus}"

			return packageVersionInstallResultJson
		}

	} catch (Exception e) {
		currentBuild.result = 'FAILED'
		notifyErrorOnBuild(e)
		throw e
	}
}

def initiatePackageInstallation(String subscriberPackageVersionId, String username, Boolean bypassError) {
	def result = cmd("sfdx force:package:install --package ${subscriberPackageVersionId} --wait 5 --apexcompile package --securitytype AdminsOnly --upgradetype Mixed --json --noprompt --targetusername ${username}", bypassError) //waiting 5 minutes for the package to be installed

	if (result != null) {
		if (!isUnix()) {
			result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
		}

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

		return packageVersionInstallResultJson

	} else {
		echo 'Skipped the Initiate Package Installation due to an SFDX error...'
		return null
	}
}

def getPackageInstallationStatus(String packageInstallId, String username, Boolean bypassError) {
	def result
	def packageVersionInstallResultJson
	def currrentStatus

	while (true) {
		result = cmd("sfdx force:package:install:report -i ${packageInstallId} --json --targetusername ${username}", bypassError)

		if (result != null) {
			if (!isUnix()) {
				result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
			}

			packageVersionInstallResultJson = convertStringIntoJSON(result)
			currrentStatus = packageVersionInstallResultJson.result.Status

			echo '=== LATEST PACKAGE INSTALL STATUS ==='

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

			if (currrentStatus.equalsIgnoreCase('Success') || currrentStatus.equalsIgnoreCase('Error')) {
					break
			}

			sleep(time: 60, unit: "SECONDS") //wait additional minutes until package is installed

		} else {
				echo 'Skipped the Get Package Installation Status due to an SFDX error...'
				return null
		}
	}

	return packageVersionInstallResultJson
}

/* SDFX: PACKAGE PROMOTION METHODS */

def salesforcePromotePackage(String subscriberPackageVersionId, String jwtCredentialId, String devHubUsername, String devHubInstanceUrl, String devHubConsumerKey, Boolean bypassError) {
	//promotes a package from beta to a release ready, only one <major.minor.patch> version of a package can be promoted
	try {
		withCredentials([file(credentialsId: jwtCredentialId, variable: 'jwt_key_file')]) {

			echo "=== SFDX AUTHENTICATION ==="
			authenticateSalesforceOrg(devHubUsername, devHubInstanceUrl, devHubConsumerKey, jwt_key_file)

			echo "=== SFDX PROMOTE PACKAGE VERSION ==="
			promotePackageVersion(subscriberPackageVersionId, devHubUsername, bypassError)
		}

	} catch (Exception e) {
		currentBuild.result = 'FAILED'
		notifyErrorOnBuild(e)
		throw e
	}
}

def promotePackageVersion(String subscriberPackageVersionId, String devHubUsername, Boolean bypassError) {
	def result = cmd("sfdx force:package:version:promote --package ${subscriberPackageVersionId} --json --noprompt --targetdevhubusername ${devHubUsername}", bypassError)

	if (result != null) {
		if (!isUnix()) {
			result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
		}

		def resultJson = convertStringIntoJSON(result)

		echo 'status :: ' + resultJson.status
		echo 'Id :: ' + resultJson.result.id
		echo 'success :: ' + resultJson.result.success
		echo 'errors :: ' + resultJson.result.errors

		return resultJson
	} else {
		echo 'Skipped the Promote Package Version due to an SFDX error...'
		return null
	}
}

/* SDFX: DEPLOY/VALIDATE METHODS */

def salesforceDeployComponent(String sourcePath, Boolean doValidationOnly, Boolean doRunLocalTests, String jwtCredentialId, String username, String instanceUrl, String consumerKey, Boolean bypassError){
	try {
		withCredentials([file(credentialsId: jwtCredentialId, variable: 'jwt_key_file')]) {

			echo "=== SFDX AUTHENTICATION ==="
			authenticateSalesforceOrg(username, instanceUrl, consumerKey, jwt_key_file)

			echo "=== SFDX DEPLOY TO SALESFORCE ==="
			def deployResultsJson = deployToSalesforce(sourcePath, doValidationOnly, doRunLocalTests, bypassError)

			return deployResultsJson
		}
	} catch (Exception e) {
		currentBuild.result = 'FAILED'
		throw e
	}
}

def deployToSalesforce(String sourcePath, Boolean doValidationOnly, Boolean doRunLocalTests, Boolean bypassError) {
	def testlevel = doRunLocalTests ? '--testlevel RunLocalTests' : ''
	def checkOnly = doValidationOnly ? '--checkonly' : ''

	def result = cmd("sfdx force:source:deploy --sourcepath ${sourcePath} ${checkOnly} ${testlevel} --verbose --json", bypassError)

	if (result != null) {
		if (!isUnix()) {
			result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
		}

		def deployResultJson = convertStringIntoJSON(result)

		echo 'Id :: ' + deployResultJson.result.id
		echo 'Status :: ' + deployResultJson.result.status
		echo 'Check Only :: ' + deployResultJson.result.checkOnly
		echo 'Number Components Deployed :: ' + deployResultJson.result.numberComponentsDeployed
		echo 'Number Components Total :: ' + deployResultJson.result.numberComponentsTotal
		echo 'Number Test Errors :: ' + deployResultJson.result.numberTestErrors
		echo 'Number Tests Completed :: ' + deployResultJson.result.numberTestsCompleted
		echo 'Number Tests Total :: ' + deployResultJson.result.numberTestsTotal
		echo 'Start Date :: ' + deployResultJson.result.startDate
		echo 'Completed Date :: ' + deployResultJson.result.completedDate
		echo 'Rollback On Error :: ' + deployResultJson.result.rollbackOnError

		return deployResultJson
	} else {
			echo 'Skipped the Deploy To Salesforcee due to an SFDX error...'
			return null
	}
}

/* BUILD NOTIFICATION METHODS */

def notifySuccessOnBuild(String packageName, String packageVersion, String packageInstallUrl){
	def subject = 'SUCCESS: Jenkins Build Successful'
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
	def subject = "ERROR: Jenkins Build Failed"
	def body = """<h1 style="background-color:red;font-size:42px;color:white;padding:10px;">Build Failed</h1>
		      <p>Job '${env.JOB_NAME} [${env.BRANCH_NAME} - ${env.BUILD_NUMBER}]':</p>
		      <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BRANCH_NAME} - ${env.BUILD_NUMBER}]</a>&QUOT;</p>
		      <p>Error: ${e}</p>"""

	echo "${body}"

	notifyByEmail(subject, body, env.EMAIL_RECIPIENTS)
}

/* UTILITIES METHODS */

def cmd(String command, Boolean bypassError = false) {
	def path = "\"${env.SFDX}\"" //adds '"' to the SFDX_HOME path in case there are spaces inside the path
	
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
		echo 'BYPASS ERROR :: ' + "${bypassError}"
		echo '===================='

		if (!bypassError) {
			throw ex
		}

		return null
	}
}

def convertStringIntoJSON(String jsonStr) {
	def json = readJSON text: jsonStr //need the Pipeline Utility Steps plugin
	return json
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
