package com.seckill.dao;

import com.seckill.entity.SuccessKill;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by TYD on 17/8/29.
 */
@Repository
public interface SuccessSeckillDao {

    /**
     * 写入秒杀记录
     *
     * @return
     */
    int insertSuccessKill(SuccessKill successKill);

    /**
     * 根据秒杀id来获取秒杀成功的记录
     *
     * @param seckillId
     * @return
     */
    SuccessKill queryByIdWithSeckill(long seckillId);


}
