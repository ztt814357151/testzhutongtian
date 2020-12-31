//package cn.keptdata.rabbitmq.test;
//
//
//import cn.keptdata.message.api.Message;
//import cn.keptdata.message.dto.email.EmailMessageDTO;
//import cn.keptdata.rabbitmq.MqConsts;
//import cn.keptdata.rabbitmq.dto.MobileRechargeDTO;
//import cn.keptdata.rabbitmq.open_api.IMQ;
//import com.alibaba.fastjson.JSONObject;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.Arrays;
//
//
//public class RabbitProductTest {
//
//    public static void main(String[] args) throws Exception {
//
//
////        MobileRechargeDTO dto = new MobileRechargeDTO();
////        dto.setMobileNo("18516049297");
////        dto.setFaceValue("100");
////        IMQ.sendMsg(dto);
////        IMQ.sendMsg(dto);
////        627371361f4d4e04b903530277ab08db352279
////        627371361f4d4e04b903530277ab08db352279
//////        System.out.println("发送了一条消息【"+dto.toString());
////      IMQ.sendMsg(dto, MqConsts.TOPIC_EXCHANGE, "FLOW.JF.bankflow");
//
//      new Thread(new Runnable() {
//
//		@Override
//		public void run() {
//			for (int i = 0; i < 10; i++) {
//                MobileRechargeDTO dto = new MobileRechargeDTO();
//                dto.setMobileNo(String.valueOf(i));
//                dto.setFaceValue("FLOW.JF.bankflow");
//				IMQ.sendMsg(JSONObject.toJSONString(dto),MqConsts.TOPIC_EXCHANGE, "FLOW.JF.bankflow");
//			}
//		}
//	}).start();
//
////    new Thread(new Runnable() {
////
////            @Override
////            public void run() {
////                for (int i = 0; i < 10; i++) {
////                    MobileRechargeDTO dto = new MobileRechargeDTO();
////                    dto.setMobileNo(String.valueOf(i));
////                    dto.setFaceValue("ORDER.JF.b-0001");
////                    IMQ.sendMsg(dto,MqConsts.TOPIC_EXCHANGE, "ORDER.JF.b-0001");
////                }
////            }
////        }).start();
//
//    while (true){
//
//    }
////      new Thread(new Runnable() {
////    	  @Override
////    	  public void run() {
////    		  for (int i = 0; i < 500; i++) {
////    			  OpenMobileRechargeDTO mobileRechargeDTO = new OpenMobileRechargeDTO();
////    			  mobileRechargeDTO.setAmount("1");
////    			  mobileRechargeDTO.setMobile("13700000000");
////    			  mobileRechargeDTO.setOrderId("test"+new Random(100).nextInt(100)+String.valueOf(System.currentTimeMillis()).substring(8));
////    			  IMQ.sendMsg(mobileRechargeDTO);
////    		  }
////    	  }
////      }).start();
//
//
//}
//
//    public synchronized  void writeString(String msg) throws IOException {
//        File file = new File("C:\\Users\\administrator_cheng\\Desktop\\test.txt");     //文件路径（路径+文件名）
//        if (!file.exists()) {   //文件不存在则创建文件，先创建目录
//            File dir = new File(file.getParent());
//            dir.mkdirs();
//            file.createNewFile();
//        }
//        FileOutputStream outStream = new FileOutputStream(file,true);  //文件输出流用于将数据写入文件
//        outStream.write(msg.getBytes());
//        outStream.write("\r\n".getBytes());
//
//        outStream.close();  //关闭文件输出流
//
//    }
//}
