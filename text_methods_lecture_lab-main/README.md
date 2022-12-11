# text_methods_lecture_lab
Как запускать через докер компоуз:
1. Собрать весь проект через gradle build
2. docker-compose -f .\docker-compose.dev.yml up --build
3. Ссылки:
  1. Проверить состояние frontend:
      http://localhost:8090/tm-frontend/login
  2. Проверить состояние backend:
      http://localhost:8089/tm-backend/rest/health_check

Логин/пароль администратора: admin/setup

Веб клиент для БД: http://localhost:8085
