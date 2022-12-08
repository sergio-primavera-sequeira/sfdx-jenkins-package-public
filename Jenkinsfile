pipeline {
    
    agent any
    
    environment {
        toolbelt = '"C:\\Program Files\\sfdx\\bin"'
    }
    
    def cmd_sfdx(command) {
        echo "${toolbelt}/sfdx (${command}").trim()"
        return bat(returnStdout: true, script: "${toolbelt}/sfdx (${command}").trim()"
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
