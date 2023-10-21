Основные комментарии.
Урок 2 пройден https://www.youtube.com/watch?v=150R89VmJgc&list=PLV_4DSIw2vvI3_a6L_z5AlNaIdFNqQlW2&index=7

1) Приложение запускается через DispatcherApplication
   1) Оно запускает SpringApplication.run(DispatcherApplication.class);
      1) имеет аннотацию @SpringBootApplication 
   2) TelegramBot, UpdateController, UpdateProducerImpl имеют аннотацию @Component 

   3) TelegramBot в конструкторе инициализирует UpgradeController
   4) И выполняет метод @PostConstruct  public void init() { updateController.registerBot(this)}

Урок 3. https://www.youtube.com/watch?v=zJU0ITyKon4&list=PLV_4DSIw2vvI3_a6L_z5AlNaIdFNqQlW2&index=4
Docker

docker -v - версия докера

Скачать образ rabbitmq:
$ docker pull rabbitmq:3.11.0-management

Создать volume:
$ docker volume create rabbitmq_data

Запустить контейнер с rabbitmq:
$ docker run -d --hostname rabbitmq --name rabbitmq -p 5672:5672 -p 15672:15672 -v rabbitmq_data:/var/lib/rabbitmq --restart=unless-stopped rabbitmq:3.11.0-management
Флаги:
--detach , -d   запустит контейнер в фоновом режиме и вернет идентификатор контейнера в терминал;
--hostname   адрес контейнера для подключения к нему внутри docker из других контейнеров;
--name   имя контейнера;
-p    порты: первый порт — тот, по которому мы будет подключаться снаружи docker, а второй — тот, который используется внутри контейнера;
-v   примонтировать volume (том), т. е. внешнее хранилище данных;
--restart=unless-stopped   контейнер будет подниматься заново при каждом перезапуске системы (точнее, при запуске docker);

Так путь к volume может выглядеть в Windows:
rabbitmq_data:c:\rabbitmq_data

Подключиться к контейнеру с rabbitmq:
$ docker exec -it rabbitmq /bin/bash

Внутри контейнера создать пользователя, сделать его админом и установить права:
$ rabbitmqctl add_user userok p@ssw0rd
$ rabbitmqctl set_user_tags userok administrator
$ rabbitmqctl set_permissions -p / userok ".*" ".*" ".*"

Урок 4 https://www.youtube.com/watch?v=gwF41nvueG0&list=PLV_4DSIw2vvI3_a6L_z5AlNaIdFNqQlW2&index=6
Создаем микросервис Node. Считываем сообщения из RabbitMQ. 

Итог: создан микросервис Node.
NodeApplication и Dispatcher должный быть запущены. RabbitMQ должен работать из докера

1) Dispatcher принимает входящее сообщение телеграм
2) Dispatcher передаёт его в RabbitMQ (развёрнут в docker)
3) RabbitMQ передаёт его в Node
4) Node получила сообщение, достала оттуда id чата и сформировала ответ Hello from Node
5) Node передала ответ в RabbitMQ
6) RabbitMQ передал ответ в Dispatcher
7) Dispatcher передал его в телеграм

Урок 5 https://www.youtube.com/watch?v=rIogCnxz9kk&list=PLV_4DSIw2vvI3_a6L_z5AlNaIdFNqQlW2&index=6
Разворачиваем PostgreSQL в Docker. PgAdmin VS IntelliJ IDEA. Урок 5.


Команда для разворачивания PostgreSQL в Docker:
docker run -d --hostname pogreb --name pogreb -p 5432:5432 -e POSTGRES_USER=userok -e POSTGRES_PASSWORD=p@ssw0rd -e POSTGRES_DB=pogreb -v /data:/var/lib/postgresql/data --restart=unless-stopped postgres:14.5

Так путь к volume может выглядеть в Windows:
/data:c:\postgres_data

