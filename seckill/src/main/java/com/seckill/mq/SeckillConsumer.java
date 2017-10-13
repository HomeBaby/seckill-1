package com.seckill.mq;

import com.seckill.dao.SeckillDao;
import com.seckill.dao.SuccessSeckillDao;
import com.seckill.entity.Seckill;
import com.seckill.entity.SuccessKill;
import com.seckill.util.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.utils.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

/**
 * Created by tyd on 2017-9-28.
 */
public class SeckillConsumer implements MessageListener {

    private static Logger logger = LoggerFactory.getLogger(SeckillConsumer.class);

    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessSeckillDao successSeckillDao;

    @Override
    public void onMessage(Message message) {


        try {
            Map<String, Long> map = ByteUtil.byteToObject(message.getBody(), Map.class);

            Long seckillId = map.get("seckillId");
            Long userPhone = map.get("userPhone");

            seckillDao.reduceNumber(seckillId);
            SuccessKill successKill = new SuccessKill(seckillId, userPhone, 1, new Date());
            successSeckillDao.insertSuccessKill(successKill);

            logger.info("后台添加秒杀信息成功:" + seckillId + ",用户：" + userPhone);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
