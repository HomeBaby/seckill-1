package com.seckill.dto;

import com.seckill.entity.SuccessKill;
import com.seckill.enums.SeckillEnum;

/**
 * Created by TYD on 2017/8/31.
 * 封装秒杀执行后的结果
 */
public class SeckillExecution {

    private long seckillId;

    private int state;

    private String stateInfo;

    private SuccessKill successKill;

    public SeckillExecution(long seckillId, SeckillEnum seckillEnum, String stateInfo) {
        this.seckillId = seckillId;
        this.state = seckillEnum.getState();
        this.stateInfo = stateInfo;
    }

    public SeckillExecution(long seckillId, SeckillEnum seckillEnum) {
        this.seckillId = seckillId;
        this.state = seckillEnum.getState();
        this.stateInfo = seckillEnum.getStateInfo();
    }

    public SeckillExecution(long seckillId, SeckillEnum seckillEnum, SuccessKill successKill) {
        this.seckillId = seckillId;
        this.state = seckillEnum.getState();
        this.successKill = successKill;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SuccessKill getSuccessKill() {
        return successKill;
    }

    public void setSuccessKill(SuccessKill successKill) {
        this.successKill = successKill;
    }
}
