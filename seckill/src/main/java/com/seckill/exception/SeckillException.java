package com.seckill.exception;

/**
 * Created by TYD on 2017/8/31.
 */
public class SeckillException extends RuntimeException {

    public SeckillException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public SeckillException(String message) {
        super(message);
    }

}
