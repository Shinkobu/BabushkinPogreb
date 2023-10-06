package ru.relex.controller;

import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.relex.service.UpdateProducer;

import javax.annotation.PostConstruct;


/* Using LongPolling (webHooks) technology (
1) need static ip
2) ssl certificate to work with telegram
 */

@Component /* spring will make bean and place it into context / annotation on a class makes a singleton bean of
 that said class */
@Log4j // Lombok adds static final field log4j. No need to make instance of logger manually.
public class TelegramBot extends TelegramLongPollingBot {

    /*    private static final Logger log = Logger.getLogger(TelegramBot.class); // the above annotation @Log4j (of Lombok
    library) replaces this declaration
 */
    @Value("${bot.name}") //spring ann - allows to get property from external file
    private String botName;
    @Value("${bot.token}")
    private String botToken;


    private UpdateController updateController;

    /**
     * https://youtu.be/150R89VmJgc?si=19FLFJ6H9T0TkuFc&t=184
     * В тгБот внедряется ссылка на updateController и после внедрения зависимости выполняется init метод. В нём мы
     * передаём ссылку на сам тгБот в updateController. Т.О. тгБот сможет передать входящее сообщение в контроллер,
     * а контроллер сможет передать ответы обратно в тгБот
     * @param updateController
     */
    public TelegramBot(UpdateController updateController) {
        this.updateController = updateController;
    }

    @PostConstruct
    public void init() {
        updateController.registerBot(this);
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    /**
     * Handles with incoming message or any other update
     * @param update
     */
    @Override
    public void onUpdateReceived(Update update) {

        //version 1
//        var originalMessage = update.getMessage();
//        System.out.println(originalMessage.getText()); // returns message sent to bot to console
        //version 2 - generate answer Hello from bot
//        var originalMessage = update.getMessage();
//        log.debug(originalMessage.getText());
//        var response = new SendMessage();
//        response.setChatId(originalMessage.getChatId().toString());
//        response.setText("Hello from bot!");
//        sendAnswerMessage(response);
        //version 3 - integration with UpdateController
        updateController.processUpdate(update);

    }

    /*
    View part of MVC

     */
    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }
}
