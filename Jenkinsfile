pipeline {
    
    agent any
    
    environment {
        toolbelt = '"C:\\Program Files\\sfdx\\bin"'
    }
    
    stages {
        stage('Build') {
            steps {
                echo 'Building..'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
                //echo "${toolbelt}/sfdx help"
                //bat "${toolbelt}/sfdx help"
                cmd_sfdx("help")
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'  
            }
        }
    }
}

def cmd_sfdx(command) {
    sfdx_cmd = "${toolbelt}/sfdx ${command}"
    echo "${sfdx_cmd}"
    //return bat(returnStdout: true, script: "${toolbelt}/sfdx ${command})"
}
