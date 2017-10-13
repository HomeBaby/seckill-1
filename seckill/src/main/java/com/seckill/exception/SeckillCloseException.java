package com.seckill.exception;

import com.seckill.dto.SeckillExecution;
import com.seckill.entity.Seckill;

/**
 * Created by TYD on 2017/8/31.
 */
public class SeckillCloseException extends SeckillException {

    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
