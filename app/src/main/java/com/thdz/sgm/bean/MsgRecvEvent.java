package com.thdz.sgm.bean;

import java.io.Serializable;

/**
 * desc:    收到短信 事件，展示到UI的控件上，但不展示到对话框上。
 * author:  Administrator
 * date:    2018/3/13  10:49
 */
public class MsgRecvEvent implements Serializable{
    private String msg;
    private String desc;

    public MsgRecvEvent() {

    }

    public MsgRecvEvent(String msg, String desc) {
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
