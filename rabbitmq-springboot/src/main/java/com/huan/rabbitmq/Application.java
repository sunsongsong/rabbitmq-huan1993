package com.huan.rabbitmq;

import com.huan.rabbitmq.producer.RabbitProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * spring boot 整合 rabbitmq
 *
 * @author huan.fu
 * @date 2018/10/22 - 13:41
 */
@SpringBootApplication
@Slf4j
public class Application implements ApplicationRunner {

	@Autowired
	private RabbitProducer rabbitProducer;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(ApplicationArguments applicationArguments) throws Exception {
		rabbitProducer.sendMessage();
	}
}
