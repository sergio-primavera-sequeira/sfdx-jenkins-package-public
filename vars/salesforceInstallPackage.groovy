
#!/usr/bin/env groovy
import java.util.Date
import java.text.SimpleDateFormat

def call(String packageId, String jwtCredentialId, String devHubUsername, String devHubInstanceUrl, String devHubConsumerKey) {
		
	sfdx.init()

	try{
		withCredentials([file(credentialsId: jwtCredentialId, variable: 'jwt_key_file')]) {

			echo "=== SFDX AUTHENTICATION ==="
			authenticateToDevHub(devHubUsername, devHubInstanceUrl, devHubConsumerKey, jwt_key_file)

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
