package com.example.demo.http;

import com.example.demo.http.bean.BaseResponseObject;

/**
 * @author xiehui6
 */
public class ResponseObject<T> extends BaseResponseObject<T> {

    private static final long serialVersionUID = -2013770706048328120L;

    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "ResponseObjcet{" +
                "code=" + code +
                '}';
    }
}
