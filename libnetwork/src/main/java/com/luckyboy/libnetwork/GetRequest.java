package com.luckyboy.libnetwork;

public class GetRequest<T> extends Request<T, GetRequest> {


    public GetRequest(String url) {
        super(url);
    }

    @Override
    protected okhttp3.Request generateRequest(okhttp3.Request.Builder builder) {
        // 需要拼接参数
        okhttp3.Request request = builder.get().url(UrlCreator.createUrlFromParams(sUrl, null)).build();
        return request;
    }





}
