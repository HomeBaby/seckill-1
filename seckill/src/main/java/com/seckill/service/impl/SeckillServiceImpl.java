package com.seckill.service.impl;

import com.seckill.dao.SeckillDao;
import com.seckill.dao.SuccessSeckillDao;
import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExecution;
import com.seckill.entity.Seckill;
import com.seckill.entity.SuccessKill;
import com.seckill.enums.SeckillEnum;
import com.seckill.exception.RepeatSeckillException;
import com.seckill.exception.SeckillCloseException;
import com.seckill.exception.SeckillException;
import com.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by tyd on 2017-8-31.
 */
@Service("seckillService")
public class SeckillServiceImpl implements SeckillService {


    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessSeckillDao successSeckillDao;

    @Autowired
    private AmqpTemplate seckillTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    private Logger logger = LoggerFactory.getLogger(SeckillServiceImpl.class);

    private static final String flat = "kjljads7093*%kdsa@##$~lljl3233(#322))(3233jkja";

    public List<Seckill> getSeckillList() {

        List<Seckill> list = null;

        String key = "seckillList";

        ValueOperations<String, List> operations = redisTemplate.opsForValue();

        boolean hasKey = redisTemplate.hasKey(key);

        if (hasKey) {
            list = operations.get(key);
        } else {
            list = seckillDao.queryAll(0, 10);
            operations.set(key, list, 60, TimeUnit.SECONDS);
        }

        return list;
    }

    public Seckill getSeckillById(Long seckillId) {

        Seckill seckill = null;

        //访问Redis缓存
        String key = "seckill_" + seckillId;

        ValueOperations<String, Seckill> operations = redisTemplate.opsForValue();

        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            seckill = operations.get(key);
        } else {
            seckill = seckillDao.queryById(seckillId);
            operations.set(key, seckill, 60, TimeUnit.SECONDS);
        }
        return seckill;
    }

    /**
     * 暴露秒杀接口
     *
     * @param seckillId
     * @return
     */
    public Exposer exportSeckillUrl(Long seckillId) {

        Seckill seckill = null;

        //访问Redis缓存
        String key = "seckill_" + seckillId;

        ValueOperations<String, Seckill> operations = redisTemplate.opsForValue();

        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            seckill = operations.get(key);
        } else {
            seckill = seckillDao.queryById(seckillId);
            operations.set(key, seckill, 60, TimeUnit.SECONDS);
        }

        //如果不存在，直接返回
        if (seckill == null) {
            return new Exposer(false, seckillId);
        }

        Date start = seckill.getStartTime();
        Date end = seckill.getEndTime();

        Date now = new Date();

        //秒杀未开启或者已经结束
        if (now.getTime() < start.getTime() || now.getTime() > end.getTime()) {
            return new Exposer(false, seckillId, now.getTime(), start.getTime(), end.getTime());
        }

        String md5 = md5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    private String md5(long seckillId) {
        String base = seckillId + "/" + flat;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }

    /**
     * 事务中不要穿插其他耗时操作或者http请求等
     *
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SeckillCloseException
     * @throws RepeatSeckillException
     * @throws SeckillException
     */
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillCloseException, RepeatSeckillException, SeckillException {

        if (md5 == null || !md5.equals(md5(seckillId))) {
            throw new SeckillException("非法请求");
        }

        //查询reids缓存中的秒杀商品数量
        String key = "seckill_count_" + seckillId;

        ValueOperations<String, Integer> ops = redisTemplate.opsForValue();

        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            int count = ops.get(key);
            //查询用户是否重复秒杀
            if (redisTemplate.hasKey("successKill_" + seckillId + ":" + userPhone + seckillId)) {
                throw new RepeatSeckillException("repeat seckill");
            }

            if (count > 0) {
                return this.executeSeckilll(userPhone, seckillId, key, count, ops);
            } else {
                throw new SeckillCloseException("seckill close");
            }

        } else {
            Seckill seckill = seckillDao.queryById(seckillId);
            int count = seckill.getNumber();
            if (count > 0) {
                return this.executeSeckilll(userPhone, seckillId, key, count, ops);
            } else {
                throw new SeckillCloseException("seckill close");
            }
        }
    }

    public SeckillExecution executeSeckilll(Long userPhone, Long seckillId, String key, int count, ValueOperations ops) {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("seckillId", seckillId);
        map.put("userPhone", userPhone);
        //发送用户秒杀成功的消息到消息队列
        seckillTemplate.convertAndSend("map", map);
        count--;
        ops.set(key, count);
        ops.set("successKill_" + seckillId + ":" + userPhone + seckillId, 1);
        return new SeckillExecution(seckillId, SeckillEnum.SUCCESS, "秒杀成功");
    }
}
