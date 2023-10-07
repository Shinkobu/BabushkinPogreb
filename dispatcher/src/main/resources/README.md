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