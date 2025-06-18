
# 🔐 User Profile Service

Это микросервис для управления **профилями пользователей** и **подписками**.

---

## 📌 Возможности

- Создание профиля после регистрации
- Получение профиля по ID и никнейму
- Обновление и удаление профиля
- Управление подписками (подписка/отписка, список подписчиков)
- Обработка событий регистрации через RabbitMQ
- Интеграция с MySQL

---

## 📦 Технологии

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Security (для UserDetails)
- MySQL 8
- RabbitMQ
- Lombok
- Docker Compose

---

## 🧩 Архитектура

```
+-----------------------+          +-----------------------+
|  Auth Service         |  ----->  |  User Profile Service  |
|  (через RabbitMQ)     |          |  (обрабатывает события)|
+-----------------------+          +-----------------------+
```

---

## 🧪 Эндпоинты (пример)

- `GET /api/user-profiles/{id}` — получить профиль по UUID
- `GET /api/user-profiles/username/{username}` — получить профиль по имени
- `PUT /api/user-profiles/{id}` — обновить профиль
- `DELETE /api/user-profiles/{id}` — удалить профиль
- `POST /api/user-profiles/{subscriberId}/subscribe/{targetUserId}` — подписаться
- `POST /api/user-profiles/{subscriberId}/unsubscribe/{targetUserId}` — отписаться

---

## 🪝 Интеграция с RabbitMQ

| Exchange                | Routing Key          | Queue Name             | Событие                    |
|-------------------------|----------------------|------------------------|----------------------------|
| `user.profile.exchange` | `user.registered`    | `user.registered.queue`| `UserRegisteredEvent`      |

Настройка RabbitMQ:

- Exchange: `user.profile.exchange`
- Queue: `user.registered.queue`
- Binding: привязка очереди к exchange с ключом `user.registered`

---

## 📁 Важные компоненты проекта

- `RabbitConsumer` — слушает события регистрации пользователей
- `UserProfileService` — бизнес-логика по работе с профилями и подписками
- `UserProfileDetailsService` — интеграция с Spring Security (UserDetailsService)
- `UserProfileRepository` — работа с БД
- `SubscriptionRepository` — управление подписками

---

## 🐬 MySQL (Docker Compose)

```yaml
version: "3.8"

services:
  mysql:
    image: mysql:8.0
    container_name: userprofileservice-mysql
    environment:
      MYSQL_ROOT_PASSWORD: 1111
      MYSQL_DATABASE: userprofileservice
    ports:
      - "3310:3306"
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

---

## ⚙️ Пример конфигурации (`application.yml`)

```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:mysql://localhost:3310/userprofileservice
    username: root
    password: 1111
  jpa:
    hibernate:
      ddl-auto: update
  rabbitmq:
    host: localhost
    username: guest
    password: guest

queue:
  name: user.registered.queue
```

---
