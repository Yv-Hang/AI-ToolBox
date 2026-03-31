package com.example.aitoolbox.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码：200成功 500失败
     */
    private int code;

    /**
     * 返回信息
     */
    private String msg;

    /**
     * 数据对象
     */
    private T data;

//    {
//        code:200,
//        msg:"",
//        data:{}
//    }

    // 成功
    public static <T> R<T> ok() {
        return ok(null);
    }

    public static <T> R<T> ok(String msg) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMsg(msg);
        r.setData(null);
        return r;
    }

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMsg("操作成功");
        r.setData(data);
        return r;
    }

    // 失败
    public static <T> R<T> fail() {
        return fail("操作失败");
    }

    public static <T> R<T> fail(String msg) {
        R<T> r = new R<>();
        r.setCode(400);
        r.setMsg(msg);
        r.setData(null);
        return r;
    }

    // 自定义状态码
    public static <T> R<T> fail(int code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(null);
        return r;
    }
}