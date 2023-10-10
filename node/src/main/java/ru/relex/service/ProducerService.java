package ru.relex.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

// for sending replies from node to broker
public interface ProducerService {
    void produceAnswer(SendMessage sendMessage);
}
