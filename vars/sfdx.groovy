def call(){}

def init(){
	
	echo 'Starting sfdx'
	
	//SFDX HOME: an SFDX custom tool needs to be configured and the 'Tool Home' (when 'Install automatically' is checked) on custom tools needs to be configured
	env.SFDX_HOME = tool name: 'sfdx', type: 'com.cloudbees.jenkins.plugins.customtools.CustomTool' 
	
	def result = cmd("sfdx --version")
	echo "${result}"
}

def cmd(String command, Boolean bypassError = false) {
	
    	def path = "\"${env.SFDX_HOME}\"" //adds '"' to the SFDX_HOME path in case there are spaces inside the path
	
	try {
	    if (isUnix()) {
		return sh(returnStdout: true, script: "${path}/sfdx ${command}")
	    } else {
		return bat(returnStdout: true, script: "${path}/${command}").trim()
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
