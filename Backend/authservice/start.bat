@echo off
echo Starting Food Delivery Auth Service...
echo.

REM Check if MySQL is running
echo Checking MySQL connection...
timeout /t 2 > nul

REM Start the Spring Boot application
echo Starting Spring Boot application...
.\mvnw.cmd spring-boot:run

pause
