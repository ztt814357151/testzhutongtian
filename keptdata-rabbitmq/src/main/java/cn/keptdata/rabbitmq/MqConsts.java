package cn.keptdata.rabbitmq;

/**
 * @Description: MQ常量
 * @author ztt
 * @date 2020年12月04日
 */
public class MqConsts {

    public final static String SERVER_IP = "RABBIT_SERVER_HOST";
    public final static Integer RECONNECTION_COUNT = 3;
    public final static String RABBIT_ADMIN = "guest";
    public final static String RABBIT_PASSWORD = "guest";


    //订阅模式 交换机名称
    public final static String TOPIC_EXCHANGE = "keptdata.topic";
    //订阅模式 路由key  以.分割  #匹配一个或多个单词 *匹配一个单词
    public final static String ROUTING_ORDER_JFAPP_PREFIX  = "";



    public final static String MOBILE_QUEUE = "keptdata_mobile";
    public final static String MSG_QUEUE = "keptdata_msg";

}
