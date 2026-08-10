# TaskChecker

**TaskChecker** — это бэкенд-сервис для управления задачами, разработанный на **Kotlin** с использованием **Spring Boot 3**.  
Проект предоставляет REST API с полным CRUD-функционалом, валидацией данных, защитой от дубликатов и глобальной обработкой ошибок.

---

## 🚀 Стек технологий

| Технология | Назначение |
|------------|------------|
| **Kotlin 2.0.20** | Основной язык разработки |
| **Spring Boot 3.3.0** | Фреймворк для создания веб-приложений |
| **Spring Web MVC** | REST API, обработка HTTP-запросов |
| **Spring Data JPA + Hibernate** | ORM, работа с базой данных |
| **Spring Boot Starter Validation** | Валидация входящих данных |
| **Spring Security** | Базовая аутентификация (Basic Auth) |
| **Spring Boot Actuator** | Мониторинг состояния приложения |
| **H2 Database (in-memory)** | Реляционная БД для разработки |
| **HikariCP** | Пул соединений с БД |
| **Gradle (Kotlin DSL)** | Система сборки |
| **Java 21** | Платформа для запуска |

---

## 🏗️ Архитектура

Проект построен на классической трёхуровневой архитектуре **Controller → Service → Repository** с использованием DTO и маппера для разделения API-контрактов и сущностей БД.

![Архитектура](images/TaskCheckerScheme.png)

---

## 🔌 API Эндпоинты

| Метод | URL | Описание |
|-------|-----|----------|
| `GET` | `/api/tasks` | Получить все задачи |
| `GET` | `/api/tasks/themes` | Получить все уникальные темы |
| `GET` | `/api/tasks/by-id?id={id}` | Получить задачу по ID |
| `GET` | `/api/tasks/by-title?title={title}` | Поиск по названию |
| `GET` | `/api/tasks/by-theme?theme={theme}` | Поиск по теме |
| `GET` | `/api/tasks/by-author?author={author}` | Поиск по автору |
| `GET` | `/api/tasks/is-started` | Получить запущенные задачи |
| `POST` | `/api/tasks` | Создать задачу (JSON `Task`) |
| `PUT` | `/api/tasks/{id}` | Обновить задачу (JSON `Task`) |
| `DELETE` | `/api/tasks/{id}` | Удалить задачу по ID |

---

## 💻 Примеры запросов

### Создание задачи (POST /api/tasks)

```json
{
  "title": "Изучить Kotlin",
  "theme": "Обучение",
  "author": "Фёдор",
  "description": "Пройти курс и сделать проект",
  "isStarted": false
}
```
Ответ (201 Created):
  ```json
{
  "id": 1,
  "theme": {
    "id": 1,
    "themeTitle": "Обучение",
    "description": null,
    "updateAt": "2026-08-02T15:14:32.305295"
  },
  "title": "Изучить Kotlin",
  "author": "Фёдор",
  "description": "Пройти курс и сделать проект",
  "updateAt": "2026-08-02T15:14:32.362767",
  "started": false
}
```

### Получение всех задач (GET /api/tasks)

Ответ (200 OK):
```json
[
  {
    "id": 1,
    "theme": {
      "id": 1,
      "themeTitle": "Обучение",
      "description": null,
      "updateAt": "2026-08-02T15:14:32.305295"
    },
    "title": "Изучить Kotlin",
    "author": "Фёдор",
    "description": "Пройти курс и сделать проект",
    "updateAt": "2026-08-02T15:14:32.362767",
    "started": false
  }
]
```

### Обновление задачи (PUT /api/tasks/1)

```json
{
  "title": "Изучить Kotlin и Spring",
  "theme": "Обучение",
  "author": "Фёдор",
  "description": "Обновлённое описание",
  "isStarted": true
}
```

Ответ(200 OK):
```json
{
  "id": 1,
  "theme": {
    "id": 1,
    "themeTitle": "Обучение",
    "description": null,
    "updateAt": "2026-08-02T15:14:32.305295"
  },
  "title": "Изучить Kotlin и Spring",
  "author": "Фёдор",
  "description": "Обновлённое описание",
  "updateAt": "2026-08-02T15:14:32.362767",
  "started": true
}
```

### Удаление задачи (DELETE /api/tasks/1)
  
Ответ(204 No Content)

---

## 🖥️ Использование приложения
### Как готовый продукт
Для прод версии у вас должен быть postgre SQL(!)

С публичного докерхаба feodorh/taskchecker
выбриаете latest образ или любую другую версию и запускаете:

