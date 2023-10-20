package ru.relex.service.impl;

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
        log.debug("Starting saving message to DB");
        saveRawData(update);
        log.debug("Message successfully saved to DB!");

        log.debug("Starting user presence in db check");
        var textMessage = update.getMessage();
        var telegramUser = textMessage.getFrom();
        var appUser = findOrSaveAppUser(telegramUser);
        log.debug("User presence check done");


        log.debug("Building answer message");
        var message = update.getMessage();
        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText("Hello from NODE");
        log.debug("Sending answer message to dispatcher via rabbitMQ: "+sendMessage.getText());
        producerService.produceAnswer(sendMessage);
        log.debug("Answer message is sent to dispatcher via rabbitMQ");
    }

    /**
     * Tries to find User in DB. If not found then new user will be created
     * @param telegramUser
     * @return
     */
    private AppUser findOrSaveAppUser(User telegramUser){
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
                    .state(UserState.BASIC_STATE)
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
