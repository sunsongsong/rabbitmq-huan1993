package com.huan.rabbitmq.producer;

import com.huan.rabbitmq.entity.User;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * rabbitmq 消息生产者
 *
 * @author huan.fu
 * @date 2018/10/22 - 14:28
 */
@Component
public class RabbitProducer {

	@Autowired
	private AmqpTemplate amqpTemplate;

	/**
	 * 发送消息
	 */
	@SendTo
	public void sendMessage() {
		new Thread(() -> {
			try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// 发送简单消息
			IntStream.rangeClosed(1, 10).forEach(num -> {
				String body = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " : " + num;
				MessageProperties properties = new MessageProperties();
				properties.setContentEncoding("UTF-8");
				properties.setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT);
				Message message = new Message(body.getBytes(Charset.forName(properties.getContentEncoding())), properties);
				amqpTemplate.convertAndSend("rabbit-springboot-exchange", "rabbit-springboot-routingkey", message);
			});
			// 发送java bean 消息
			IntStream.rangeClosed(1, 10).forEach(num -> {
				User user = User.builder().userId(num).username("zhangsan:" + num).password("666666").build();
				amqpTemplate.convertAndSend("rabbit-springboot-exchange", "rabbit-springboot-routingkey.javabean", user);
			});
		}).start();
	}
}
