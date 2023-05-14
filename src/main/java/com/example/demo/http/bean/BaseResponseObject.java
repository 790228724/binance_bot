package com.example.demo.http.bean;

import java.io.Serializable;

/**
 * @author xiehui6
 */
public abstract class BaseResponseObject<T> implements Serializable {

    private static final long serialVersionUID = -4112426223810612178L;

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseResponseObject{" +
                "data=" + data +
                '}';
    }
}
