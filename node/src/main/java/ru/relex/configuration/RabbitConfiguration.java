package ru.relex.configuration;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static ru.relex.model.RabbitQueue.*;


/**
 * class for rabbitmq configuration
 */

@Configuration
public class RabbitConfiguration {
    /**
     * returns jsonConverter which will convert updates to json and send them to rabbitmq, and convert json from
     * rabbitmq convert to java objects
     *
     * @return
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
