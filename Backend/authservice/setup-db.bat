@echo off
echo Setting up Food Delivery Auth Service...
echo.

echo Starting MySQL database with Docker...
docker run -d --name auth-mysql ^
  -e MYSQL_DATABASE=auth_db ^
  -e MYSQL_ROOT_PASSWORD=password ^
  -e MYSQL_USER=authuser ^
  -e MYSQL_PASSWORD=authpass ^
  -p 3306:3306 ^
  mysql:8.0

echo.
echo Waiting for MySQL to start...
timeout /t 10 > nul

echo.
echo MySQL is ready! You can now start the auth service with start.bat
echo.
echo Database connection details:
echo URL: jdbc:mysql://localhost:3306/auth_db
echo Username: root
echo Password: password
echo.

pause
