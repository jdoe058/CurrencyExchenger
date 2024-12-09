# Используем официальный образ Tomcat
FROM tomcat:10.1.33

# Копируем WAR файл в каталог для приложений Tomcat
COPY target/ROOT.war /usr/local/tomcat/webapps/

# Открываем порт 8080
EXPOSE 8080