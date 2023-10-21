package ru.relex.service.impl;

import com.sun.xml.bind.v2.TODO;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.relex.dao.AppUserDAO;
import ru.relex.dao.RawDataDAO;
import ru.relex.entity.AppUser;
import ru.relex.entity.RawData;
import ru.relex.entity.enums.UserState;
import ru.relex.service.MainService;

import static ru.relex.entity.enums.UserState.BASIC_STATE;
import static ru.relex.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static ru.relex.service.enums.ServiceCommands.*;

@Service
@Log4j
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;

    public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService, AppUserDAO appUserDAO) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
    }

    @Override
    public void processTextMessage(Update update) {
        log.debug("Processing text message...");
        log.debug("Starting saving message to DB...");
        saveRawData(update);
        log.debug("Message successfully saved to DB!");

        log.debug("Starting user presence in db check...");
        var appUser = findOrSaveAppUser(update);
        log.debug("User presence check done!");

        log.debug("Handling user message: " + update.getMessage());

        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";

        if (CANCEL.equals(text)){
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            //TODO добавить обработку емейла
        } else {
            log.error("Unknown error: " + userState);
            output = "Неизвестная ошибка! Введите /cancel и попробуйте снова!";
        }
        var chatId = update.getMessage().getChatId();
        log.debug("Building answer message");
        sendAnswer(output,chatId);
    }

    @Override
    public void processDocMessage(Update update) {
        log.debug("Processing Doc message...");
        log.debug("Starting saving message to DB...");
        saveRawData(update);
        log.debug("Message successfully saved to DB!");

        log.debug("Starting user presence in db check...");
        var appUser = findOrSaveAppUser(update);
        log.debug("User presence check done!");
        var chatId = update.getMessage().getChatId();
        if(isNotAllowToSendContent(chatId, appUser)){
            return;
        }
        //TODO Добавить сохранение документа
        log.debug("Building answer message");
        var answer = "Документ успешно загружен! Ссылка для скачивания: http://test.ru/get-doc/777";
        sendAnswer(answer,chatId);
    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        log.debug("Checking user state - must be activated and in basic state...");
        var userState = appUser.getState();
        if (!appUser.getIsActive()){
            log.debug("User is not activated");
            var error = "Зарегистрируйтесь или активируйте свою учётную запись для загрузки контента";
            sendAnswer(error,chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            log.debug("User is not in Basic State");
            var error = "Отмените текущую команду с помощью /cancel для отправки файлов";
            sendAnswer(error,chatId);
            return true;
        }
        log.debug("Checking user state - done!");
        return false;
    }

    @Override
    public void processPhotoMessage(Update update) {
        log.debug("Processing Photo message...");
        log.debug("Starting saving message to DB...");
        saveRawData(update);
        log.debug("Message successfully saved to DB!");

        log.debug("Starting user presence in db check...");
        var appUser = findOrSaveAppUser(update);
        log.debug("User presence check done!");
        var chatId = update.getMessage().getChatId();
        if(isNotAllowToSendContent(chatId, appUser)){
            return;
        }
        //TODO Добавить сохранение фото
        log.debug("Building answer message");
        var answer = "Фото успешно загружено! Ссылка для скачивания: http://test.ru/get-photo/777";
        sendAnswer(answer,chatId);

    }

    private String cancelProcess(AppUser appUser) {
        // basic state is set to current user
        appUser.setState(BASIC_STATE);
        // updated data is saved to db
        appUserDAO.save(appUser);
        return "Команда отменена!";
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(output);
        log.debug("Sending answer message to dispatcher via rabbitMQ: "+sendMessage.getText());
        producerService.produceAnswer(sendMessage);
        log.debug("Answer message is sent to dispatcher via rabbitMQ");
    }

    /**
     * Handles commands
     * @param appUser
     * @param cmd
     * @return
     */
    private String processServiceCommand(AppUser appUser, String cmd) {
        if (REGISTRATION.equals(cmd)){
            // TODO добавить регистрацию
            return  "Временно недоступно!";
        } else if (HELP.equals(cmd)) {
            return help();
        } else if (START.equals(cmd)) {
            return "Приветствую! Чтобы посмотреть список доступных команд введите /help";
        } else {
            return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
        }
    }

    private String help() {
        return "Список доступных команд: \n"
                + "/cancel - отмена выполнения текущей команды;\n"
                + "/registration - регистрация пользователя.";
    }

    /**
     * Tries to find User in DB. If not found then new user will be created
     * @param
     * @return
     */
    private AppUser findOrSaveAppUser(Update update){
        User telegramUser = update.getMessage().getFrom();
        AppUser persistentAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
        if (persistentAppUser == null){
            log.debug("User is not found in db. Creating new user");
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    // TODO изменить значение по умолчанию после добавления регистрации
                    .isActive(true)
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser); // save method returns object from db with primary key
            // and hibernate session
        }
        log.debug("User found in db");
        return persistentAppUser;
    }

    private void saveRawData(Update update) {
        //builder pattern implementation
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData); // saves to DB. Save method is generated in Spring automatically
    }
}
