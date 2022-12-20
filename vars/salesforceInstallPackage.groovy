#!/usr/bin/env groovy
import java.util.Date
import java.text.SimpleDateFormat

def call(String packageVersionId, String jwtCredentialId, String devHubUsername, String devHubInstanceUrl, String devHubConsumerKey) {
		
	sfdx.init()

	try{
		withCredentials([file(credentialsId: jwtCredentialId, variable: 'jwt_key_file')]) {

			echo "=== SFDX AUTHENTICATION ==="
			authenticateToDevHub(devHubUsername, devHubInstanceUrl, devHubConsumerKey, jwt_key_file)
			
			echo "=== SFDX INSTALL PACKAGE ==="
			installPackage(packageVersionId, devHubUsername)
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

def installPackage(String packageVersionId, String username){
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

	def PACKAGE_VERSION_INSTALL_ID = packageVersionInstallResultJson.result.Id
	echo 'PACKAGE_VERSION_INSTALL_ID :: ' + "${PACKAGE_VERSION_INSTALL_ID}"
	return PACKAGE_VERSION_INSTALL_ID;
}
