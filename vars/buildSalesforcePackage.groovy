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
			createPackageVersion(packageName, devHubUsername)
			
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

def createPackageVersion(String packageName, String devHubUsername){
	//--versionnumber parameter to override the sfdx-project.json value
	def result = sfdx.cmd("sfdx force:package:version:create --package ${packageName} --installationkeybypass --wait 0 --json --codecoverage --targetdevhubusername ${devHubUsername}", true)

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
