package com.xshadow.catspringbootdemo.utils;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 *
 * @Description:
 * @Author: cuijianlong
 * @Created: 03/04/2018  5:33 PM
 */
public class IdGeneratorUtil {

  private final static long workerIdBits = 4L;
  private final static long sequenceBits = 10L;
  private final static long twepoch = 1288834974657L;
  private final static long workerIdShift = sequenceBits;
  private final static long timestampLeftShift = sequenceBits + workerIdBits;
  private final static long sequenceMask = -1L ^ -1L << sequenceBits;
  private final static long maxWorkerId = -1L ^ -1L << workerIdBits;
  private static IdGeneratorUtil instance = null;

  private final long workerId;
  private long sequence = 0L;
  private long lastTimestamp = -1L;

  public static IdGeneratorUtil getInstance() {
    getIdGeneratorUtil(1);
    return instance;
  }

  public static IdGeneratorUtil getInstance2() {
    return getIdGeneratorUtil(2);
  }

  private static IdGeneratorUtil getIdGeneratorUtil(int workerId) {
    try {
      if (instance == null) {
        //创建实例之前可能会有一些准备性的耗时工作
        Thread.sleep(100);
        synchronized (IdGeneratorUtil.class) {
          if (instance == null) {//二次检查
            instance = new IdGeneratorUtil(workerId);
          }
        }
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return instance;
  }


  private IdGeneratorUtil(final long workerId) {
    super();
    if (workerId > maxWorkerId || workerId < 0) {
      String errorInfo = String.format("worker Id can't be greater than %d or less than 0", maxWorkerId);
      throw new IllegalArgumentException(errorInfo);
    }
    this.workerId = workerId;
  }

  private long tilNextMillis(final long lastTimestamp) {
    long timestamp = System.currentTimeMillis();
    while (timestamp <= lastTimestamp) {
      timestamp = System.currentTimeMillis();
    }
    return timestamp;
  }

  public synchronized long nextId() {
    long timestamp = System.currentTimeMillis();
    if (this.lastTimestamp == timestamp) {
      this.sequence = (this.sequence + 1) & sequenceMask;
      if (this.sequence == 0) {
        timestamp = this.tilNextMillis(this.lastTimestamp);
      }
    } else {
      this.sequence = 0;
    }
    if (timestamp < this.lastTimestamp) {
      timestamp = this.tilNextMillis(this.lastTimestamp);
    }

    this.lastTimestamp = timestamp;
    return ((timestamp - twepoch << timestampLeftShift)) | (this.workerId << workerIdShift) | (this.sequence);
  }

  /**
   * 生成uuid
   * @return
   */
  public static String getUUID(){
    UUID uuid=UUID.randomUUID();
    String str = uuid.toString();
    String uuidStr=str.replace("-", "");
    return uuidStr;
  }


  /**
   * 生成订单id
   * @return
   */
  public static String generateOrderId() {
    StringBuilder sb = new StringBuilder();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmm");
    sb.append(dateFormat.format(new Date()));
    sb.append(generateRandomStr(9));
    return sb.toString();
  }

  /**
   *
   * @param length
   * @return
   */
  public static String generateRandomStr(int length) {
    StringBuilder randomStr = new StringBuilder();
    Random random = new Random();
    for (int i = 0; i < length; i++) {
      randomStr.append(random.nextInt(10));
    }
    return randomStr.toString();
  }



//  public static void main(String[] args) {
//    System.out.println(IdGeneratorUtil.getInstance().nextId());
//    // System.out.println(IdGeneratorUtil.generateRandomStr(32));
//    //System.out.println(IdGeneratorUtil.generateFoBatchNo());
//  }



}
