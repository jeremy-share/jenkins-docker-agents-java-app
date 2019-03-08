#!/usr/bin/env groovy

def version
def versionString
def jdkVersion="11"     // I was not able to make this come from .env or pom.xml
def mavenVersion="3"    // I was not able to make this come from .env

//---The CI itself------------------------------------------------------------
pipeline {
    agent any

    post {
        success {
            slackSend(message: "Jenkins Job successful ('${env.JOB_NAME}#${env.BUILD_NUMBER}) - Url: ${env.BUILD_URL}", color: "good")
        }
        failure {
            slackSend(message: "Jenkins Job FAILED! ('${env.JOB_NAME}#${env.BUILD_NUMBER}) - Url: ${env.BUILD_URL}", color: "danger")
        }
    }

    stages {
        stage('Checkout') {
            steps{
                checkout scm
            }
        }

        stage('Info') {
            agent {
                docker {
                    image "maven:$mavenVersion-jdk-$jdkVersion"
                }
            }
            steps{
                // Extract Info
                script {
                    // JDK Version
                    echo "jdkVersion: $jdkVersion"

                    // Maven Version
                    echo "mavenVersion: $mavenVersion"

                    // Note: The >2 below is to remove WARNINGS. See: https://github.com/ontodev/robot/issues/308

                    // Version e.g. 1.2.3-
                    version = sh(
                        script: "mvn -quiet -Dexec.executable=echo -Dexec.args='\${project.version}' --non-recursive exec:exec 2> /dev/null | sed 's/[^0-9.]*\\([0-9.]*\\).*/\\1/'",
                        returnStdout: true
                    ).trim()
                    echo "Detected Version: $version"

                    // Version String e.g. 1.2.3-SNAPSHOT
                    versionString = sh(
                        script: "mvn -quiet -Dexec.executable=echo -Dexec.args='\${project.version}' --non-recursive exec:exec 2> /dev/null",
                        returnStdout: true
                    ).trim()
                    echo "Detected versionString: $versionString"
                }
            }
        }

        stage('Maven Package') {
            agent {
                docker {
                    image "maven:$mavenVersion-jdk-$jdkVersion"
                }
            }
            steps{
                sh("mvn clean package")
            }
        }

        stage('Docker Build') {
            when {
                anyOf {
                    branch 'master'
                    branch 'develop'
                }
            }
            agent {
                docker {
                    image 'docker:latest'
                    args '-e HOME'
                }
            }
            steps {
                script {
                    sh("""
                            source .env \
                            && docker build \
                                --tag "\${DOCKER_IMAGE_NAME}:$env.GIT_COMMIT" \
                                --build-arg "APPLICATION_JDK_VERSION=$jdkVersion" \
                                --build-arg "APPLICATION_CODE=\${APPLICATION_CODE}" \
                                --build-arg "APPLICATION_NAME=\${APPLICATION_NAME}" \
                                --build-arg "APPLICATION_DESCRIPTION=\${APPLICATION_DESCRIPTION}" \
                                --build-arg "APPLICATION_VENDOR_NAME=\${APPLICATION_VENDOR_NAME}" \
                                --build-arg "APPLICATION_VERSION=$version" \
                                --build-arg "APPLICATION_VERSION_HASH=$env.GIT_COMMIT" \
                                --build-arg "APPLICATION_VERSION_STRING=$versionString" \
                                --build-arg "APPLICATION_BUILD_DATE=`date -u +\"%Y-%m-%dT%H:%M:%SZ\"`" \
                                --pull \
                                --file Dockerfile \
                                .
                    """)
                }
            }
        }

        stage('Docker Push Master') {
            when {
                anyOf {
                    branch 'master'
                }
            }
            agent {
                docker {
                    image 'docker:latest'
                    args '-e HOME'
                }
            }
            steps {
                script {
                    // Only push "latest" if its a master build
                    sh("source .env && docker tag \${DOCKER_IMAGE_NAME}:$env.GIT_COMMIT \${DOCKER_IMAGE_NAME}:latest")
                    sh("source .env && docker push \${DOCKER_IMAGE_NAME}:latest")

                    // Push the semantic version e.g. 1.2.3
                    sh("source .env && docker tag \${DOCKER_IMAGE_NAME}:$env.GIT_COMMIT \${DOCKER_IMAGE_NAME}:$version")
                    sh("source .env && docker push \${DOCKER_IMAGE_NAME}:$version")
                }
            }
        }

        stage('Docker Push Develop') {
            when {
                anyOf {
                    branch 'develop'
                }
            }
            agent {
                docker {
                    image 'docker:latest'
                    args '-e HOME'
                }
            }
            steps {
                script {
                    // Push the version string e.g. 1.2.3-SNAPSHOT
                    sh("source .env && docker tag \${DOCKER_IMAGE_NAME}:$env.GIT_COMMIT \${DOCKER_IMAGE_NAME}:$versionString")
                    sh("source .env && docker push \${DOCKER_IMAGE_NAME}:$versionString")
                }
            }
        }
    }
}
