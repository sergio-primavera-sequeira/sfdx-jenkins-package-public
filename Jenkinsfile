pipeline {
    
    agent any
        
    stages {
        stage('Build') {
            steps {
                echo 'Building..'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
                bat ' cd "C:\Program Files\sfdx\bin\" 
                     sfdx "force:auth:jwt:grant"'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'  
            }
        }
    }
}
