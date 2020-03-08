package com.luckyboy.libcommon.global;


import android.app.Application;

import java.lang.reflect.InvocationTargetException;

/**
 * 这种获取全局的Application 是一种扩展思路
 *
 * 对于组件化项目 不可能把项目实际的Application下沉到Base, 而且各个module也不需要知道Application的名字
 *
 * 这种一次反射就能获取全局的Application对象的方式相比于在Application的onCreate保存一份的方式显得更加通用了
 *
 *
 * */
public class AppGlobals {

    private static Application sApplication;

    public static Application getInstance(){
        if (sApplication == null){
            try {
                sApplication = (Application) Class.forName("android.app.ActivityThread")
                        .getMethod("currentApplication")
                        .invoke(null, (Object[])null);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return sApplication;

    }

}
