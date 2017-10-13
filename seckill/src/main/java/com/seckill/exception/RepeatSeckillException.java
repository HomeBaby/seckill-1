package com.seckill.exception;

/**
 * Created by TYD on 2017/8/31.
 * 重复秒杀异常
 */
public class RepeatSeckillException extends SeckillException {

    public RepeatSeckillException(String message) {
        super(message);
    }

    public RepeatSeckillException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
