package cn.keptdata.rabbitmq.product;

public interface RabbitProduct {

    /**
     * 发送消息至队列
     * @param queueName 队列名称
     * @param msg 消息内容
     * @param count 重试次数
     * @return boolean
     */
    boolean sendMsg(String queueName,byte[] msg,int count);

    /**
     * 发送消息至交换机
     * @param msg msg
     * @param count 重试次数
     * @param exchangeName 交换机名称
     * @param bindingKey 路由key
     * @return boolean
     */
    boolean sendMsg(byte[] msg,int count,String exchangeName,String bindingKey);
}
