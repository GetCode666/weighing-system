pipeline {
    agent any
    tools {
        jdk "JDK21"
        maven "maven3"
        nodejs "node26"
    }
    environment {
        // 根据git分支自动确定环境
        ENV_NAME = ""
        BUILD_VERSION = ""
    }
    stages {
        stage('1.识别分支环境') {
            steps {
                script {
                    println "当前Git分支: ${env.BRANCH_NAME}"
                    if (env.BRANCH_NAME == 'dev') {
                        ENV_NAME = "dev"
                    } else if (env.BRANCH_NAME == 'release') {
                        ENV_NAME = "test"
                    } else if (env.BRANCH_NAME == 'master') {
                        ENV_NAME = "prod"
                    } else {
                        error("不支持的分支：${env.BRANCH_NAME}")
                    }
                    BUILD_VERSION = "${ENV_NAME}-build${env.BUILD_NUMBER}"
                    println("构建环境=${ENV_NAME},构建版本号=${BUILD_VERSION}")
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
                    npm config set registry https://registry.npmmirror.com
                    npm install
                    npm run build
                    '''
                }
            }
        }

        stage('4.归档构建产物') {
            steps {
                echo "归档jar包与前端dist包，构建版本：${BUILD_VERSION}"
            }
            post {
                always {
                    // 归档产物，Jenkins页面可以直接下载
                    archiveArtifacts artifacts: '''
weighing-system-backend/target/*.jar,
weighing-frontend/dist/**
''', fingerprint: true, allowEmptyArchive: false
                }
            }
        }
    }

    post {
        success {
            echo "✅流水线打包完成！版本：${BUILD_VERSION}"
            echo "👉 Jenkins页面【构建产物】可以下载后端jar、前端dist包"
        }
        failure {
            echo "❌流水线打包执行失败！查看控制台日志排查问题"
        }
    }
}