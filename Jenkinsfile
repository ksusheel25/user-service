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
                sh 'chmod +x ./gradlew'
                sh './gradlew clean build'
                sh 'ls -la'
                sh 'ls -la build/libs'
                sh './gradlew clean build --info' 
            }
        }

        stage('Test') {
            agent {
                docker {
                    image 'eclipse-temurin:21-jdk'
                }
            }
            steps {
                sh 'chmod +x ./gradlew'
                sh './gradlew test'
            }
        }

        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
            }
        }
    }

    post {
        always {
            echo 'Cleaning up workspace...'
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
