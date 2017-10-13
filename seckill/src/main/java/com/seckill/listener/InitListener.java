package com.seckill.listener;

import com.seckill.entity.Seckill;
import com.seckill.service.SeckillService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;

/**
 * Created by tyd on 2017-9-28.
 */

//必须添加该注解，否则无法扫描和注入applicationContext
@Component
public class InitListener implements ServletContextListener, ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        //初始化秒杀商品的数量

        RedisTemplate redisTemplate = (RedisTemplate) applicationContext.getBean("redisTemplate");

        SeckillService seckillService = (SeckillService) applicationContext.getBean("seckillService");

        List<Seckill> list = seckillService.getSeckillList();

        list.stream().forEach((seckill) -> {
            String key = "seckill_count_" + seckill.getSeckillId();
            ValueOperations<String, Integer> ops = redisTemplate.opsForValue();
            ops.set(key, seckill.getNumber());
        });

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }


}
