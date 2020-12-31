package cn.keptdata.rabbitmq.product;

import cn.keptdata.rabbitmq.RabbitUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author ztt
 * @version 1.0
 * @description: mq生产者消息发送类
 * @date 2020/12/4 16:01
 */
public class RabbitProductImpl implements RabbitProduct {

    protected static Logger LOG = LoggerFactory.getLogger(RabbitProductImpl.class);

    private volatile static Object lock = new Object();
    private static volatile RabbitProduct product = null;

    /**
     * 私有化构造函数
     */
    private RabbitProductImpl() {
    }


    /**
     * 提供单例的生产者实例获取方法
     *
     * @return
     */
    public static RabbitProduct getProduct() {
        if (product == null) {
            synchronized (lock) {
                if (product == null) {
                    product = new RabbitProductImpl();
                    LOG.info("初始化生产者[{}]", product.hashCode());
                }
            }
        }
        return product;
    }

    @Override
    public boolean sendMsg(String queueName, byte[] msg, int count) {
        LOG.info("开始向队列[{}]推送消息[{}]", queueName, msg);
        Channel channel = RabbitUtil.getChannel();
        try {

            /**
             * 开启生产者confirm机制
             */
            channel.confirmSelect();
            /**
             * 声明一个队列
             * @param queue 队列名称
             * @param durable 是否持久化, 队列的声明默认是存放到内存中的，如果rabbitmq重启会丢失，如果想重启之后还存在就要使队列持久化，保存到Erlang自带的Mnesia数据库中，当rabbitmq重启之后会读取该数据库
             * @param exclusive 是否排外 为true时一个队列只能有一个消费者来消费 多了报错
             * @param autoDelete 是否自动删除 当最后一个消费者断开连接之后队列是否自动被删除，可以通过RabbitMQ Management，查看某个队列的消费者数量，当consumers = 0时队列就会自动删除
             * @param map
             */
            channel.queueDeclare(queueName, true, false, false, null);
            /**
             * 向队列发送消息
             * var1 : 队列名称，如果不传，则通过默认的/*队列发送到队列
             * var2: 队列名称
             * var3: 选择发送消息的传输防水，是否持久化等
             * var4:消息内容
             */
            channel.basicPublish("", queueName, MessageProperties.PERSISTENT_BASIC, msg);
            /**
             * 消息确认 直到mq返回消息持久化成功
             * 是一个阻塞方法
             */
            channel.waitForConfirmsOrDie();
            return true;
        } catch (Exception e) {
            try {
                //等待重连机制触发，三次后还未重连成功则返回失败
                LOG.warn("thread name [{}]:connection failed,try to reconnection--->  count[{}]，exception message[{}]", Thread.currentThread().getName(), count, e.getMessage());
                if (count > 0) {
                    count--;
                    Thread.sleep(100);
                    return sendMsg(queueName, msg, count);
                }
                LOG.info("向队列[{}]推送消息[{}]异常",  queueName, msg, e);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }finally {
            RabbitUtil.closeChannel(channel);
        }

        return false;
    }

    @Override
    public boolean sendMsg(byte[] msg, int count, String exchangeName, String bindingKey) {
        LOG.info("开始向路由器[{}]绑定key[{}]推送订阅消息[{}]", exchangeName, bindingKey, msg);
        Channel channel = RabbitUtil.getChannel();
        try {
            /**
             * 开启生产者confirm机制
             */
            channel.confirmSelect();
            /**
             * 声明一个交换机
             * exchange：交换机名称
             * type：交换机类型，常见的如fanout、direct、topic
             * durable：设置是否持久化。durable设置true表示持久化，反之是持久化。持久化可以将将换机存盘，在服务器重启时不会丢失相关信息
             * autoDelete：设置是否自动删除。autoDelete设置为true则表示自动删除。默认 fasle 。自动删除的前提是至少有一个队列或者交换机与这个交换器绑定的队列或者交换器都与之解绑
             * internal：设置是否内置的。默认 fasle 。如果设置为true，则表示是内置的交换器，客户端程序无法直接发送消息到这个交换器中，只能通过交换器路由到交换器这种方式
             * argument：其他一些结构化参数，比如alternate-exchange
             */
            channel.exchangeDeclare(exchangeName, "topic", true);
            channel.basicPublish(exchangeName, bindingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, msg);
            channel.waitForConfirmsOrDie();
            return true;
        } catch (Exception e) {
            try {
                //等待重连机制触发，三次后还未重连成功则返回失败
                LOG.warn("thread name [{}]:connection failed,try to reconnection--->  count[{}]，exception message[{}]", Thread.currentThread().getName(), count, e.getMessage());
                if (count > 0) {
                    count--;
                    Thread.sleep(1000);
                    return sendMsg(msg, count, exchangeName, bindingKey);
                }
                e.printStackTrace();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }finally {
           RabbitUtil.closeChannel(channel);
        }
        return false;
    }
}
