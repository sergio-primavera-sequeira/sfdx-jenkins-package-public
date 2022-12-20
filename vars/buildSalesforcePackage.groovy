#!/usr/bin/env groovy
import java.util.Date
import java.text.SimpleDateFormat

def call(String packageNameOrId, String jwtCredentialId, String devHubUsername, String devHubInstanceUrl, String devHubConsumerKey) {
		
	sfdx.init()
	
	try{
		withCredentials([file(credentialsId: jwtCredentialId, variable: 'jwt_key_file')]) {
			
			echo "=== SFDX AUTHENTICATION ==="
			authenticateToDevHub(devHubUsername, devHubInstanceUrl, devHubConsumerKey, jwt_key_file)
			
			echo "=== SFDX CREATE PACKAGE VERSION ==="
			createPackageVersion(packageNameOrId, devHubUsername)
			
			echo "=== SFDX LATEST PACKAGE VERSION ==="
			
			def PACKAGE_VERSION = getLastestPackageVersionCreated(packageNameOrId, devHubUsername)
			echo 'PACKAGE_VERSION :: ' + "${PACKAGE_VERSION}"
			
		}
		
	} catch(Exception e) {
		currentBuild.result = 'FAILED'
		throw e
	}
}

def authenticateToDevHub(String username, String instanceUrl, String connectedAppConsumerkey, Object jwtKeyfile){
	def result = sfdx.cmd("sfdx force:auth:jwt:grant --clientid ${connectedAppConsumerkey} --username ${username} --setdefaultusername --jwtkeyfile ${jwtKeyfile} --instanceurl ${instanceUrl}")
	echo "${result}"
}

def createPackageVersion(String packageNameOrId, String devHubUsername){
	//--versionnumber parameter to override the sfdx-project.json value
	def result = sfdx.cmd("sfdx force:package:version:create --package ${packageNameOrId} --installationkeybypass --wait 1 --json --codecoverage --targetdevhubusername ${devHubUsername}", true)

	if(result != null){
		result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only

		def packageVersionResultJson = json.convertStringIntoJSON(result)

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

	} else {
		echo 'Skipped the Create Package Version Stage due to an SFDX error...'
	}
}

def getLastestPackageVersionCreated(String packageNameOrId, String devHubUsername){
	def result
	def packageCreationListResultJson
	def latestPackageCreation
	def currrentStatus

	while(true){

		result = sfdx.cmd("sfdx force:package:version:create:list -c 1 --json --targetdevhubusername ${devHubUsername}")
		result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
		
		packageCreationListResultJson = json.convertStringIntoJSON(result)
		latestPackageCreation = packageCreationListResultJson.result.last()
		currrentStatus = latestPackageCreation.Status

		echo '======== Lastest Package Creation Status ========'

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

		if(currrentStatus.equalsIgnoreCase('Success') || currrentStatus.equalsIgnoreCase('Error')) {
			break
		}

		sleep(time:10,unit:"SECONDS")
	}

	return latestPackageCreation.SubscriberPackageVersionId
}
