#!/usr/bin/env groovy
import java.util.Date
import java.text.SimpleDateFormat

def call(String subscriberPackageVersionId, String jwtCredentialId, String username, String instanceUrl, String consumerKey) {
		
	sfdx.init()

	try{
		withCredentials([file(credentialsId: jwtCredentialId, variable: 'jwt_key_file')]) {

			echo "=== SFDX AUTHENTICATION ==="
			authenticateToDevHub(username, instanceUrl, consumerKey, jwt_key_file)
			
			echo "=== SFDX INSTALL PACKAGE ==="
			def packageInstallId = initiatePackageInstallation(subscriberPackageVersionId, username)
			echo 'Package Install ID :: ' + "${packageInstallId}"
			
			echo "=== SFDX INSTALL PACKAGE STATUS==="
			def packageInstallStatus = getPackageInstallationStatus(packageInstallId, username)
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
