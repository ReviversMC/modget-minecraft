pipeline {
    agent {
        docker {
            image 'openjdk:8-jdk'
        }
    }
    stages {
        stage('Build') {
            steps {
                sh './gradlew build'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'build/libs/*', fingerprint: true
                }
            }
        }
        stage('Publish') {
            when {
                expression {
                    return sh(returnStdout: true, script: 'git tag --contains').trim().length() > 0
                }
            }
            steps {
                sh './gradlew publish'
                withCredentials([string(credentialsId: 'curseforge_key', variable: 'CURSEFORGE_KEY')]) {
                    sh './gradlew -Pcurseforge.api_key="${CURSEFORGE_KEY}" curseforge'
                }
            }
        }
    }
}
