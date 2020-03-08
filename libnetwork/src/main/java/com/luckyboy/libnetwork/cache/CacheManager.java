package com.luckyboy.libnetwork.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class CacheManager {


    private static Object toObject(byte[] data) {
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(data);
            ois = new ObjectInputStream(bis);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (ois != null) {
                    ois.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private static <T> byte[] toByteArray(T body) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(body);
            oos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (oos != null) {
                    oos.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }

    public static <T> void save(String key, T value) {
        Cache cache = new Cache();
        cache.key = key;
        cache.data = toByteArray(value);
        CacheDatabase.getCacheDb().getCacheDao().save(cache);
    }


    public static <T> void delete(String key, T value) {
        Cache cache = new Cache();
        cache.key = key;
        cache.data = toByteArray(value);
        CacheDatabase.getCacheDb().getCacheDao().delete(cache);
    }


    public static Object getCache(String key) {
        Cache cache = CacheDatabase.getCacheDb().getCacheDao().getCache(key);
        if (cache!=null && cache.data!=null){
            return toObject(cache.data);
        }
        return null;
    }


}
