package cn.keptdata.rabbitmq;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author ztt
 * @Description: mq工具类
 * @date 2020年12月04日
 */
public class RabbitUtil {
    private static Logger LOG = LoggerFactory.getLogger(RabbitUtil.class);
    private volatile static Object lock = new Object();

    private volatile static Connection connection = null;

	private RabbitUtil() {
	}

	/**
     * 获取一个channel
     * 每个线程都应该有一个channel
     */
    public static Channel getChannel()  {
		try {
			return getConnection().createChannel();
		} catch (IOException e) {
			return null;
		}
	}

    /**
     * 关闭信道
     */
    public static void closeChannel(Channel channel)  {
		try {
			if(channel!=null){
				channel.close();
			}
		} catch (IOException | TimeoutException e) {
			LOG.error("信道关闭异常【{}】",e);
		}
	}




	/**
	 * 初始化rabbitmq连接
	 * 维持一个单例的连接
	 * @return
	 */
	private static void initConnection() {
		try {
			if (connection == null) {
				synchronized (lock) {
					if (connection == null) {
						ConnectionFactory factory = new ConnectionFactory();
						factory.setUsername(MqConsts.RABBIT_ADMIN);
						factory.setPassword(MqConsts.RABBIT_PASSWORD);
						// 自动重连机制
						factory.setAutomaticRecoveryEnabled(true);
						factory.setNetworkRecoveryInterval(1000);
						String host = System.getenv(MqConsts.SERVER_IP);
	//						String host = "127.0.0.1:5672";
						LOG.info("消息队列初始化地址[{}]", host);
						Address[] addresses = Address.parseAddresses(host);
						connection = factory.newConnection(addresses);
					}
				}
			}
		} catch (IOException e) {
			LOG.info("初始化rabbitmq连接[{}]", e);
		} catch (TimeoutException e) {
			LOG.info("初始化rabbitmq连接[{}]", e);
		}

	}

    /**
     * 返回一个rabbitmq连接
     * @return Channel
     */
	private static Connection getConnection(){
    	if (connection == null) {
			initConnection();
		}
        return connection;
    }

    /**
     * 返回一个指定队列的生产者
     *
     * @param
     * @return
     */
//    public static RabbitProduct getProductInstance(String queueName){
//    	if (MqContsts.CREDIT_QUEUE.equals(queueName)||MqContsts.CREDIT_RES_QUEUE.equals(queueName)) {
//			 credit_product.setQueue(queueName);
//			 return credit_product;
//		}else {
//			if (product == null){
//				synchronized (lock){
//					if (product == null){
//						product = new RabbitProductImpl(queueName);
//					}
//				}
//			}
//			product.setQueue(queueName);
//			return product;
//		}
//    }

   /* public static RabbitConsumer getConsumerByQueue(String queueName){
        return new RabbitConsumerImpl(queueName);
    }*/
    public static void main(String[] args) {
        Address[] addresses = Address.parseAddresses("127.0.0.1:5672,10.0.10.21:5672,10.0.10.22:5672");
        System.out.println(addresses.toString());
        System.out.println();
    }
}
