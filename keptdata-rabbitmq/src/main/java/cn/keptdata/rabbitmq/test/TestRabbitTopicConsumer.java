package cn.keptdata.rabbitmq.test;

import cn.keptdata.rabbitmq.MqConsts;
import cn.keptdata.rabbitmq.consumer.RabbitTopicConsumer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ztt
 * @version 1.0
 * @description: TODO
 * @date 2020/12/8 18:12
 */
public class TestRabbitTopicConsumer extends RabbitTopicConsumer {




    public TestRabbitTopicConsumer(List<String> bindingKeys, String exchangeName, String queueName) {
        super(bindingKeys, exchangeName, queueName);
    }

    @Override
    public void process(byte[] body) {
        try {
            String msg = new String(body, "utf-8");
            System.out.println("接受到了一条消息：" + msg);
        } catch (UnsupportedEncodingException e) {
        }
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
//        list.add("FLOW.JF.bankflow");
//        list.add("PROCEEDS.JF.alloca");
//        list.add("ORDER.JF.b-0001");
        new TestRabbitTopicConsumer(list, MqConsts.TOPIC_EXCHANGE, MsgType.MOBILE.get_name()).starListen();
        System.out.println("消费者已启动监听");
    }
}
