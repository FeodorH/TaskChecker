TaskChecker
TaskChecker — это бэкенд-сервис для управления задачами, разработанный на Kotlin с использованием Spring Boot 3.
Проект предоставляет REST API с полным CRUD-функционалом, валидацией данных, защитой от дубликатов и глобальной обработкой ошибок.

🚀 Стек технологий
Технология	Назначение
Kotlin 2.0.20	Основной язык разработки
Spring Boot 3.3.0	Фреймворк для создания веб-приложений
Spring Web MVC	REST API, обработка HTTP-запросов
Spring Data JPA + Hibernate	ORM, работа с базой данных
Spring Boot Starter Validation	Валидация входящих данных
H2 Database (in-memory)	Реляционная БД для разработки
HikariCP	Пул соединений с БД
Gradle (Kotlin DSL)	Система сборки
Java 21	Платформа для запуска

🏗️ Архитектура
Проект построен на классической трёхуровневой архитектуре Controller → Service → Repository с использованием DTO и маппера для разделения API-контрактов и сущностей БД.


src/main/resources/
└── application.yml                      # Конфигурация приложения
🔌 API Эндпоинты
Метод	URL	Описание
GET	/api/tasks	Получить все задачи
GET	/api/tasks/themes	Получить все уникальные темы
GET	/api/tasks/by-id?id={id}	Получить задачу по ID
GET	/api/tasks/by-title?title={title}	Поиск по названию
GET	/api/tasks/by-theme?theme={theme}	Поиск по теме
GET	/api/tasks/by-author?author={author}	Поиск по автору
GET	/api/tasks/is-started	Получить запущенные задачи
POST	/api/tasks	Создать задачу (JSON Task)
PUT	/api/tasks/{id}	Обновить задачу (JSON Task)
DELETE	/api/tasks/{id}	Удалить задачу по ID

📦 Примеры запросов
  Создание задачи (POST /api/tasks)
{
  "title": "Изучить Kotlin",
  "theme": "Обучение",
  "author": "Алексей",
  "description": "Пройди курс и сделай проект",
  "isStarted": false
}
  Ответ (201 Created):
{
  "id": 1,
  "title": "Изучить Kotlin",
  "theme": "Обучение",
  "author": "Алексей",
  "description": "Пройди курс и сделай проект",
  "isStarted": false,
  "updateAt": "2026-07-07T18:00:00.000Z"
}

  Получение всех задач (GET /api/tasks)
GET http://localhost:8080/api/tasks

  Обновление задачи (PUT /api/tasks/1)
{
  "title": "Изучить Kotlin и Spring",
  "theme": "Обучение",
  "author": "Алексей",
  "description": "Обновлённое описание",
  "isStarted": true
}
  Удаление задачи (DELETE /api/tasks/1)
DELETE http://localhost:8080/api/tasks/1

  H2 Console
http://localhost:8080/h2-console

📌 Особенности
✅ Полный CRUD с валидацией
✅ Защита от дубликатов (проверка на уровне сервиса)
✅ Глобальная обработка исключений (@RestControllerAdvice)
✅ Автоматическое обновление времени (@UpdateTimestamp)
✅ Чистое разделение слоёв (Controller → Service → Repository)
✅ DTO + Mapper для изоляции API от сущностей БД
✅ In-memory БД для быстрой разработки
✅ Настройка через application.yml

🧠 Чему научился в этом проекте
Создание REST API на Kotlin + Spring Boot 3
Работа с Spring Data JPA и Hibernate
Проектирование архитектуры Controller-Service-Repository
Валидация данных и обработка ошибок
Использование DTO и мапперов
Настройка H2 Database и подключение к ней
Работа с Gradle (Kotlin DSL)

📄 Лицензия
Этот проект создан в учебных целях. Свободно используйте для портфолио и обучения.

🔗 Контакты
Автор: FeodorH
GitHub: https://github.com/FeodorH
Email: fholkin@yandex.ru
Telegram: @feodorH
