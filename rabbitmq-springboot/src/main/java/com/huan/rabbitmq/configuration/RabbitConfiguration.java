package com.huan.rabbitmq.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbitmq 配置类
 *
 * @author huan.fu
 * @date 2018/10/24 - 19:42
 */
@Configuration
@Slf4j
public class RabbitConfiguration {

	@Bean
	public MessageConverter messageConverter() {
		return new SimpleMessageConverter() {
			@Override
			protected Message createMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
				Message message = super.createMessage(object, messageProperties);
				log.info("使用自定义的MessageConvert转换消息");
				return message;
			}
		};
	}
}
