package com.sltj.medical.base;

import java.util.HashMap;
import java.util.Map;

import android.view.View;

/**
 * Created by linan on2016/1/8  18:42
 * Email:fengyunzhinan@163.com
 */
public class ViewHolder {
    //把item中的每一个view存入集合中
    Map<Integer, View> mapView = new HashMap<Integer, View>();

    public void setView(int key, View v) {
        this.mapView.put(key, v);
    }

    public <T> T getView(int key) {
        return (T) this.mapView.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getView(Class<T> clazz, int key) {
        return (T) this.mapView.get(key);
    }


}

