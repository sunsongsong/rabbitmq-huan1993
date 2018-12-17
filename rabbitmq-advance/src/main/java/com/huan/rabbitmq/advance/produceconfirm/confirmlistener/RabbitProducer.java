package com.huan.rabbitmq.advance.produceconfirm.confirmlistener;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ 生产者
 * <p>
 * 确保消息发送到了RabbitMQ服务器 - 使用Confirm Listener 机制
 *
 * @author huan.fu
 * @date 2018/10/15 - 10:30
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
				// 将channel设置成 confirm 模式
				channel.confirmSelect();
				// 异步确认消息
				channel.addConfirmListener(new ConfirmListener() {
					/**
					 * 处理 RabbitMQ 回传的 Basic.Ack
					 * @param deliveryTag
					 * @param multiple true: 表示到 deliveryTag 之前的消息都被 ack 了，即如果 deliveryTag=3 则1和2和3都是Ack的
					 * @throws IOException
					 */
					@Override
					public void handleAck(long deliveryTag, boolean multiple) throws IOException {
						System.out.println(String.format("ack: 服务器接收到了消息,deliveryTag:[%d],multiple:[%b]", deliveryTag, multiple));
					}

					/**
					 * 处理 RabbitMQ 回传的 Basic.Nack，注意nack的消息不一定就是丢失的，可能也到达了消费者
					 * @param deliveryTag
					 * @param multiple
					 * @throws IOException
					 */
					@Override
					public void handleNack(long deliveryTag, boolean multiple) throws IOException {
						System.out.println(String.format("nack: 服务器没有收到消息,deliveryTag:[%d],multiple:[%b]，此处可能需要进行重发的逻辑处理", deliveryTag, multiple));
					}
				});

				long deliveryTag = channel.getNextPublishSeqNo();
				System.out.println(String.format("当前发布消息的唯一id:[%d]", deliveryTag));

				// 使用 routingKey
				channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));

				System.out.println("消息发送完成......");
				TimeUnit.SECONDS.sleep(1);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
