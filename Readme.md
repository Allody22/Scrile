# Сервер для управления абстрактными покупками

## Описание проекта

Этот проект представляет собой сервер для обработки абстрактных покупок товаров от пользователей. 
Он создан с использованием Spring Boot и баз данных PostgreSQL. 
Пользователи могут регистрироваться в системе, аутентифицироваться с помощью Basic Auth, создавать заказы на товары, а также просматривать свои заказы. В проекте реализованы:

- Аутентификация пользователей с помощью Basic Auth
- Валидация входящих данных
- Тестирование функциональности контроллеров и сервисов
- Пагинация для получения заказов

## Технологии

- **Spring Boot** — основной фреймворк для разработки приложения
- **PostgreSQL** — база данных для хранения пользователей и заказов
- **Spring Security** — используется для базовой аутентификации (Basic Auth)
- **JUnit 5 и Mockito** — используются для написания тестов
- **Spring Data JPA** — для взаимодействия с базой данных
- **Lombok** — для сокращения шаблонного кода