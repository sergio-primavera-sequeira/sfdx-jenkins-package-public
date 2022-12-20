#!/usr/bin/env groovy
import java.util.Date
import java.text.SimpleDateFormat

def call(String DEV_HUB = 'none') {
}

def configJob(){
	echo 'Starting sfdx'
  
	def result = cdmSfdx("--version")
	echo "${result}"
}

def cdmSfdx(String command, Boolean bypassError = false) {
    	def path = "\"${SFDX_HOME}\"" //adds '"' to the SFDX_HOME path in case there are spaces inside the path
	
	try {
	    if (isUnix()) {
		return sh(returnStdout: true, script: "${path}/sfdx ${command}")
	    } else {
		return bat(returnStdout: true, script: "${path}/sfdx ${command}").trim()
	    }
	} catch (Exception ex) {				
		echo '==== SFDX ERROR ===='
		echo ex.toString()
		echo '===================='
				
		if(!bypassError){
			throw ex
		}
		
		return null
	}	
}

def convertStringIntoJSON(String jsonStr) {
    def json = readJSON text: jsonStr //need the Pipeline Utility Steps plugin
    return json
}
