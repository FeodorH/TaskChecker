pipeline {
    agent any

    environment {
            REGISTRY = 'docker.io'
            IMAGE = 'feodorh/taskchecker'
            LATEST_TAG = 'latest'
            version = '0.0.0'
        }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('SetVersion') {
            steps {
                script {
                    env.VERSION = sh(script: './gradlew -q printVersion', returnStdout: true).trim()
                    echo "Version from Gradle: ${env.VERSION}"
                }
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew build'
            }
        }

        stage('Test') {
            steps {
                sh './gradlew test'
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'build/libs/*.jar'
            }
        }

        stage('Docker Build & Push') {
            when {
                expression { env.BRANCH_NAME == 'develop' || env.BRANCH_NAME == 'master' }
            }
            steps {
                env.DOCKER_TAG = env.BRANCH_NAME + ":" + env.VERSION + "-dck"
                withCredentials([usernamePassword(
                    credentialsId: 'docker-hub-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh """
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker build -t ${REGISTRY}/${IMAGE}:${env.DOCKER_TAG} .
                        docker push ${REGISTRY}/${IMAGE}:${env.DOCKER_TAG}

                        if [ "${env.BRANCH_NAME}" == "master" ]; then
                            docker tag ${REGISTRY}/${IMAGE}:${env.DOCKER_TAG} ${REGISTRY}/${IMAGE}:${LATEST_TAG}
                            docker push ${REGISTRY}/${IMAGE}:${LATEST_TAG}
                        fi
                    """
                }
            }
        }

        stage('Deploy') {
            when {
                expression { env.BRANCH_NAME == 'master' }
            }
             //TODO: релиз здесь
        }

        post {
            always { cleanWs() }
            success { echo "✅ Build succeeded for branch ${env.BRANCH_NAME}" }
            failure { echo "❌ Build failed for branch ${env.BRANCH_NAME}" }
        }
    }
}
