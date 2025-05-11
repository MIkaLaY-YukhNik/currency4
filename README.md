Лабораторная работа №4, учебная группа 334701
Краткое описание проекта
Проект currency4 — это Spring Boot приложение для конвертации валют с использованием API Open Exchange Rates. Сохраняет историю конвертаций в PostgreSQL (currency_rate, conversion_history). Реализованы эндпоинты /convert, /history, /to-history, /history-by-date, кэш (HashMap), обработка ошибок, логирование через аспекты, Swagger и CheckStyle. Использованы переменные окружения для безопасности.

Выполнение задания
GET-эндпоинт: Добавлен /history-by-date для выборки истории по дате через JPQL-запрос с параметром (@Query).
Кэш: Использован in-memory HashMap как бин для кэширования результатов запросов.
Обработка ошибок: Реализована обработка 400 и 500 ошибок через @ControllerAdvice и @ExceptionHandler.
Логирование: Добавлено логирование действий и ошибок с помощью аспектов Spring AOP.
Swagger и CheckStyle: Подключены Swagger для документации API и CheckStyle для проверки стиля кода.