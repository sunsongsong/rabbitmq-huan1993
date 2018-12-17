package com.huan.rabbitmq.advance.produceconfirm.tx;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ 生产者
 * <p>
 * 确保消息发送到了RabbitMQ服务器 - 使用事务机制（不推荐）
 *
 * @author huan.fu
 * @date 2018/10/15 - 09:40
 */
public class RabbitProducer {

	private static final String EXCHANGE_NAME = "exchange_demo";
	private static final String ROUTING_KEY = "routingkey_demo";
	private static final String QUEUE_NAME = "queue_demo";
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
			// 创建一个 type="direct"持久化、非自动删除的交换器
			channel.exchangeDeclare(EXCHANGE_NAME, "direct", true, false, null);
			// 创建一个 持久化、非排他的、非自动删除的交换器
			channel.queueDeclare(QUEUE_NAME, true, false, false, null);
			// 将交换器与队列通过路由键绑定 使用 bindingKey
			channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
			// 发送一条持久化消息
			String message = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " Hello Word.";
			try {
				// 开启事务
				channel.txSelect();
				// 使用 routingKey
				channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));
				System.out.println("消息发送完成......");
				// 提交事务
				channel.txCommit();
			} catch (IOException e) {
				e.printStackTrace();
				// 回滚事务
				channel.txRollback();
			}
		}
	}

}
