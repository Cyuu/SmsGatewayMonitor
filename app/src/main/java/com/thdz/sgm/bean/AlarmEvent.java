package com.thdz.sgm.bean;

import java.io.Serializable;

/**
 * desc:    告警事件： 1 保存和展示日志， 2 主页面弹出通知栏和textview，提示告警
 * author:  Administrator
 * date:    2018/3/13  10:49
 */
public class AlarmEvent implements Serializable{
    private String msg;
    private String desc;

    public AlarmEvent() {

    }

    public AlarmEvent(String msg, String desc) {
        this.msg = msg;
        this.desc = desc;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
