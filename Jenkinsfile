pipeline {
    agent any

    environment {
        SPRING_PROFILE_DEPLOY = 'prod'
        SPRING_PROFILE_DEV = 'dev'
        DEPLOY_BRANCH = 'master'

        // Docker
        REGISTRY = 'docker.io'
        IMAGE = 'feodorh/taskchecker'
        LATEST_TAG = 'latest'

        // Spring
        CONTAINER_NAME = 'taskchecker-main'
        HOST_PORT = '8080'
        CONTAINER_PORT = '8080'

        // Postgre
        DB_HOST = 'host.docker.internal'
        DB_PORT = '5432'
        DB_NAME = 'taskdb'
        DB_USER = 'postgres'
        DB_PASSWORD = 'password'

        //H2
        H2_URL = 'jdbc:h2:mem:testdb'
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
                        if [ "${env.BRANCH_NAME}" = "${DEPLOY_BRANCH}" ]; then
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
                expression { env.BRANCH_NAME == DEPLOY_BRANCH }
            }
            steps {
                script {
                    sh """
                        echo "Checking PostgreSQL availability at ${DB_HOST}:${DB_PORT}..."
                        if nc -zv ${DB_HOST} ${DB_PORT} 2>/dev/null; then
                            echo "✅ PostgreSQL is available, using prod profile"
                            PROFILE=${SPRING_PROFILE_DEPLOY}
                            DB_URL="jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}"
                        else
                            echo "❌ PostgreSQL not available, using dev profile with H2"
                            PROFILE=${SPRING_PROFILE_DEV}
                            DB_URL=${H2_URL}
                        fi

                        docker pull ${REGISTRY}/${IMAGE}:${env.LATEST_TAG}
                        docker stop ${CONTAINER_NAME} || true
                        docker rm ${CONTAINER_NAME} || true
                        docker run -d \\
                            --name ${CONTAINER_NAME} \\
                            -p ${HOST_PORT}:${CONTAINER_PORT} \\
                            -e SPRING_PROFILES_ACTIVE=\${PROFILE}\\
                            -e DB_URL=\${DB_URL} \\
                            -e DB_USER=${DB_USER} \\
                            -e DB_PASSWORD=${DB_PASSWORD} \\
                            --restart unless-stopped \\
                            ${REGISTRY}/${IMAGE}:${env.LATEST_TAG}
                        sleep 5
                        docker ps | grep ${CONTAINER_NAME} || (echo "Container failed to start" && exit 1)
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
