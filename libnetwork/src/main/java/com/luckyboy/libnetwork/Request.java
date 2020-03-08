package com.luckyboy.libnetwork;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;

import com.luckyboy.libnetwork.cache.CacheManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class Request<T, R extends Request> implements Cloneable {

    private static final String TAG = "Request";
    protected String sUrl;
    private Type sType;
    protected HashMap<String, String> headers = new HashMap<>();
    protected HashMap<String, Object> params = new HashMap<>();

    // 仅仅只访问本地缓存,即便本地缓存不存在，也不会发起网络请求
    public static final int CACHE_ONLY = 1;
    // 先访问缓存 同时发起网络的请求，成功后缓存到本地
    public static final int CACHE_FIRST = 2;
    // 仅仅访问服务器 不存任何存储
    public static final int NET_ONLY = 3;
    // 先访问网络 成功后缓存到本地
    public static final int NET_CACHE = 4;
    // 默认情况下 只走网络请求
    private int mCacheStrategy = NET_ONLY;

    public Request(String url) {
        sUrl = url;
    }


    @IntDef({CACHE_ONLY, CACHE_FIRST, NET_ONLY, NET_CACHE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CacheStrategy {

    }


    private Call getCall() {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        addHeader(builder);
        okhttp3.Request request = generateRequest(builder);
        Call call = ApiService.okHttpClient.newCall(request);
        return call;
    }

    protected abstract okhttp3.Request generateRequest(okhttp3.Request.Builder builder);


    private void addHeader(okhttp3.Request.Builder builder) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
    }

    public R responseType(Type type) {
        sType = type;
        return (R) this;
    }

    public R responseType(Class clz) {
        sType = clz;
        return (R) this;
    }

    public R cacheStrategy(@CacheStrategy int strategy) {
        mCacheStrategy = strategy;
        return (R) this;
    }

    public R addHeader(String key, String value) {
        headers.put(key, value);
        return (R) this;
    }

    public R addParam(String key, Object value) {
        if (value == null) {
            return (R) this;
        }
        if (value.getClass() == String.class) {
            params.put(key, value);
        } else {
            try {
                Field field = value.getClass().getField("TYPE");
                Class clz = (Class) field.get(null);
                if (clz.isPrimitive()) {
                    params.put(key, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return (R) this;
    }

    // 直接在异步方法中调用
    public ApiResponse<T> execute() {
        if (sType == null) {
            throw new RuntimeException("同步方法，response 返回值 类型必须设置");
        }
        if (mCacheStrategy == CACHE_ONLY) {
            return readCache();
        }
        if (mCacheStrategy != CACHE_ONLY) {
            ApiResponse<T> result = null;
            try {
                Response response = getCall().execute();
                result = parseResponse(response, null);
            } catch (IOException e) {
                e.printStackTrace();
                if (result == null) {
                    result = new ApiResponse<>();
                    result.message = e.getMessage();
                }
            }
            return result;
        }
        return null;
    }

    private String cacheKey;

    private ApiResponse<T> readCache() {
        String key = TextUtils.isEmpty(cacheKey) ? generateCacheKey() : cacheKey;
        Object cache = CacheManager.getCache(key);
        ApiResponse<T> result = new ApiResponse<>();
        result.success = true;
        result.status = 304;
        result.message = "缓存请求成功";
        result.body = (T) cache;
        return result;
    }


    private String generateCacheKey() {
        cacheKey = UrlCreator.createUrlFromParams(sUrl, params);
        return cacheKey;
    }

    // 直接在同步方法中调用
    public void execute(final JsonCallback callback) {
        if (mCacheStrategy != NET_ONLY) {
            ArchTaskExecutor.getIOThreadExecutor().execute(() -> {
                ApiResponse<T> response = readCache();
                if (callback != null) {
                    Log.e(TAG, "execute: 缓存数据...");
                    callback.onCacheSuccess(response);
                }
            });
        }
        if (mCacheStrategy != CACHE_ONLY) {
            getCall().enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    ApiResponse<T> result = new ApiResponse<>();
                    result.message = e.getMessage();
                    callback.onError(result);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    // 这里的callback 是为了获取泛型化参数的类型 好做解析 并不是回调处理
                    ApiResponse<T> result = parseResponse(response, callback);
                    if (!result.success) {
                        callback.onError(result);
                    } else {
                        callback.onSuccess(result);
                    }
                }
            });
        }
    }

    // ApiResponse<List<User>>
    private ApiResponse<T> parseResponse(Response response, JsonCallback<T> callback) {
        String message = null;
        int status = response.code();
        boolean success = response.isSuccessful();
        ApiResponse<T> result = new ApiResponse<>();
        Convert convert = ApiService.sConvert;
        try {
            String content = response.body().string();
            if (success) {
                if (callback != null) {
                    // 参数化类型
                    ParameterizedType type = (ParameterizedType) callback.getClass().getGenericSuperclass();
                    Type argument = type.getActualTypeArguments()[0];
                    result.body = (T) convert.convert(content, argument);
                } else if (sType != null) {
                    result.body = (T) convert.convert(content, sType);
                } else {
                    Log.e(TAG, "解析失败");
                }
            } else {
                message = content;
            }
        } catch (IOException e) {
            e.printStackTrace();
            message = e.getMessage();
            success = false;
            status = 0;
        }
        result.success = success;
        result.status = status;
        result.message = message;

        if (mCacheStrategy != NET_ONLY && result.success && result.body != null && result.body instanceof Serializable) {
            saveCache(result.body);
        }
        return result;
    }

    private void saveCache(T body) {
        String key = TextUtils.isEmpty(cacheKey) ? generateCacheKey() : cacheKey;
        CacheManager.save(key, body);
    }

    @NonNull
    @Override
    protected Request clone() throws CloneNotSupportedException {
        return (Request<T, R>) super.clone();
    }


}
