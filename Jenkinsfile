pipeline {
    agent any

    environment {
        REPO_URL = 'https://github.com/Gooaein/GoojilGoojil-BE.git'
        BRANCH = 'dev'
        IMAGE_NAME = 'dongkyeomjang/goojilgoojil'
    }

    stage('Clone') {
        steps {

                // dev 브랜치로부터 코드 클론
                git branch: "${BRANCH}", url: "${REPO_URL}"
            }
        }

    stage('Download application-dev.yml from S3') {
                steps {
                    script {
                        // 매개변수로 전달된 yml 값을 이용해 S3에서 파일 다운로드
                        echo "Downloading application-dev.yml from ${params.yml}"
                        sh "curl -o src/main/resources/application-dev.yml ${params.yml}"
                    }
                }
            }

        stage('Build with Gradle') {
            steps {
                // Gradle을 사용하여 bootJar 빌드
                sh './gradlew bootJar'
            }
        }

        stage('Build Docker Image') {
            steps {
                // Docker 이미지를 빌드
                sh 'docker build -t ${IMAGE_NAME} .'
            }
        }

        stage('Run Docker Container') {
            steps {
                // Docker 컨테이너 실행
                sh 'docker run -d --network app-network -p 8081:8080 ${IMAGE_NAME}'
            }
        }

        stage('Clean up') {
            steps {
                // 클론 받은 디렉토리 삭제
                deleteDir()
            }
        }
    }
}
