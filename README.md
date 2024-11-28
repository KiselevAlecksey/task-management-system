

<p style="text-align: center;">Пример микросервисов для входа в систему и управления задачами.</p>

<p align="center">
  <a href="#key-features">Ключевые особенности</a> •
  <a href="#how-to-use">Как использовать</a> •
  <a href="#credits">Благодарности</a> •
  <a href="#license">Лицензия</a>
</p>

![Static Badge](https://img.shields.io/badge/EN_Guide-blue?style=plastic)

## Ключевые особенности

* Аутентификация пользователей
    - Регистрация пользователя, обновление JWT-токена
    - Возможность добавлять пользователей с ролями ADMIN, USER, GUEST
* Менеджер задач
    - Можно создавать задачи, управлять их статусом, назначать исполнителя
* Менеджер пользователей
    - Простые операции CRUD

## Требования

- Java 21 или выше
- Maven 3.6+

## Как использовать

Чтобы клонировать и запустить это приложение, вам нужно установить [Git](https://git-scm.com/), [Docker](https://www.docker.com/), [Docker Compose](https://docs.docker.com/compose/install/) на вашем компьютере. Также у вас должна быть пара SSH ключей для клонирования по SSH(рекомендуется), следуйте инструкции [SSH](https://github.com/KiselevAlecksey/gitInfo?tab=readme-ov-file#проверка-наличия-ssh-ключа)

Выполните следующие шаги в командной строке:

```bash
# Клонируйте этот репозиторий
$ git clone git@github.com:KiselevAlecksey/task-management-system.git

# Перейдите в каталог репозитория
$ cd ваш_репозиторий

# Запустите Docker Compose для сборки и запуска приложений
$ docker-compose up --build
```

Предварительные требования

1. Убедитесь, что вы находитесь в корневом каталоге вашего проекта, где находится файл docker-compose.yml.

После запуска вы можете проверить состояние контейнеров с помощью команды:

`docker ps`

2. Убедитесь, что все контейнеры запущены и работают. Вы должны увидеть следующие контейнеры:

- tms-auth-container
- task-manager-server
- postgres-tms
- postgres-auth

Проверка здоровья сервисов:

3. Вы можете убедиться, что сервисы работают правильно, перейдя по следующим адресам в вашем веб-браузере:

- Для auth-server: http://localhost:9090/actuator/health
- Для task-manager-server: http://localhost:8080/actuator/health

4. Остановка контейнеров:

Чтобы остановить и удалить контейнеры, выполните:

`docker-compose down`

## Добавление данных

После успешного запуска сервисов вы можете использовать [Postman](https://www.postman.com/) для выполнения запросов к API.

- После запуска контейнеров вы можете подключиться к базам данных для добавления данных. Используйте любой инструмент для работы с PostgreSQL, например, [pgAdmin](https://www.pgadmin.org/) или командную строку.

- Пароли указаны в docker-compose.yml.

- Swagger UI доступен по ссылкам http://localhost:8080/swagger-ui/index.html#/ и http://localhost:9090/swagger-ui/index.html#/

- Также можно использовать [Swagger.io](https://editor-next.swagger.io/ ) для просмотра json пакете postman

Примечание

Если у вас возникли проблемы с запуском контейнеров, проверьте логи с помощью команды:

`docker-compose logs`

Это поможет вам диагностировать проблемы и найти их решения.

## Благодарности

Это программное обеспечение использует следующие открытые пакеты:

- [Spring Boot 3 JWT Security](https://github.com/ali-bouali/spring-boot-3-jwt-security)

## Лицензия

MIT

> GitHub [@KiselevAlecksey](https://github.com/KiselevAlecksey) &nbsp;&middot;&nbsp;
>Telegram [@Kiselev_Alecksey](https://t.me/Kiselev_Alecksey)

