package com.huan.rabbitmq.advance.dlx;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ 生产者
 * <p>
 * 死信队列和延时消息的使用
 * 消息先到 order_queue 中，然后 10s 钟没有消费，消息流转到死信队列 dlx.queue 中
 *
 * @author huan.fu
 * @date 2018/10/15 - 14:10
 */
public class RabbitProducer {

	private static final String IP_ADDRESS = "39.104.169.209";
	private static final int PORT = 5672;

	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost(IP_ADDRESS);
		connectionFactory.setPort(PORT);
		connectionFactory.setUsername("admin");
		connectionFactory.setPassword("admin");
		connectionFactory.setVirtualHost("/");

		try (
				// 创建一个连接
				Connection connection = connectionFactory.newConnection()
		) {
			// 创建信道
			Channel channel = connection.createChannel();
			String orderExchangeName = "order_exchange";
			String dlxExchangeName = "dlx.exchange";
			String orderQueueName = "order_queue";
			String dlxQueueName = "dlx.queue";
			String orderRoutingKey = "order.#";
			Map<String, Object> arguments = new HashMap<>(16);
			// 为队列设置队列交换器
			arguments.put("x-dead-letter-exchange", dlxExchangeName);
			// 设置队列中的消息 10s 钟后过期
			arguments.put("x-message-ttl", 10000);
			//arguments.put("x-dead-letter-routing-key", "为 dlx exchange 指定路由键，如果没有特殊指定则使用原队列的路由键");
			channel.exchangeDeclare(orderExchangeName, "topic", true, false, null);
			channel.queueDeclare(orderQueueName, true, false, false, arguments);
			channel.queueBind(orderQueueName, orderExchangeName, orderRoutingKey);
			String message = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 创建订单.";

			// 创建死信交换器和队列
			channel.exchangeDeclare(dlxExchangeName, "topic", true, false, null);
			channel.queueDeclare(dlxQueueName, true, false, false, null);
			channel.queueBind(dlxQueueName, dlxExchangeName, orderRoutingKey);

			channel.basicPublish(orderExchangeName, "order.save", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));

			System.err.println("消息发送完成......");
		}
	}
}