**Важно**: Для prod режима **необходимо**, чтобы PostgreSQL был запущен на хосте
и доступен по адресу localhost:5432. Если вы используете другой хост/порт, измените DB_URL в команде.\
Если порт 8080 занят, измените -p 8081:8080 или другой порт
#### Prod: 
```shell
docker run -d   \
    --name taskchecker-main \
    -p 8080:8080 \
    -e SPRING_PROFILES_ACTIVE=prod \
    -e DB_URL=jdbc:postgresql://host.docker.internal:5432/taskdb \
    -e DB_USER=postgres \
    -e DB_PASSWORD=password \
    --restart unless-stopped \
    docker.io/feodorh/taskchecker:latest
```
#### Dev:
```shell
docker run -d   \
--name taskchecker-main \
-p 8080:8080 \
-e SPRING_PROFILES_ACTIVE=dev \
-e DB_URL=jdbc:h2:mem:testdb \
--restart unless-stopped \
docker.io/feodorh/taskchecker:latest
```

### Для последующей разработки
Для локальной разработки и отладки с полным окружением (PostgreSQL + приложение) используйте Docker Compose.

**Важно:** Для CI/CD каждый разработчик использует **свой собственный Jenkins-сервер** (локально или на VPS). Данный Compose-файл не включает Jenkins, так как он не требуется для локального запуска приложения.

Выполните этапы:
#### 1. Клонируйте репозиторий

```bash
git clone https://github.com/FeodorH/TaskChecker.git
cd ./TaskChecker
```

#### 2.Запустите Docker Compose
```shell
docker-compose up -d
```

После этого:\
Приложение будет доступно по адресу http://localhost:8080.\
PostgreSQL будет работать в отдельном контейнере и автоматически инициализируется с базой taskdb.

---

## 🔐 Аутентификация
### API защищён с помощью Basic Authentication (Spring Security).

Логин: admin

Пароль: password

Для доступа к защищённым эндпоинтам необходимо добавить заголовок Authorization: Basic ... или использовать встроенную поддержку Basic Auth в Postman / IDEA HTTP Client.

## 🏥 Мониторинг (Actuator)
### Spring Boot Actuator предоставляет эндпоинты для мониторинга состояния приложения:

/actuator/health - Статус здоровья приложения (доступен без аутентификации)\
/actuator/info - Информация о приложении\
/actuator/metrics - Метрики (память, процессор, запросы)\

## 💻 H2 Console
### Для просмотра базы данных в реальном времени используй H2 Console:

```text
http://localhost:8080/h2-console
```

JDBC URL: jdbc:h2:mem:testdb\
User Name: sa\
Password: (оставить пустым)\

## 📬 Тестирование через Postman

В папке `/postman` находится экспортированная коллекция запросов.

1. Открой Postman.
2. Нажми **Import** → выбери файл `TaskChecker.postman_collection.json`.
3. Убедись, что авторизация настроена: коллекция использует **Basic Auth** с логином `admin` и паролем `password`.
4. Отправляй запросы и тестируй API.

---

## 📌 Особенности
✅ Полный CRUD с валидацией (@Valid, @NotBlank, @Size)\
✅ Защита от дубликатов (проверка на уровне сервиса)\
✅ Глобальная обработка исключений (@RestControllerAdvice)\
✅ Автоматическое обновление временной метки (@UpdateTimestamp)\
✅ Чистое разделение слоёв (Controller → Service → Repository)\
✅ DTO + Mapper для изоляции API от сущностей БД\
✅ In-memory БД для быстрой разработки\
✅ Разделение ответственности за разные таблицы на разные горизонтальные слои\
✅ Пагинация больших запросов\
✅ Настройка через application.yml\
✅ Базовая аутентификация (Spring Security)\
✅ Мониторинг через Actuator\
✅ Юнит- и интеграционные тесты\
✅ AOP-logging\
✅ Централизованная обработка исключений(AOP)\
✅ Полноценная сборка через Jenkins\
✅ Результат - docker контейнер соответствующей версии

## 🧠 Чему научился в этом проекте
#### Создание REST API на Kotlin + Spring Boot 3
#### Работа с Spring Data JPA и Hibernate
#### Проектирование архитектуры Controller-Service-Repository
#### Валидация данных и обработка ошибок
#### Использование DTO и мапперов
#### Работа с несколькими таблицами, связывание
#### Настройка H2 Database и подключение к ней
#### Работа с Gradle (Kotlin DSL)
#### Внедрение Basic Authentication (Spring Security)
#### Настройка мониторинга через Actuator
#### Написание unit тестов для всех слоёв
#### Работа с парадигмой AOP
#### Создание и развертывание пайплайнов в Jenkins
#### Работа с docker и docker compose

---

## 📄 Лицензия
### Этот проект создан в учебных целях. Свободно используйте для портфолио и обучения.

## 🔗 Контакты:
Автор: FeodorH\
GitHub: https://github.com/FeodorH \
Email: fholkin@yandex.ru\
Telegram: @feodorH\
