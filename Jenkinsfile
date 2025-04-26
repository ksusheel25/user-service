pipeline {
    agent any

    stages {
        stage('Build') {
            agent {
                docker {
                    image 'eclipse-temurin:21-jdk'
                }
            }
            steps {
                echo 'Hello World'
                sh 'java --version'
            }
        }
    }
}
