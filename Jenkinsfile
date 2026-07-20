pipeline {
    agent {
        docker {
            image 'maven:3.9.6-eclipse-temurin-17'
            args '-v /var/run/docker.sock:/var/run/docker.sock:z -v /usr/bin/docker:/usr/bin/docker --user=root'
        }
    }

    options {
        timeout(time: 20, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/kridency/FinanceTracker'
            }
        }

        stage('Run Tests') {
            environment {
                TESTCONTAINERS_RYUK_DISABLED = 'true'
            }
            steps {
                sh 'mvn clean package'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("finance-tracker:latest", ".")
                }
                echo 'Docker-образ finance-tracker:latest успешно собран!'
            }
        }
    }
}