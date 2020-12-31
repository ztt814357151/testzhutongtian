package cn.keptdata.rabbitmq.consumer;

import cn.keptdata.rabbitmq.RabbitUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author ztt
 * @version 1.0
 * @description: mq Topic模式消费者抽象类，继承该类，实现process方法，调用startListen即可实现监听
 * @date 2020/12/8 16:53
 */
public abstract class RabbitTopicConsumer {
    protected static final Logger LOG = LoggerFactory.getLogger(RabbitTopicConsumer.class);
    protected Channel channel;
    protected String queueName;
    protected List<String> bindingKeys;
    protected String exchangeName;
    protected List<DefaultConsumer> consumers = new ArrayList<>();
    protected DefaultConsumer consumer;



    public RabbitTopicConsumer(List<String> bindingKeys, String exchangeName, String queueName) {
        this.channel = RabbitUtil.getChannel();
        this.bindingKeys = bindingKeys;
        this.exchangeName = exchangeName;
        this.queueName = queueName;
    }

    public void starListen() {
        try {
            String queueName = this.queueName;
            /**
             * 声明一个队列 如果队列已经存在则mq不会做任何事情直接返回成功
             * @param queue 队列名称
             * @param durable 是否持久化, 队列的声明默认是存放到内存中的，如果rabbitmq重启会丢失，如果想重启之后还存在就要使队列持久化，保存到Erlang自带的Mnesia数据库中，当rabbitmq重启之后会读取该数据库
             * @param exclusive 是否排外 为true时一个队列只能有一个消费者来消费 多了报错
             * @param autoDelete 是否自动删除 当最后一个消费者断开连接之后队列是否自动被删除，可以通过RabbitMQ Management，查看某个队列的消费者数量，当consumers = 0时队列就会自动删除
             * @param map
             */
            channel.queueDeclare(queueName, true, false, false, null);
            /**
             * 声明一个topic交换机
             * exchange：交换机名称
             * type：交换机类型，常见的如fanout、direct、topic
             * durable：设置是否持久化。durable设置true表示持久化，反之是持久化。持久化可以将将换机存盘，在服务器重启时不会丢失相关信息
             * autoDelete：设置是否自动删除。autoDelete设置为true则表示自动删除。默认 fasle 。自动删除的前提是至少有一个队列或者交换机与这个交换器绑定的队列或者交换器都与之解绑
             * internal：设置是否内置的。默认 fasle 。如果设置为true，则表示是内置的交换器，客户端程序无法直接发送消息到这个交换器中，只能通过交换器路由到交换器这种方式
             * argument：其他一些结构化参数，比如alternate-exchange
             */
            channel.exchangeDeclare(exchangeName, "topic", true);

            for (String bindingKey : bindingKeys) {
                /**
                 * 使用绑定键(路由键)将交换机和队列绑定起来
                 */
                channel.queueBind(queueName, exchangeName, bindingKey);
            }
            /**
             * mq同一时刻只会向服务器发送一条消息
             */
            channel.basicQos(1);
            LOG.info("消费者订阅队列[{}]已启动监听[{}]路由key[{}]", queueName, channel.hashCode(), bindingKeys);
            this.consumer = new DefaultConsumer(channel) {
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    try {
                        process(body);
                    } catch (Exception e) {
                        LOG.info("消费异常[{}]", e);
                    } finally {
                        //手动确认消息，消息确认后，mq会把消息从队列移除
                        channel.basicAck(envelope.getDeliveryTag(), false);

                    }
                }
            };
            consumers.add(consumer);
            /**
             * false:关闭自动确认
             */
            channel.basicConsume(queueName, false, consumer);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.info("消费者订阅队列[{}]异常[{}]", queueName, e);
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
                    LOG.info("消费者 | 消费者[{}]已停止", consumerTag);
                    iterator.remove();
                }
            }
            return true;
        } catch (Exception e) {
            LOG.info("单点消费者 | 消费者异常[{}]", e);
        }
        return false;
    }
}
