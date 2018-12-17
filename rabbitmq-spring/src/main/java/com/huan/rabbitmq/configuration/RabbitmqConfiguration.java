package com.huan.rabbitmq.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * rabbitmq 配置
 *
 * @author huan.fu
 * @date 2018/10/17 - 10:57
 */
@Configuration
@Slf4j
public class RabbitmqConfiguration {

	/**
	 * 创建 rabbitmq 连接工厂
	 *
	 * @return
	 */
	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);
		connectionFactory.setHost("39.104.169.209");
		connectionFactory.setPort(5672);
		connectionFactory.setUsername("admin");
		connectionFactory.setPassword("admin");
		connectionFactory.setVirtualHost("/");
		return connectionFactory;
	}

	/**
	 * rabbitmq 实现 AMQP 便携式的管理操作，比如创建队列、绑定、交换器等
	 *
	 * @param connectionFactory
	 * @return
	 */
	@Bean
	public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
		RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
		rabbitAdmin.setAutoStartup(true);
		return rabbitAdmin;
	}

	/**
	 * rabbit mq 模板
	 *
	 * @param connectionFactory
	 * @return
	 */
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		return new RabbitTemplate(connectionFactory);
	}

	/**
	 * 消息监听容器
	 *
	 * @param connectionFactory
	 * @return
	 */
	@Bean
	public SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
		// 设置监听的队列
		simpleMessageListenerContainer.setQueueNames("queue001", "queue002");
		// 指定要创建的并发使用者的数量,默认值是1,当并发高时可以增加这个的数值，同时下方max的数值也要增加
		simpleMessageListenerContainer.setConcurrentConsumers(3);
		// 最大的并发消费者
		simpleMessageListenerContainer.setMaxConcurrentConsumers(10);
		// 设置是否重回队列
		simpleMessageListenerContainer.setDefaultRequeueRejected(false);
		// 设置签收模式
		simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		// 设置非独占模式
		simpleMessageListenerContainer.setExclusive(false);
		// 设置consumer未被 ack 的消息个数
		simpleMessageListenerContainer.setPrefetchCount(1);
		// 接收到消息的后置处理
		simpleMessageListenerContainer.setAfterReceivePostProcessors((MessagePostProcessor) message -> {
			message.getMessageProperties().getHeaders().put("接收到消息后", "在消息消费之前的一个后置处理");
			return message;
		});
		// 设置 consumer 的 tag
		simpleMessageListenerContainer.setConsumerTagStrategy(new ConsumerTagStrategy() {
			private AtomicInteger consumer = new AtomicInteger(1);

			@Override
			public String createConsumerTag(String queue) {
				return String.format("consumer:%s:%d", queue, consumer.getAndIncrement());
			}
		});
		// 设置消息监听器
		simpleMessageListenerContainer.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
			try {
				log.info("============> Thread:[{}] 接收到消息:[{}] ", Thread.currentThread().getName(), new String(message.getBody()));
				channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				// 发生异常此处需要捕获到
				channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
			}
		});

		/**  ================ 消息转换器的用法 ================
		 simpleMessageListenerContainer.setMessageConverter(new MessageConverter() {
		 // 将 java 对象转换成 Message 对象
		 @Override public Message toMessage(Object object, MessageProperties messageProperties) {
		 return null;
		 }

		 // 将 message 对象转换成 java 对象
		 @Override public Object fromMessage(Message message) {
		 return null;
		 }
		 });
		 */

		/**  ================ 消息适配器的用法，用于处理各种不同的消息 ================
		 MessageListenerAdapter adapter = new MessageListenerAdapter();
		 // 设置真正处理消息的对象，可以是一个普通的java对象，也可以是 ChannelAwareMessageListener 等
		 adapter.setDelegate(null);
		 adapter.setDefaultListenerMethod("设置上一步中delegate对象中处理的方法名");

		 ContentTypeDelegatingMessageConverter converters = new ContentTypeDelegatingMessageConverter();
		 // 文本装换器
		 MessageConverter txtMessageConvert = null;
		 // json 转换器
		 MessageConverter jsonMessageConvert = null;

		 converters.addDelegate("text", txtMessageConvert);
		 converters.addDelegate("html/text", txtMessageConvert);
		 converters.addDelegate("text/plain", txtMessageConvert);

		 converters.addDelegate("json", jsonMessageConvert);
		 converters.addDelegate("json/*", jsonMessageConvert);
		 converters.addDelegate("application/json", jsonMessageConvert);

		 adapter.setMessageConverter(converters);
		 simpleMessageListenerContainer.setMessageListener(adapter);

		 */
		return simpleMessageListenerContainer;
	}


	@Bean
	public Queue queue003() {
		return new Queue("queue003", false, false, false, null);
	}

	@Bean
	public Exchange exchange003() {
		return new TopicExchange("exchange003", false, false, null);
	}

	@Bean
	public Binding binding003() {
		return new Binding("queue003", Binding.DestinationType.QUEUE, "exchange003", "save.*", null);
	}

}
