package com.huan.rabbitmq.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 测试 rabbitAdmin
 *
 * @author huan.fu
 * @date 2018/10/17 - 12:54
 */
@Component
@Slf4j
public class RabbitAdminService implements InitializingBean {

	@Autowired
	private RabbitAdmin rabbitAdmin;

	/**
	 * 创建队列
	 *
	 * @param queueName
	 */
	public void createQueue(String queueName) {
		log.info("创建队列:[{}]", queueName);
		rabbitAdmin.declareQueue(new Queue(queueName, false, false, false, null));
	}

	/**
	 * 创建direct交换器
	 *
	 * @param exchangeName
	 */
	public void createDirectExchange(String exchangeName) {
		log.info("创建direct交换器:[{}]", exchangeName);
		rabbitAdmin.declareExchange(new DirectExchange(exchangeName, false, false, null));
	}

	/**
	 * 创建topic交换器
	 *
	 * @param exchangeName
	 */
	public void createTopicExchange(String exchangeName) {
		log.info("创建topic交换器:[{}]", exchangeName);
		rabbitAdmin.declareExchange(new TopicExchange(exchangeName, false, false, null));
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		createQueue("queue001");
		createQueue("queue002");
		createDirectExchange("exchange001");
		createTopicExchange("exchange002");
		// 创建绑定
		rabbitAdmin.declareBinding(new Binding("queue001", Binding.DestinationType.QUEUE, "exchange001", "direct_001", null));
		// 创建绑定
		rabbitAdmin.declareBinding(new Binding("queue002", Binding.DestinationType.QUEUE, "exchange002", "topic.save.#", null));
	}
}
