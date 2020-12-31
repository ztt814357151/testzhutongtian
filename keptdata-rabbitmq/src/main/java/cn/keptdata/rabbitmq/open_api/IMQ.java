package cn.keptdata.rabbitmq.open_api;

import cn.keptdata.rabbitmq.MqConsts;
import cn.keptdata.rabbitmq.product.RabbitProduct;
import cn.keptdata.rabbitmq.product.RabbitProductImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ztt
 * @version 1.0
 * @description: MQ对外接口api类
 * @date 2020/12/8 16:53
 */
public class IMQ {
    protected static Logger LOG = LoggerFactory.getLogger(IMQ.class);


    /**
     * @param msg 消息对象
     * @param queueName 队列名称
     * @return
     */
    public static boolean sendMsg(byte[] msg,String queueName) {
        try {
            RabbitProduct product = RabbitProductImpl.getProduct();
            boolean result = product.sendMsg(queueName, msg, MqConsts.RECONNECTION_COUNT);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param msg 消息对象
     * @param exchangeName 交换机名称
     * @param bindingKey 交换机和队列绑定的路由key
     * @return
     */
    public static boolean sendMsg(byte[] msg, String exchangeName, String bindingKey) {
        try {
            RabbitProduct product = RabbitProductImpl.getProduct();
            boolean result = product.sendMsg(msg, MqConsts.RECONNECTION_COUNT, exchangeName, bindingKey);
            return result;
        } catch (Exception e) {
            return false;
        }
    }



}
