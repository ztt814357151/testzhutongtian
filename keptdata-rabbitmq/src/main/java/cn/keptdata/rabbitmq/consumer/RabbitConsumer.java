package cn.keptdata.rabbitmq.consumer;

import cn.keptdata.rabbitmq.RabbitUtil;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author ztt
 * @version 1.0
 * @description: mq消费者抽象类，继承该类，实现process方法，调用startListen即可实现监听
 * @date 2020/12/8 16:53
 */
public abstract class RabbitConsumer {
    protected Logger LOG = LoggerFactory.getLogger(RabbitConsumer.class);
    protected Channel channel;
    protected String queueName;
    protected DefaultConsumer consumer;
    protected List<DefaultConsumer> consumers = new ArrayList<>();

    public RabbitConsumer(String queueName) {
        this.queueName = queueName;
        channel= RabbitUtil.getChannel();
    }


    public void startListen(){
        try {
            //同一时刻服务器只会发送一条消息给消费者
            channel.basicQos(1);
            channel.queueDeclare(queueName, true, false, false, null);
            LOG.info("消费者队列[{}]已启动监听[{}][{}]",queueName,channel.hashCode(),channel.getConnection().hashCode());
            this.consumer = new DefaultConsumer(channel) {
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body)throws IOException {
                    try {
                        process(body);
                    } catch (Exception e) {
                        LOG.info("消费异常[{}]",e);
                    }finally {
                        channel.basicAck(envelope.getDeliveryTag(),false);
                    }
                }
            };
            this.consumers.add(this.consumer);
            channel.basicConsume(queueName, false, this.consumer);
        } catch (Exception e) {
            LOG.info("消费者队列[{}]异常[{}]",queueName,e);
        }
    }

    public abstract void process(byte[] body);

    public synchronized boolean stop() {
        try {
            Iterator<DefaultConsumer> iterator = consumers.iterator();
            while (iterator.hasNext()) {
                DefaultConsumer consumer = iterator.next();
                if (consumer != null && consumer.getConsumerTag() != null) {
                    String consumerTag = consumer.getConsumerTag();
                    channel.basicCancel(consumerTag);
                    LOG.info("消费者 | 消费者[{}]已停止",consumerTag);
                    iterator.remove();
                }
            }
            return true;
        } catch (Exception e) {
            LOG.info("单点消费者 | 消费者异常[{}]",e);
        }
        return false;
    }
}
