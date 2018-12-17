package com.huan.rabbitmq.advance.topic;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 测试 * 号匹配
 *
 * @author huan.fu
 * @date 2018/9/4 - 10:02
 */
public class TopicStarConsumer {

	private static final String QUEUE_NAME = "topic-queue-star";
	private static final String IP_ADDRESS = "39.104.169.209";
	private static final int PORT = 5672;

	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
		Address[] addresses = new Address[]{new Address(IP_ADDRESS, PORT)};
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setUsername("admin");
		connectionFactory.setPassword("admin");

		try (
				// 注意此时获取连接的方式和生产者略有不同
				Connection connection = connectionFactory.newConnection(addresses)
		) {
			// 创建信道
			Channel channel = connection.createChannel();
			// 设置客户端最多接收未被ack的消息个数
			channel.basicQos(64);
			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
					System.err.println("topic : * 接收到消息：" + new String(body, StandardCharsets.UTF_8));
					System.err.println("deliveryTag:" + envelope.getDeliveryTag());
					channel.basicAck(envelope.getDeliveryTag(), false);
				}
			};

			channel.basicConsume(QUEUE_NAME, consumer);
			TimeUnit.SECONDS.sleep(10000000L);
		}
	}

}
