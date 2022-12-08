pipeline {
    
    agent any
    
    environment {
        toolbelt = 'C:\\Program Files\\sfdx\\bin'
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
                echo "${toolbelt}\\sfdx help"
                bat '"${toolbelt}\\sfdx help"'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'  
            }
        }
    }
}