Флаги:
--detach , -d   запустит контейнер в фоновом режиме и вернет идентификатор контейнера;
--hostname   адрес контейнера для подключения к нему внутри docker из других контейнеров;
--name   имя контейнера;
-p    порты: первый порт — тот, который мы увидим снаружи docker, а второй — тот, который внутри контейнера;
-e  задает переменную окружения в контейнере;
POSTGRES_DB=pogreb - задать дефолтную базу данных
-v   примонтировать volume (том); /data - путь, где хранятся данные внутри контейнера,
/var/lib/postgresql/data - путь в нашей файловой системе
--restart=unless-stopped   контейнер будет подниматься заново при каждом перезапуске системы (точнее, при запуске docker);


Урок 6. Подключаем микросервис к PostgreSQL с помощью Spring Data JPA.
https://www.youtube.com/watch?v=AeekNYkAYug&list=PLV_4DSIw2vvI3_a6L_z5AlNaIdFNqQlW2&index=7

Что получается:
Dispatcher
Docker
RabbitMQ
Node
Postgres

Итог: подключена база данных.
NodeApplication и Dispatcher должный быть запущены. RabbitMQ и Postgres должны работать из докера

1) Dispatcher принимает входящее сообщение телеграм
2) Dispatcher передаёт его в RabbitMQ (развёрнут в docker)
3) RabbitMQ передаёт его в Node
4) Node через RawData преобразует Update в jsonb
5) В Node. 
   1) MainService - связующее звено. 
   2) ConsumerService получает сообщение из rabbiMQ, передаёт его в MainService 
   3) MainService обрабатывает сообщение, 
   4) вызывает saveRawData
   5) RawDataDAO сохраняет данные в БД - *без участия RabbitMQ
6) Node получила сообщение, достала оттуда id чата и сформировала ответ Hello from Node
7) Node передала ответ в RabbitMQ
8) RabbitMQ передал ответ в Dispatcher
9) Dispatcher передал его в телеграм

Урок 8. Сохраняем пользователей в Postgres БД. 
https://www.youtube.com/watch?v=87OG-yaJUZU&list=PLV_4DSIw2vvI3_a6L_z5AlNaIdFNqQlW2&index=9

Модуль common-jpa нужен для совместного использования кода в Node и нового микросервиса для скачивания файлов 
и подтверждения регистрации с почты.

1) Dispatcher принимает входящее сообщение телеграм
2) Dispatcher передаёт его в RabbitMQ (развёрнут в docker)
3) RabbitMQ передаёт его в Node
4) Node через RawData преобразует Update в jsonb
5) В Node.
   1) MainService - связующее звено.
   2) ConsumerService получает сообщение из rabbiMQ, передаёт его в MainService
   3) MainService обрабатывает сообщение, 
   4) Проверяет на наличие пользователя в базе данных
   5) вызывает saveRawData
   6) RawDataDAO сохраняет данные в БД - *без участия RabbitMQ
6) Node получила сообщение, достала оттуда id чата и сформировала ответ Hello from Node
7) Node передала ответ в RabbitMQ
8) RabbitMQ передал ответ в Dispatcher
9) Dispatcher передал его в телеграм


Урок 9. Добавляем обработку текстовых команд из чата telegram-бота.
https://www.youtube.com/watch?v=Yo7knU4rz-g&list=PLV_4DSIw2vvI3_a6L_z5AlNaIdFNqQlW2&index=10

Итог - добавлены заглушки для обработки базовых команд

1) Dispatcher принимает входящее сообщение телеграм
2) Dispatcher передаёт его в RabbitMQ (развёрнут в docker)
3) RabbitMQ передаёт его в Node
4) Node через RawData преобразует Update в jsonb
5) В Node.
   1) MainService - связующее звено.
   2) ConsumerService получает сообщение из rabbiMQ, передаёт его в MainService
   3) MainService обрабатывает сообщение,
   4) Проверяет на наличие пользователя в базе данных
   5) Вызывает saveRawData 
   6) RawDataDAO сохраняет данные в БД
   7) Для фото и doc файлов идёт проверка состояния юзера
6) Node получила сообщение, достала оттуда id чата и сформировала ответ Hello from Node
7) Node передала ответ в RabbitMQ
8) RabbitMQ передал ответ в Dispatcher
9) Dispatcher передал его в телеграм