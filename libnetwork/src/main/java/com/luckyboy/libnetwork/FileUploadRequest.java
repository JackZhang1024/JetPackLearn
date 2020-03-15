package com.luckyboy.libnetwork;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FileUploadRequest<T> extends Request<T, FileUploadRequest> {

    private String filePath;
    private String fileName;

    public FileUploadRequest(String url, String filePath, String fileName) {
        super(url);
        this.filePath = filePath;
        this.fileName = fileName;
    }

    @Override
    protected okhttp3.Request generateRequest(okhttp3.Request.Builder builder) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName,
                        RequestBody.create(MediaType.parse("multipart/form-data"), new File(filePath))
                ).build();
        okhttp3.Request request = builder.url(sUrl).post(requestBody).build();
        return request;
    }

}
