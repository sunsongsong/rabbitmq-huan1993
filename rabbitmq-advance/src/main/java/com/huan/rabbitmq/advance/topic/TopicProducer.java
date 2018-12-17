package com.huan.rabbitmq.advance.topic;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * <pre>
 * 测试 RabbitMQ 的发布订阅
 * bindingKey:
 * 		goods.* ===> * 匹配一个，比如 goods.apple 或 goods.phone
 * 		goods.# ===> # 匹配一个或多个，比如 goods.shopping.apple
 * 	routingKey:
 * 		goods.apple       ===> 都可以被上面2个bindingKey 匹配上
 * 		goods.gouwu.apple ===> 只可以被第二个匹配上
 * </pre>
 *
 * @author huan.fu
 * @date 2018/9/4 - 9:31
 */
public class TopicProducer {

	public static void main(String[] args) {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setUsername("admin");
		connectionFactory.setPassword("admin");
		connectionFactory.setHost("39.104.169.209");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");

		try (Connection connection = connectionFactory.newConnection()) {
			// 创建信道
			Channel channel = connection.createChannel();
			// 声明一个 topic 类型的交换器
			channel.exchangeDeclare("topic-exchange", BuiltinExchangeType.TOPIC, true, false, null);
			channel.queueDeclare("topic-queue-star", true, false, false, null);
			channel.queueDeclare("topic-queue-pound", true, false, false, null);

			channel.queueBind("topic-queue-star", "topic-exchange", "goods.*");
			channel.queueBind("topic-queue-pound", "topic-exchange", "goods.#");

			channel.basicPublish("topic-exchange", "goods.shopping.apple", null, "测试*号通配符,*表示匹配一个或多个".getBytes());
			channel.basicPublish("topic-exchange", "goods.apple", null, "可以同时被 # 和 * 同时匹配上".getBytes());

			System.out.println("消息发送完毕。。。");

		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}
	}
}
