pipeline {
    agent any
    tools {
            jdk "JDK21"
            maven "maven3"
            nodejs "node26"
        }
    environment {
        // harbor地址、项目名，改成你自己的
        HARBOR_URL = "harbor.devpilot.com.cn"
        HARBOR_PROJECT = "weighing"
        IMAGE_NAME = "weighing-system"
        // 根据git分支自动确定环境
        ENV_NAME = ""
        IMAGE_TAG = ""
    }
    stages {
        stage('1.识别分支环境') {
            steps {
                script {
                    println "当前分支: ${env.BRANCH_NAME}"
                    if (env.BRANCH_NAME == 'dev') {
                        ENV_NAME = "dev"
                    } else if (env.BRANCH_NAME == 'release') {
                        ENV_NAME = "test"
                    } else if (env.BRANCH_NAME == 'master') {
                        ENV_NAME = "prod"
                    } else {
                        error("不支持的分支：${env.BRANCH_NAME}")
                    }
                    IMAGE_TAG = "${ENV_NAME}-${env.BUILD_NUMBER}"
                    println("环境=${ENV_NAME},镜像tag=${IMAGE_TAG}")
                }
            }
        }

        stage('2.后端Maven打包Java') {
            steps {
                dir("weighing-system-backend") {
                    sh '''
                    mvn clean package -DskipTests
                    '''
                }
            }
        }

        stage('3.前端Vue编译打包') {
            steps {
                dir("weighing-frontend") {
                    sh '''
                    npm install
                    npm run build
                    '''
                }
            }
        }

        stage('4.构建Docker镜像并推送Harbor') {
            steps {
                // 注意：Dockerfile放在仓库根目录
                sh """
                docker build -t ${HARBOR_URL}/${HARBOR_PROJECT}/${IMAGE_NAME}:${IMAGE_TAG} .
                docker push ${HARBOR_URL}/${HARBOR_PROJECT}/${IMAGE_NAME}:${IMAGE_TAG}
                """
            }
        }

        // ==========【可选】远程SSH部署到业务服务器，不需要可以直接删掉这个stage ==========
        stage('5.远程服务器部署应用') {
            steps {
                sshPublisher(publishers: [sshPublisherDesc(
                    configName: '业务服务器ssh配置', // Jenkins系统配置里的SSH Server name
                    transfers: [sshTransfer(
                        sourceFiles: '',
                        remoteDirectory: '/opt/app/weighing',
                        execCommand: """
cd /opt/app/weighing
docker compose down
docker compose pull ${HARBOR_URL}/${HARBOR_PROJECT}/${IMAGE_NAME}:${IMAGE_TAG}
docker compose up -d
"""
                    )]
                )])
            }
        }
    }

    post {
        success {
            echo "✅流水线构建成功，镜像：${HARBOR_URL}/${HARBOR_PROJECT}/${IMAGE_NAME}:${IMAGE_TAG}"
        }
        failure {
            echo "❌流水线执行失败！"
        }
    }
}