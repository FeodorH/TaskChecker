pipeline {
    agent any

    environment {
            REGISTRY = 'docker.io'
            IMAGE = 'feodorh/taskchecker'
            LATEST_TAG = 'latest'
        }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Debug') {
            steps {
                sh 'pwd'
                sh 'cat gradle.properties || echo "gradle.properties not found"'
                sh './gradlew -q printVersion || echo "printVersion task not found"'
                sh './gradlew properties | grep version'
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
                script{
                    env.DOCKER_TAG = env.BRANCH_NAME + "-" + env.VERSION + "-dck"
                }
                withCredentials([usernamePassword(
                    credentialsId: 'docker-hub-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh """
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker build -t ${REGISTRY}/${IMAGE}:${env.DOCKER_TAG} .
                        docker push ${REGISTRY}/${IMAGE}:${env.DOCKER_TAG}

                        echo "Current branch: ${env.BRANCH_NAME}"
                        if [ "${env.BRANCH_NAME}" = "develop" ]; then
                            echo "Tagging and pushing latest"
                            docker tag ${REGISTRY}/${IMAGE}:${env.DOCKER_TAG} ${REGISTRY}/${IMAGE}:${LATEST_TAG}
                            docker push ${REGISTRY}/${IMAGE}:${LATEST_TAG}
                        fi
                    """
                }
            }
        }

        stage('Deploy') {
            when {
                expression { env.BRANCH_NAME == 'develop' }
            }
            steps {
                script {
                    def containerName = 'taskchecker-main'
                    def hostPort = 8080
                    def containerPort = 8080

                    sh """
                        docker pull ${REGISTRY}/${IMAGE}:${env.LATEST_TAG}
                        docker stop ${containerName} || true
                        docker rm ${containerName} || true
                        docker run -d \\
                            --name ${containerName} \\
                            -p ${hostPort}:${containerPort} \\
                            -e JAVA_OPTS='-Xmx512m' \\
                            -e SPRING_PROFILES_ACTIVE=prod\\
                            -e DB_URL=jdbc:postgresql://host.docker.internal:5432/taskdb \\
                            -e DB_USER=postgres \\
                            -e DB_PASSWORD=password \\
                            --restart unless-stopped \\
                            ${REGISTRY}/${IMAGE}:${env.LATEST_TAG}
                        sleep 5
                        docker ps | grep ${containerName} || (echo "Container failed to start" && exit 1)
                    """
                }
            }
        }

    }

    post {
        always { cleanWs() }
        success { echo "✅ Build succeeded for branch ${env.BRANCH_NAME}" }
        failure { echo "❌ Build failed for branch ${env.BRANCH_NAME}" }
    }
}
