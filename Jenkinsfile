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
        withCredentials([file(credentialsId: 'sf-jwt-key', variable: 'jwt_key_file')]) {
            stage('Test') {
                steps {
                    echo 'Testing..'
                    //echo "${toolbelt}/sfdx help"
                    //bat "${toolbelt}/sfdx help"
                    script {
                        def rec = cmd_sfdx("help")
                        echo "${rec}"
                    }
                }
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
    echo "${toolbelt}/sfdx ${command}"
    return bat(returnStdout: true, script: "${toolbelt}/sfdx ${command}")
}
