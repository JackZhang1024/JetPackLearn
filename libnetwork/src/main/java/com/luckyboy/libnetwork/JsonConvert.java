package com.luckyboy.libnetwork;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Type;

public class JsonConvert implements Convert {

    @Override
    public Object convert(String response, Type type) {
        JSONObject jsonObject = JSON.parseObject(response);
        JSONObject data = jsonObject.getJSONObject("data");
        if (data != null) {
            Object dataInner = data.get("data");
            if (dataInner != null) {
                return JSONObject.parseObject(dataInner.toString(), type);
            }
        }
        return null;
    }


}
