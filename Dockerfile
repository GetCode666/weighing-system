# 替换为JDK21轻量基础镜像，和本地编译版本匹配
FROM openjdk:21-jdk-slim
WORKDIR /app

# 后端打包jar
COPY weighing-system-backend/target/*.jar app.jar
# 前端静态资源
COPY weighing-frontend/dist /app/static

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]