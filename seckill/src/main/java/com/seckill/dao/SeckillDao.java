package com.seckill.dao;

import com.seckill.entity.Seckill;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by TYD on 17/8/29.
 */
@Repository
public interface SeckillDao {

    /**
     * 减库存
     * @return
     */
    int reduceNumber(Long seckillId);

    /**
     * 根据id查询
     * @param seckillId
     * @return
     */
    Seckill queryById(long seckillId);

    /**
     * 获取所有秒杀列表
     * @param offset
     * @param limit
     * @return
     */
    List<Seckill> queryAll(int offset, int limit);
}
