# 1 build app
#bắt đầu với 1 Maven image bao gồm JDK 21 để build app và đăt tên nó là build, 
#ta sd image này để làm nền,
#như thực tế thì ta tải về máy rồi nhưng đây là docker nên lấy image maven 
FROM maven:3.9.8-amazoncorretto-17 AS build
#nếu k sd image maven thì cũng có thể sd image linux đơn thuần có maven và java21
#nhưng mất thời gian hơn, phức tạp hơn.

#copy source code và pom.xml dự án hiện tại vào thư mục /app
#tạo thư mục app trong image maven ta mới kéo về ở trên
WORKDIR /app 
COPY pom.xml .
COPY src ./src

#tiến hành build file jar với maven
RUN mvn package -DskipTests
#=> ta sẽ có file jar trong thư muc app cua container


#2 tiến hành create images
#pull 1 image khác để tạo image vì để chạy jar chỉ cần java  là được
FROM amazoncorretto:17.0.4

#tương tự tạo thư mục /app trong image vừa lấy, và copy file jar vừa tạo ở bước 1 vô
#đặt tên nó là app.jar

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

#tạo chỉ dẫn cho Docker chạy image lên nó sẽ làm gì để chạy được ứng dụng
#giống câu lệnh ta chạy trong Teminal để chạy sau khi buld app ấy: java -jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]