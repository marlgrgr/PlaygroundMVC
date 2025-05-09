pipeline {
    agent any
    
    parameters {
        choice(name: 'ENVIRONMENT', choices: ['local', 'production'], description: 'Select the deployment environment')
    }
    
    environment {
        CONFIG_FILE_ID = "playground-application-properties-${ENVIRONMENT}"
        REDISSON_CONFIG_FILE_ID = "playground-redisson-${ENVIRONMENT}"
        CREDENTIALS_ID = "playground-credentials-${ENVIRONMENT}"
        DOCKER_IMAGE = "playground-mvc"
        APP_NAME = "playground-mvc"
    }
    
    tools {
        maven 'Maven 3'
        jdk 'JDK 21'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipUnitTest=true'
            }
        }
        stage('Unit Test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Integration Test') {
            steps {
                sh 'mvn verify -DskipUnitTest=true -DskipIntegrationTest=false'
            }
        }
        
        stage('JaCoCo HTML Report') {
            steps {
                publishHTML(target: [
                    reportDir: 'target/site/jacoco',
                    reportFiles: 'index.html',
                    reportName: 'JaCoCo Coverage Report',
                    keepAll: true,
                    alwaysLinkToLastBuild: true,
                    allowMissing: false
                ])
            }
        }
        
        stage('Prepare Config') {
            steps {
                configFileProvider([configFile(fileId: "${CONFIG_FILE_ID}", targetLocation: 'config/application.properties')]) {
                    echo "Base application properties file loaded from Config File Manager"
                }
                
                configFileProvider([configFile(fileId: "${REDISSON_CONFIG_FILE_ID}", targetLocation: 'config/redisson.yaml')]) {
                    echo "Redisson file loaded from Config File Manager"
                }
            }
        }
        
        stage('Docker Build') {
            steps {
                script {
                    sh "docker build -t ${DOCKER_IMAGE}:latest ."
                }
            }
        }
        
        stage('Deploy') {
            steps {
                script {
                    withCredentials([string(credentialsId: "${CREDENTIALS_ID}", variable: 'JAVA_OPTS_SECRETS')]) {
                        sh """
                            docker stop ${APP_NAME} || true
                            docker rm ${APP_NAME} || true
                            
                            docker run -d --name ${APP_NAME} \
                                -e "JAVA_OPTS=\${JAVA_OPTS_SECRETS}" \
                                -p 8081:8081 \
                                ${DOCKER_IMAGE}:latest
                        """
                    }
                }
            }
        }
        
        stage('Verify Deployment') {
            steps {
                sh "docker logs ${APP_NAME}"
                sh "docker ps | grep ${APP_NAME}"
            }
        }
        
        stage('Cleanup Dangling Images') {
            steps {
                script {
                    sh """
                        echo "Cleanup Dangling Images"
                        docker image prune -f
                    """
                }
            }
        }
    }
    post {
        always {
            junit '**/target/surefire-reports/*.xml'
            junit '**/target/failsafe-reports/*.xml'
        }
        success {
            echo 'The pipeline has been completed successfully'
        }
        failure {
            echo 'The pipeline has failed'
        }
    }
}