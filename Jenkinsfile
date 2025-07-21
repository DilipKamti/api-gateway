pipeline {
    agent any

    environment {
        IMAGE_NAME = "dilipkamti/api-gateway"
        IMAGE_TAG_PREFIX = "v"
        VERSION_FILE = ".docker-version"
    }

    parameters {
        string(name: 'VERSION', defaultValue: '1.0.0', description: 'Version of the API Gateway')
        booleanParam(name: 'DELETE_OLD_BUILDS', defaultValue: false, description: 'Delete old builds before deploying')
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/DilipKamti/api_gateway.git'
            }
        }

        stage('Determine Docker Image Version') {
            steps {
                script {
                    def currentVersion = '0.0'
                    if (fileExists(env.VERSION_FILE)) {
                        currentVersion = readFile(env.VERSION_FILE).trim()
                    }
                    def (major, minor) = currentVersion.tokenize('.').collect { it.toInteger() }
                    def newVersion = "${major}.${minor + 1}"
                    env.DOCKER_VERSION = "${IMAGE_TAG_PREFIX}${newVersion}"
                    writeFile file: env.VERSION_FILE, text: newVersion
                    echo "New Docker version: ${env.DOCKER_VERSION}"
                }
            }
        }

        stage('Clean Old Docker Resources') {
            when {
                expression { return params.DELETE_OLD_BUILDS }
            }
            steps {
                script {
                    if (isUnix()) {
                        sh '''
                            docker ps -a --filter "ancestor=dilipkamti/api-gateway" --format "{{.ID}}" | xargs -r docker stop || true
                            docker ps -a --filter "ancestor=dilipkamti/api-gateway" --format "{{.ID}}" | xargs -r docker rm || true
                            docker images dilipkamti/api-gateway --format "{{.Repository}}:{{.Tag}}" | grep -v ${DOCKER_VERSION} | xargs -r docker rmi -f || true
                        '''
                    } else {
                        bat """
                            for /f "delims=" %%i in ('docker ps -a --filter "ancestor=dilipkamti/api-gateway" --format "{{.ID}}"') do (
                                docker stop %%i
                                docker rm %%i
                            )
                            powershell -Command "docker images dilipkamti/api-gateway --format '{{.Repository}}:{{.Tag}}' | Where-Object { \$_ -ne '${IMAGE_NAME}:${DOCKER_VERSION}' } | ForEach-Object { docker rmi -f \$_ }"
                        """
                    }
                }
            }
        }

        stage('Build Maven Project') {
            steps {
                script {
                    def mvnCmd = "mvn clean package -DskipTests"
                    if (isUnix()) {
                        sh mvnCmd
                    } else {
                        bat mvnCmd
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    def versionTag = "${IMAGE_NAME}:${DOCKER_VERSION}"
                    def buildCmd = "docker build -t ${versionTag} ."
                    if (isUnix()) {
                        sh buildCmd
                    } else {
                        bat buildCmd
                    }
                }
            }
        }

        stage('Login to Docker Hub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USERNAME',
                    passwordVariable: 'DOCKER_PASSWORD'
                )]) {
                    script {
                        if (isUnix()) {
                            sh "echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin"
                        } else {
                            bat "echo %DOCKER_PASSWORD% | docker login -u %DOCKER_USERNAME% --password-stdin"
                        }
                    }
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    def versionTag = "${IMAGE_NAME}:${DOCKER_VERSION}"
                    if (isUnix()) {
                        sh "docker push ${versionTag}"
                    } else {
                        bat "docker push ${versionTag}"
                    }
                }
            }
        }

        stage('Deploy (Optional)') {
            steps {
                echo "✅ Docker image pushed successfully: ${IMAGE_NAME}:${DOCKER_VERSION}"
                // Add your deployment logic here (SSH, docker-compose, etc.)
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo "✅ Build and deployment successful with Docker tag: ${DOCKER_VERSION}"
        }
        failure {
            echo "❌ Build or deployment failed!"
        }
    }
}
