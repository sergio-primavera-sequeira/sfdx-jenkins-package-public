#!/usr/bin/env groovy
import java.util.Date
import java.text.SimpleDateFormat

def call(String packageId, String jwtCredentialId, String devHubUsername, String devHubInstanceUrl, String devHubConsumerKey, Boolean bypassError) {
		
	sfdx.init()
	
	try{
		withCredentials([file(credentialsId: jwtCredentialId, variable: 'jwt_key_file')]) {
			
			echo "=== SFDX AUTHENTICATION ==="
			authenticateSalesforceOrg(devHubUsername, devHubInstanceUrl, devHubConsumerKey, jwt_key_file)
			
			echo "=== SFDX CREATE PACKAGE VERSION ==="
			def packageVersionJson = createPackageVersion(packageId, devHubUsername, bypassError)
			
			echo "=== SFDX LATEST PACKAGE VERSION ==="
			def lastestPackageVersionJson = getLastestPackageVersionCreationStatus(packageId, devHubUsername, bypassError)
			def subscriberPackageVersionId  = lastestPackageVersionJson.SubscriberPackageVersionId
			echo 'Subscriber Package Version ID :: ' + "${subscriberPackageVersionId}"
			
			echo "=== SFDX LATEST PACKAGE VERSION INFORMATION ==="
			def latestPackageInformationJson =  getLastestPackageVersionInformation(subscriberPackageVersionId, devHubUsername, bypassError)
			
			return latestPackageInformationJson  
		}
		
	} catch(Exception e) {
		currentBuild.result = 'FAILED'
		throw e
	}
}

def authenticateSalesforceOrg(String username, String instanceUrl, String connectedAppConsumerkey, Object jwtKeyfile){
	def result = sfdx.cmd("sfdx force:auth:jwt:grant --clientid ${connectedAppConsumerkey} --username ${username} --setdefaultusername --jwtkeyfile ${jwtKeyfile} --instanceurl ${instanceUrl}")
	echo "${result}"
}

def createPackageVersion(String packageId, String devHubUsername, Boolean bypassError){
	//--versionnumber parameter to override the sfdx-project.json value
	def result = sfdx.cmd("sfdx force:package:version:create --package ${packageId} --installationkeybypass --wait 0 --json --codecoverage --targetdevhubusername ${devHubUsername}", true)

	if(result != null){
		if (!isUnix()) {
			result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
		}
		
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
		
		return packageVersionResultJson
	} else {
		echo 'Skipped the Create Package Version Stage due to an SFDX error...'
		return null
	}
}

def getLastestPackageVersionCreationStatus(String packageId, String devHubUsername, Boolean bypassError){
	def result
	def packageCreationListResultJson
	def latestPackageCreation
	def currrentStatus

	while(true){
		result = sfdx.cmd("sfdx force:package:version:create:list -c 1 --json --targetdevhubusername ${devHubUsername}", bypassError)
		
		if(result != null){
			
			if (!isUnix()) {
				result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
			}
			
			packageCreationListResultJson = json.convertStringIntoJSON(result)
			latestPackageCreation = packageCreationListResultJson.result.findAll{ r -> r.Package2Id.equalsIgnoreCase(packageId) }.last()
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

			if(currrentStatus.equalsIgnoreCase('Success') || currrentStatus.equalsIgnoreCase('Error')) {
				break
			}

			sleep(time:10,unit:"SECONDS")
		} else {
			echo 'Skipped the Create Package Version Stage due to an SFDX error...'
			return null
		}
	}

	return latestPackageCreation
}

def getLastestPackageVersionInformation(String subscriberPackageVersionId, String devHubUsername, Boolean bypassError){
	def result = sfdx.cmd("sfdx force:package:version:list --verbose --json --targetdevhubusername ${devHubUsername}")
	
	if(result != null) {
		
		if (!isUnix()) {
			result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
		}
		
		def packageVersionListResultJson = json.convertStringIntoJSON(result)
		def latestPackageVersion = packageVersionListResultJson.result.findAll{ r -> r.SubscriberPackageVersionId.equalsIgnoreCase(subscriberPackageVersionId) }.last()

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
		echo 'Skipped the Package Promotion Stage due to an SFDX error...'
		return null
	}
}
