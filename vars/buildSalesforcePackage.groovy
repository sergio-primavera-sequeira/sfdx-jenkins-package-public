#!/usr/bin/env groovy
import java.util.Date
import java.text.SimpleDateFormat

def call(String devHub = 'none', String packageName) {
	
	def DEV_HUB_ORG_JWT_KEY_CRED_ID="sf-jwt-key"
	def DEV_HUB_ORG_USER="integration.jenkins@sfjenkins.poc.org01.ca"
	def DEV_HUB_ORG ="https://login.salesforce.com" 
	def DEV_HUB_ORG_CONNECTED_APP_CONSUMER_KEY="3MVG9ux34Ig8G5epoz.M1VfJxB82Qyj0J57NXfZmSeZWN5XytkVPTKSj7C9J.QYiwbdkPpmv9X0Efg0CKRXIX"
	
	sfdx.init()
	
	try{
		withCredentials([file(credentialsId: DEV_HUB_ORG_JWT_KEY_CRED_ID, variable: 'jwt_key_file')]) {
			
			echo "=== SFDX AUTHENTICATION ==="
			authenticateToDevHub(DEV_HUB_ORG_CONNECTED_APP_CONSUMER_KEY, DEV_HUB_ORG_USER, DEV_HUB_ORG, jwt_key_file)
			
			echo "=== SFDX CREATE PACKAGE VERSION ==="
			createPackageVersion(packageName, DEV_HUB_ORG_USER)
			
		}
	} catch(Exception e) {
		currentBuild.result = 'FAILED'
		throw e
	}
}

def authenticateToDevHub(String connectedAppConsumerkey, String username, String instanceUrl, Object jwtKeyfile){
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
