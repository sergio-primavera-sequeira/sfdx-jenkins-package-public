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
                bat '"C:\Program Files\sfdx\bin\sfdx.cmd" help'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'  
            }
        }
    }
}
