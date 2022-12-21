#!/usr/bin/env groovy
import java.util.Date
import java.text.SimpleDateFormat

def call(String subscriberPackageVersionId, String jwtCredentialId, String username, String instanceUrl, String consumerKey, Boolean bypassError) {
		
	sfdx.init()

	try{
		withCredentials([file(credentialsId: jwtCredentialId, variable: 'jwt_key_file')]) {

			echo "=== SFDX AUTHENTICATION ==="
			authenticateSalesforceOrg(username, instanceUrl, consumerKey, jwt_key_file)
			
			echo "=== SFDX INSTALL PACKAGE ==="
			def packageVersionInstallResultJson = initiatePackageInstallation(subscriberPackageVersionId, username, bypassError)
			def packageInstallId = packageVersionInstallResultJson.result.Id
			echo 'Package Install ID :: ' + "${packageInstallId}"
			
			echo "=== SFDX INSTALL PACKAGE STATUS ==="
			def packageInstallStatus = getPackageInstallationStatus(packageInstallId, username, bypassError)
			echo 'Package Install Status :: ' + "${packageInstallStatus}"
			
			return packageInstallStatus
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

def initiatePackageInstallation(String subscriberPackageVersionId, String username, Boolean bypassError){
	def result = sfdx.cmd("sfdx force:package:install --package ${subscriberPackageVersionId} --wait 0 --apexcompile package --securitytype AdminsOnly --upgradetype Mixed --json --noprompt --targetusername ${username}", bypassError)
	
	if(result != null) {
		result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
		
		def packageVersionInstallResultJson = json.convertStringIntoJSON(result)

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
		echo 'Skipped the Package Promotion Stage due to an SFDX error...'
		return null
	}
}

def getPackageInstallationStatus(String packageInstallId, String username, Boolean bypassError){
	def result
	def packageVersionInstallResultJson
	def currrentStatus
	
	while(true){

		result = sfdx.cmd("sfdx force:package:install:report -i ${packageInstallId} --json --targetusername ${username}", bypassError)
		result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
		
		packageVersionInstallResultJson = json.convertStringIntoJSON(result)
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

		if(currrentStatus.equalsIgnoreCase('Success') || currrentStatus.equalsIgnoreCase('Error')) {
			break
		}

		sleep(time:10,unit:"SECONDS")
	}
	
	return packageVersionInstallResultJson.result.Status
}
