package com.luckyboy.libnavcompiler;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.auto.service.AutoService;
import com.luckyboy.libnavannotation.ActivityDestination;
import com.luckyboy.libnavannotation.FragmentDestination;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;


@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"com.luckyboy.libnavannotation.ActivityDestination", "com.luckyboy.libnavannotation.FragmentDestination"})
public class NavProcessor extends AbstractProcessor {

    private Messager messager;
    private Filer filer;
    private static final String OUTPUT_FILE_NAME = "destination.json";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        // 日志打印 在java环境下不能使用android.util.log.e()
        messager = processingEnvironment.getMessager();
        // 需要Java 打印方法
        // 文件处理工具
        filer = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        messager.printMessage(Diagnostic.Kind.WARNING, "准备生成文件");
        // 收集被注解标记的类
        Set<? extends Element> activityElements = roundEnvironment.getElementsAnnotatedWith(ActivityDestination.class);
        Set<? extends Element> fragmentElements = roundEnvironment.getElementsAnnotatedWith(FragmentDestination.class);
        HashMap<String, JSONObject> destMap = new HashMap<>();
        handleDestination(activityElements, ActivityDestination.class, destMap);
        handleDestination(fragmentElements, FragmentDestination.class, destMap);
//        String destFileName = "destination.json";
        FileOutputStream fos = null;
        OutputStreamWriter writer = null;
        try {
            FileObject resource = filer.createResource(StandardLocation.CLASS_OUTPUT, "", OUTPUT_FILE_NAME);
            String resourcePath = resource.toUri().getPath();
            // 错误: resourcePath:/Users/zhangfengzhou/GitHub/JetPackLearn/app/build/tmp/kapt3/classes/debug/destination.json
//            messager.printMessage(Diagnostic.Kind.ERROR, "resourcePath:"+resourcePath);
            messager.printMessage(Diagnostic.Kind.WARNING, "resourcePath:++++++" + resourcePath);
            // 由于我们想在app/src/main/assets/目录下 生成json文件，所以需要在这里对resourcePath进行截取
            String appPath = resourcePath.substring(0, resourcePath.indexOf("app") + 4);
            messager.printMessage(Diagnostic.Kind.WARNING, "appPath " + appPath);
            String assetPath = appPath + "src/main/assets";

            File file = new File(assetPath);
            if (!file.exists()) {
                file.mkdirs();
            }

            File outPutFile = new File(file, OUTPUT_FILE_NAME);
            if (outPutFile.exists()) {
                outPutFile.delete();
            }
            outPutFile.createNewFile();

            // 利用FastJson将收集到的所有页面的信 转换成JSON格式的 并输出到文件中
            String content = JSON.toJSONString(destMap);
            fos = new FileOutputStream(outPutFile);
            writer = new OutputStreamWriter(fos);
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            messager.printMessage(Diagnostic.Kind.NOTE, "error:");
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        messager.printMessage(Diagnostic.Kind.WARNING, "文件已经生成");
        return true;
    }

    private void handleDestination(Set<? extends Element> elements, Class<? extends Annotation> annotationClazz, HashMap<String, JSONObject> destMap) {
        for (Element element : elements) {
            TypeElement typeElement = (TypeElement) element;
            String clazzName = typeElement.getQualifiedName().toString();
            int id = Math.abs(clazzName.hashCode());
            String pageUrl = null;
            boolean needLogin = false;
            // 是否是首页第一个展示的面
            boolean asStarter = false;
            // 标记该页面是fragment 还是Activity
            boolean isFragment = false;
            // 获取每个类上的注解信息
            Annotation annotation = element.getAnnotation(annotationClazz);
            if (annotation instanceof ActivityDestination) {
                ActivityDestination dest = (ActivityDestination) annotation;
                pageUrl = dest.pageUrl();
                needLogin = dest.needLogin();
                asStarter = dest.asStarter();
                isFragment = false;
            } else if (annotation instanceof FragmentDestination) {
                FragmentDestination dest = (FragmentDestination) annotation;
                pageUrl = dest.pageUrl();
                needLogin = dest.needLogin();
                asStarter = dest.asStarter();
                isFragment = true;
            }
            if (destMap.containsKey(pageUrl)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "不同的页面不允许使用相同的pageUrl" + pageUrl);
            } else {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("pageUrl", pageUrl);
                jsonObject.put("needLogin", needLogin);
                jsonObject.put("asStarter", asStarter);
                jsonObject.put("id", id);
                jsonObject.put("className", clazzName);
                jsonObject.put("isFragment", isFragment);
                destMap.put(pageUrl, jsonObject);
            }
        }
    }

}
