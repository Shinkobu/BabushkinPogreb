package ru.relex.controller;

import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


/* Using LongPolling (webHooks) technology (
1) need static ip
2) ssl certificate to work with telegram
 */

@Component // spring will make bin and place it into context
@Log4j // Lombok
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${bot.name}") //spring ann - allows to get property from external file
    private String botName;
    @Value("${bot.token}")
    private String botToken;
/*    private static final Logger log = Logger.getLogger(TelegramBot.class); // the above annotation @Log4j (of Lombok
    library) replaces this declaration

 */

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        var originalMessage = update.getMessage();
//        System.out.println(originalMessage.getText()); // returns message sent to bot to console
        log.debug(originalMessage.getText());

        var response = new SendMessage();
        response.setChatId(originalMessage.getChatId().toString());
        response.setText("Hello from bot!");
        sendAnswerMessage(response);

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
