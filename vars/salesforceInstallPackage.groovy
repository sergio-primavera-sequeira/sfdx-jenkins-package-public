#!/usr/bin/env groovy
import java.util.Date
import java.text.SimpleDateFormat

def call(String packageVersionId, String jwtCredentialId, String username, String instanceUrl, String consumerKey) {
		
	sfdx.init()

	try{
		withCredentials([file(credentialsId: jwtCredentialId, variable: 'jwt_key_file')]) {

			echo "=== SFDX AUTHENTICATION ==="
			authenticateToDevHub(username, instanceUrl, consumerKey, jwt_key_file)
			
			echo "=== SFDX INSTALL PACKAGE ==="
			def packageInstallId = initiatePackageInstallation(packageVersionId, username)
			echo 'Package Install ID :: ' + "${packageInstallId}"
			
			echo "=== SFDX INSTALL PACKAGE STATUS==="
			def packageInstallStatus = getPackageInstallStatus(packageInstallId, username)
			echo 'Package Install Status :: ' + "${packageInstallStatus}"
			
			return packageInstallStatus
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

def initiatePackageInstallation(String packageVersionId, String username){
	def result = sfdx.cmd("sfdx force:package:install --package ${packageVersionId} --wait 0 --apexcompile package --securitytype AdminsOnly --upgradetype Mixed --json --noprompt --targetusername ${username}")
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

	return packageVersionInstallResultJson.result.Id
}

def getPackageInstallStatus(String packageInstallId, String username){
	def result
	def packageVersionInstallResultJson
	def currrentStatus
	
	while(true){

		result = sfdx.cmd("sfdx force:package:install:report -i ${packageInstallId} --json --targetusername ${username}")
		result = result.readLines().drop(1).join(" ") //removes the first line of the output, for Windows only
		
		packageVersionInstallResultJson = json.convertStringIntoJSON(result)
		currrentStatus = packageVersionInstallResultJson.result.Status
		
		echo '======== LATEST PACKAGE INSTALL STATUS ========'
		
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
