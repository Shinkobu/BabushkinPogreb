package ru.relex.service;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Through MainService all incoming messages will be handled
 *
 * MainService will be a link between DAO and Producer (Consumer?) which will transmit messages from rabbitmq
 *
 */
public interface MainService {
    void processTextMessage (Update update);
}
