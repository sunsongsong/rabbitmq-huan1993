# rabbitmq

#### 项目介绍
对消息队列RabbitMQ简单的使用，以及编写一些示例。

#### 代码结构
~~~ 
rabbitmq
│ |- 父项目
├─rabbitmq-helloword
│ |- rabbitmq的入门示例，一个简单的生产者和消费者示例  
│    1、消费端消息的限流，(basicQos,必须要手动签收)   
├─rabbitmq-advance
│ |- rabbitmq的高级示例
├──────── returnlistener(处理未被正确路由的消息 http://huan1993.iteye.com/blog/2429061)
│               1、mandatory：参数的使用  
│               2、用于获取到没有路由到消息队列中的消息。
│               3、如果没有触发ReturnListener的注意事项(RabbitMQ配置的内存和磁盘限制) http://www.rabbitmq.com/alarms.html
│──────── alternateexchange(处理未被正确路由的消息 http://huan1993.iteye.com/blog/2429061)
│               1、当消息从生产者交换器出发，没有合适的routingKey，那么会交给这个交换器的 备份交换器(alternate-exchange)   
│──────── defaultexchange(默认交换器)
│               1、默认的交换器是 "" ,并且是直连的交换器
│               2、新建的队列默认会自动绑定到默认的交换器上，且routingKey是队列的名称
│──────── topic(topic交换器-发布订阅)
│               1、# 匹配一个或多个
│               2、* 匹配一个  
│               3、eg:
│                     bindingKey : 
│                         goods.*      
│                         goods.#                
│                     routingKey:
│                         goods.apple           ==> 可以被上方2个都匹配上
│                         goods.shopping.apple  ==> 只可以被 goods.# 匹配上           
│──────── produceconfirm(服务端消息确认 http://huan1993.iteye.com/blog/2432156)   
│           有时候我们消息生产者，需要知道消息是否到达了RabbitMQ服务器，那么就需要进行消息确认了
│           |- confirmlistener(confirm异步确认)   
│           |- tx(事务确认)  
│──────── dlx(死信队列和延时队列的使用 )
│           |- 死信队列 和 延时消息 整合成一个延时队列的使用例子 
├─rabbitmq-spring  
│ |- spring和rabbitmq的整合示例（http://huan1993.iteye.com/blog/2432329）    
│       |- configuration rabbitmq和spring整合的配置    
│       |- test 各种测试  
│           |- DynamicSimpleMessageListenerContainerTest  
│                   |- 测试动态的添加对队列的监听和移除对队列的监听  
│                   |- 还可以动态修改其它的属性  
│           |- RabbitAdminService  
│                   |- rabbitAdmin 的简单使用，比如创建队列删除队列等等  
│           |- RabbitTemplateTest  
│                   |- 使用rabbitTemplate发送消息等  
├─rabbitmq-springboot  
│ |- springboot整合rabbitmq（http://huan1993.iteye.com/blog/2432697）  
│       |- @RabbitListener 标注的方法中的bindings会自动进行创建队列、转换器和绑定等    
│           |- 标注在类上： 为当前类中所有存在@RabbitHandler标注的方法服务  
│           |- 标注在方法上：为每个方法创建一个监听容器      
│       |- @RabbitHandler 标注的方法会使用用于处理接收到的消息  
├─rabbitmq-springboot-advanced  
│ |- springboot整合rabbitmq（http://huan1993.iteye.com/blog/2432697）  
│       |- 使用@Bean方式自动实现队列、交换器、绑定的创建    
│       |- 使用@RabbitListener实现队列消息的监听   
│       |- 实现生产者消息确认
│           |- spring.rabbitmq.template.mandatory = true 设置成true  
│           |- spring.rabbitmq.publisher-confirms = true 设置成true      
│       |- 实现死信交换器（过期的消息、basic.nack或basic.reject且requeue参数为false或队列满的消息将进入此交换器）        
│           |- 申明队列的时候设置 x-dead-letter-exchange 参数  
│       |- 实现备份交换器(alternate-exchange)，未被正确路由的消息将会经过此交换器    
│           |- 申明交换器的时候设置 alternate-exchange 参数  

~~~
