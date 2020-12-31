package cn.keptdata.rabbitmq.test;

import cn.keptdata.rabbitmq.consumer.RabbitConsumer;

import java.io.UnsupportedEncodingException;

/**
 * @author ztt
 * @version 1.0
 * @description: TODO
 * @date 2020/12/8 18:12
 */
public class TestRabbitConsumer extends RabbitConsumer {


    public TestRabbitConsumer(String queueName) {
        super(queueName);
    }

    @Override
    public void process(byte[] body) {
        try {
            String msg = new String(body, "utf-8");
            System.out.println("接受到了一条消息："+msg);
        } catch (UnsupportedEncodingException e) {
        }
    }

    public static void main(String[] args) {
        new TestRabbitConsumer(MsgType.MOBILE.get_name()).startListen();
        System.out.println("消费者启动监听队列["+MsgType.MOBILE.get_name()+"]");
        while(true){

        }
    }
}
