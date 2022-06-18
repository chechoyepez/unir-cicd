pipeline {
    agent any
    stages {
        stage('Source') {
            steps {                
                git 'https://github.com/chechoyepez/unir-cicd.git'
            }
        }
        stage('Build') {
            steps {
                echo 'Building stage!'
                sh 'make build'
            }
        }
        stage('Unit tests') {
            steps {
                sh 'make test-unit'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
        stage('API tests') {
            steps {
                sh 'make test-api'
                archiveArtifacts artifacts: 'results/api_result.xml'
            }
        }
        stage('E2E tests') {
            steps {
                sh 'make test-e2e'
                archiveArtifacts artifacts: 'results/cypress_result.xml'
            }
        }
    }
    post {
        always {
            junit 'results/*_result.xml'
            cleanWs()
        }
        failure{
            echo "El pipeline ha terminado con ERRORES"
            emailext body: "${currentBuild.currentResult}:. Tarea ${env.JOB_NAME}. Build # ${env.BUILD_NUMBER}\n Más información en: ${env.BUILD_URL}",
                recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']],
                subject: "Estado de Jenkins Build: ${currentBuild.currentResult}. Tarea: ${env.JOB_NAME}"
        }
        success{
            echo "Pipeline ha terminado con EXITO"
        }
    }
}

