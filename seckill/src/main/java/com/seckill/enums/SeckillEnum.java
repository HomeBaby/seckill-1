package com.seckill.enums;

/**
 * Created by TYD on 2017/8/31.
 */
public enum SeckillEnum {

    SUCCESS(1, "秒杀成功"),
    END(0, "秒杀结束"),
    REPEAT_SECKILL(-1, "重复秒杀"),
    INNER_ERROR(-2, "系统异常"),
    DATA_REWITE(-3, "数据篡改");

    private int state;
    private String stateInfo;

    SeckillEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public int getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public static SeckillEnum stateOf(int index) {
        for (SeckillEnum state : values()) {
            if (state.getState() == index) {
                return state;
            }
        }
        return null;
    }
}