pipeline {
    
    agent any
    
    environment {
        toolbelt = 'C:\\Program Files\\sfdx\\bin\\'
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
                bat '"C:\\Program Files\\sfdx\\bin\\sfdx" help'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'  
            }
        }
    }
}
