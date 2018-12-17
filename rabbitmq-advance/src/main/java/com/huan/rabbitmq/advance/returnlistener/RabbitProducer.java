package com.huan.rabbitmq.advance.returnlistener;

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
 * <pre>
 * 	   1、ReturnListener 的使用。
 * 	   		>> mandatory: 参数需要设置成 true , ReturnListener 才会生效。
 * 	   		>> 用于获取到没有路由到消息队列中的消息。
 * 	   2、ReturnListener 的注意事项 http://www.rabbitmq.com/alarms.html
 * 	   		>> 受到内存和磁盘的限制
 * 	   		>> http://rabbitmq.1065348.n5.nabble.com/ReturnListener-is-not-invoked-td24549.html(一个RabbitMQ disk_free_limit 参数导致ReturnListener没有进入的例子)
 *
 * 	</pre>
 *
 * @author huan.fu
 * @date 2018/8/13 - 15:23
 */
public class RabbitProducer {

	private static final String EXCHANGE_NAME = "exchange_demo";
	private static final String ROUTING_KEY = "missing_routing_key";
	private static final String BINDING_KEY = "bingkey_demo";
	private static final String QUEUE_NAME = "queue_demo";
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
			// 创建一个 type="direct"持久化、非自动删除的交换器
			channel.exchangeDeclare(EXCHANGE_NAME, "direct", true, false, null);
			// 创建一个 持久化、非排他的、非自动删除的交换器
			channel.queueDeclare(QUEUE_NAME, true, false, false, null);
			// 将交换器与队列通过路由键绑定 使用 bindingKey
			channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, BINDING_KEY);

			// 发送一条持久化消息
			String message = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 没有被正确路由到消息队列的消息.mandatory参数设置成true";
			try {
				// 使用 routingKey
				channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, true, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));
				System.err.println("消息发送完成......");
			} catch (IOException e) {
				e.printStackTrace();
			}

			/**
			 * 处理生产者没有正确路由到消息队列的消息
			 * 这个可能不会生效：受到 rabbitmq 配置的内存和磁盘的限制 {@link http://www.rabbitmq.com/alarms.html}
			 */
			channel.addReturnListener((replyCode, replyText, exchange, routingKey, properties, body) -> {
				System.out.println("replyCode:" + replyCode);
				System.out.println("replyText:" + replyText);
				System.out.println("exchange:" + exchange);
				System.out.println("routingKey:" + routingKey);
				System.out.println("properties:" + properties);
				System.out.println("body:" + new String(body, StandardCharsets.UTF_8));
			});
		}
	}

}
