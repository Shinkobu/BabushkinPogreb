package ru.relex.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.relex.service.UpdateProducer;

/**
 * Programming on interfaces approach
 *
 * The only one class must implement interface UpdateProducer or we get an error:
 *
 * Parameter 1 of constructor in ru.relex.controller.UpdateController required a single bean, but 2 were found:
 * 	- updateProducerImpl: defined in file [B:\IT\BabushkinPogreb\dispatcher\target\classes\ru\relex\service\impl\
 * 	UpdateProducerImpl.class]
 * 	- updateProducerImpl1: defined in file [B:\IT\BabushkinPogreb\dispatcher\target\classes\ru\relex\service\impl\
 * 	UpdateProducerImpl1.class]
 * Action:
 * Consider marking one of the beans as @Primary, updating the consumer to accept multiple beans, or using @Qualifier
 * to identify the bean that should be consumed
 */
@Service
@Log4j
public class UpdateProducerImpl implements UpdateProducer {
    private final RabbitTemplate rabbitTemplate;

    public UpdateProducerImpl(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void produce(String rabbitQueue, Update update) {
        log.debug(update.getMessage().getText()); // stub
        rabbitTemplate.convertAndSend(rabbitQueue, update);
    }
}
