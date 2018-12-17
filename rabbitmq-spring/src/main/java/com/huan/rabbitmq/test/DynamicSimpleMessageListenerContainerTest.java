package com.huan.rabbitmq.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 测试动态改变 SimpleMessageListenerContainer 的属性，比如动态增加需要监听的队列等
 *
 * @author huan.fu
 * @date 2018/10/17 - 15:12
 */
@Component
@Slf4j
public class DynamicSimpleMessageListenerContainerTest implements InitializingBean {

	@Autowired
	private ApplicationContext applicationContext;

	@Override
	public void afterPropertiesSet() throws Exception {
		new Thread(() -> {
			try {
				TimeUnit.SECONDS.sleep(10);
				SimpleMessageListenerContainer simpleMessageListenerContainer = applicationContext.getBean(SimpleMessageListenerContainer.class);
				log.info("移除对队列:[{}]的监听", "queue001");
				simpleMessageListenerContainer.removeQueueNames("queue001");
				TimeUnit.SECONDS.sleep(5);
				log.info("添加对队列:[{}]的监听", "queue001");
				String[] queueNames = simpleMessageListenerContainer.getQueueNames();
				Arrays.copyOf(queueNames, queueNames.length + 1);
				queueNames[queueNames.length - 1] = "queue001";
				simpleMessageListenerContainer.addQueueNames(queueNames);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}
}
