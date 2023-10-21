package ru.relex.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.relex.service.MainService;
import ru.relex.service.ProducerService;

import static ru.relex.model.RabbitQueue.*;

@Service
@Log4j
public class ConsumerService implements ru.relex.service.ConsumerService {
    private final MainService mainService;

    public ConsumerService(MainService mainService) {
        this.mainService = mainService;
    }

    // RabbitMQ pushes the message to the current consumer.
    // Kafka instead works in pull way. Consumers ask broker fo new messages
    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE) // the queue which method will listen to
    public void consumeTextMessageUpdates(Update update) {
//        log.debug("NODE: Text message is received"); //stub
        log.debug("-------------------------------------------");
        log.debug("NODE has received TEXT message from rabbitMQ");
        log.debug("NODE. Update is being processed...");
        mainService.processTextMessage(update);
    }

    @Override
    @RabbitListener(queues = DOC_MESSAGE_UPDATE) // the queue which method will listen to
    public void consumeDocMessageUpdates(Update update) {
        log.debug("-------------------------------------------");
        log.debug("NODE has received DOC message from rabbitMQ");
        log.debug("NODE. Update is being processed...");
        mainService.processDocMessage(update);
    }

    @Override
    @RabbitListener(queues = PHOTO_MESSAGE_UPDATE) // the queue which method will listen to
    public void consumePhotoMessageUpdates(Update update) {
        log.debug("-------------------------------------------");
        log.debug("NODE has received PHOTO message from rabbitMQ");
        log.debug("NODE. Update is being processed...");
        mainService.processPhotoMessage(update);
    }
}
