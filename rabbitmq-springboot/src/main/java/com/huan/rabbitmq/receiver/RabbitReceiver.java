package com.huan.rabbitmq.receiver;

import com.huan.rabbitmq.entity.User;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.Charsets;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * rabbitmq 消息接收者
 *
 * @author huan.fu
 * @date 2018/10/22 - 14:36
 */
@Component
@Slf4j

public class RabbitReceiver {

	@RabbitHandler
	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = "rabbit-springboot-queue", durable = "true", exclusive = "false", autoDelete = "false"),
			exchange = @Exchange(value = "rabbit-springboot-exchange", type = ExchangeTypes.DIRECT, durable = "true", autoDelete = "false"),
			key = "rabbit-springboot-routingkey"
	))
	public void receiveMessage(Message message, Channel channel) {
		String encoding = message.getMessageProperties().getContentEncoding();
		log.info("接收到消息1:[{}]", new String(message.getBody(), Charsets.toCharset(encoding)));
		try {
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	@RabbitHandler
	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = "rabbit-springboot-queue-javabean", durable = "true", exclusive = "false", autoDelete = "false"),
			exchange = @Exchange(value = "rabbit-springboot-exchange", type = ExchangeTypes.DIRECT, durable = "true", autoDelete = "false"),
			key = "rabbit-springboot-routingkey.javabean"
	))
	public void receiveMessage(User user, Message message, Channel channel) {
		log.info("接收到消息2:[{}]", user);
		try {
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
}
