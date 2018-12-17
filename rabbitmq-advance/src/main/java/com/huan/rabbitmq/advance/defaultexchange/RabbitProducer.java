package com.huan.rabbitmq.advance.defaultexchange;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

/**
 * RabbitMQ 生产者
 * <p>
 * <pre>
 * 		默认交换机：是一个空字符("")的直连交换机
 * 		每一个新建的队列：
 * 			1、都会自动绑定到默认的直连交换机上
 * 			2、绑定的路由键和队列的名称相同
 * 	</pre>
 *
 * @author huan.fu
 * @date 2018/8/13 - 15:23
 */
public class RabbitProducer {

	private static final String QUEUE_NAME = "new-queue";
	private static final String IP_ADDRESS = "39.104.169.209";
	private static final int PORT = 5672;

	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost(IP_ADDRESS);
		connectionFactory.setPort(PORT);
		connectionFactory.setUsername("admin");
		connectionFactory.setPassword("admin");

		try (
				// 创建一个连接
				Connection connection = connectionFactory.newConnection()
		) {
			// 创建信道
			Channel channel = connection.createChannel();
			// 创建一个 持久化、非排他的、非自动删除的队列
			channel.queueDeclare(QUEUE_NAME, true, false, false, null);

			// 此处声明了一个队列，默认会自动绑定默认的交换机上，默认的交换机为 "" 是一个直连交换机(direct), 默认的绑定的 routingKey 为 队列的名称

			IntStream.rangeClosed(1, 20)
					.forEach(i -> {
						// 发送一条持久化消息
						String message = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 测试默认交换机.";
						try {
							// 使用 routingKey
							channel.basicPublish("", "new-queue", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));
							System.err.println("消息发送完成......");
						} catch (IOException e) {
							e.printStackTrace();
						}
					});

		}
	}

}
