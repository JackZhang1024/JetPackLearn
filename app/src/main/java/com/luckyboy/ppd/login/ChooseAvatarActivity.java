package com.luckyboy.ppd.login;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.luckyboy.jetpacklearn.BuildConfig;
import com.luckyboy.jetpacklearn.R;
import com.luckyboy.jetpacklearn.databinding.ActivityChooseAvatarBinding;
import com.luckyboy.libcommon.utils.StatusBar;
import com.luckyboy.libcommon.utils.ToastManager;
import com.luckyboy.libnetwork.ApiResponse;
import com.luckyboy.libnetwork.ApiService;
import com.luckyboy.libnetwork.JsonCallback;
import com.luckyboy.ppd.core.model.OSSFile;
import com.luckyboy.ppd.core.view.BottomPicturePickerDialogHelper;
import com.luckyboy.ppd.login.model.UserViewModel;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import io.reactivex.functions.Consumer;

public class ChooseAvatarActivity extends AppCompatActivity {

    private static final String TAG = "ChooseAvatarActivity";
    private ActivityChooseAvatarBinding mBinding;

    public static final int REGISTER_REQUEST_CODE = 1000;

    private static final int TAKE_PHOTO_REQUEST = 100;
    private static final int GALLERY_REQUEST_CODE = 101;
    private static final String USER_PHONE = "user_name";
    private static final String USER_PWD = "user_pwd";

    private File mUserAvatarUserFile;
    private UserViewModel mUserViewModel;

    public static void startChooseAvatarActivityForResult(Activity context, String userName, String passWord) {
        Intent intent = new Intent(context, ChooseAvatarActivity.class);
        intent.putExtra(USER_PHONE, userName);
        intent.putExtra(USER_PWD, passWord);
        context.startActivityForResult(intent, REGISTER_REQUEST_CODE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBar.fitSystemBar(this);
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_choose_avatar);
        mBinding.userAvatar.setOnClickListener(v -> {
            // 显示对话框 进行选择相册还是拍照
            chooseAvatar();
        });
        mBinding.ivUserAvatar.post(() -> {
            mBinding.ivUserAvatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_choose_avatar));
        });
        mBinding.toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        mUserViewModel.phone.setValue(getIntent().getStringExtra(USER_PHONE));
        mUserViewModel.password.setValue(getIntent().getStringExtra(USER_PWD));
        mUserViewModel.status.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String status) {
                if ("success".equals(status)) {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
        mBinding.setModel(mUserViewModel);
    }

    public void chooseAvatar() {
        BottomPicturePickerDialogHelper dialogHelper = new BottomPicturePickerDialogHelper(this, new BottomPicturePickerDialogHelper.OnBottomDialogItemClick() {
            @Override
            public void onSelectGalleryClick() {
                RxPermissions rxPermissions = new RxPermissions(ChooseAvatarActivity.this);
                rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean granted) throws Exception {
                                if (granted) {
                                    Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                                    // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                                    pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                    startActivityForResult(pickIntent, GALLERY_REQUEST_CODE);
                                } else {
                                    showAlertDialog("需要对您手机的存储卡进行读写权限设置才能正常使用");
                                }
                            }
                        });
            }

            @Override
            public void onTakePhotoClick() {
                RxPermissions rxPermissions = new RxPermissions(ChooseAvatarActivity.this);
                rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean granted) throws Exception {
                                if (granted) {
                                    File rootFile = getExternalCacheDir();
                                    File imgPath = new File(rootFile, "PPDImages");
                                    if (!imgPath.exists()) {
                                        imgPath.mkdir();
                                    }
                                    File imgFile = new File(imgPath, "avatar.png");
                                    mUserAvatarUserFile = imgFile;
                                    takePhoto(TAKE_PHOTO_REQUEST, imgFile);
                                } else {
                                    showAlertDialog("需要对您手机的存储卡进行读写权限设置才能正常使用");
                                }
                            }
                        });
            }
        });
        dialogHelper.show();
    }

    private void showAlertDialog(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();

    }

    private void takePhoto(int requestCode, File targetFile) {
        try {
            Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Android 7.0之后 应用访问外部文件会有限制 需要处理
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileProvider", targetFile);
                takeIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            } else {
                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(targetFile));
            }
            startActivityForResult(takeIntent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO_REQUEST:
                    File userAvatarFile = mUserAvatarUserFile;
                    Log.e("拍照图像 ", userAvatarFile.getAbsolutePath());
                    startCropActivity(Uri.fromFile(userAvatarFile));
                    break;
                case GALLERY_REQUEST_CODE:
                    startCropActivity(data.getData());
                    break;
                case UCrop.REQUEST_CROP:
                    handleCropResult(data);
                    break;
                case UCrop.RESULT_ERROR:
                    final Throwable cropError = UCrop.getError(data);
                    ToastManager.showToast(cropError.getMessage());
                    break;
            }
        }
    }


    public void startCropActivity(Uri uri) {
        String targetFileName = String.format("IMG_CROP_%s.png", System.currentTimeMillis());
        int[] maxResultSize = new int[]{720, 1080};
        Uri mDestinationUri = Uri.fromFile(new File(getCacheDir(), targetFileName));
        UCrop.of(uri, mDestinationUri)
                .withAspectRatio(4, 4)
                .withMaxResultSize(maxResultSize[0], maxResultSize[1])
                //.withTargetActivity(CropActivity.class)
                .start(this);
    }

    private void handleCropResult(Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (null != resultUri) {
            String filePath = resultUri.getEncodedPath();
            String imagePath = Uri.decode(filePath);
            Log.e(TAG, "handleCropResult: " + imagePath);
            if (!TextUtils.isEmpty(imagePath)) {
                // 上传头像
                mBinding.setUserAvatarPath(imagePath);
                ToastManager.showToast("准备上传头像");
            }
        } else {
            ToastManager.showToast("无法剪切选择图片");
        }
    }


    // 进行注册最后完成环节 上传头像和用户名和密码
    public void doRegisterFinish(View view) {
        String userAvatarFilePath = mBinding.getUserAvatarPath();
        if (TextUtils.isEmpty(userAvatarFilePath)) {
            ToastManager.showToast("你还没有选择头像");
            return;
        }
        String name = mUserViewModel.name.getValue();
        if (TextUtils.isEmpty(name)) {
            ToastManager.showToast("您还没有取昵称呢");
            return;
        }
        String description = mUserViewModel.userDescription.getValue();
        if (TextUtils.isEmpty(description)) {
            ToastManager.showToast("你还没有描述自己呢");
            return;
        }
        Log.e(TAG, "doRegisterFinish: " + userAvatarFilePath);
        String fileName = userAvatarFilePath.substring(userAvatarFilePath.lastIndexOf("/") + 1);
        Log.e(TAG, "doRegisterFinish: fileName " + fileName);
        ApiService.upload("/fileUploadOSS", userAvatarFilePath, fileName)
                .responseType(String.class)
                .execute(new JsonCallback<OSSFile>() {
                    @Override
                    public void onSuccess(ApiResponse<OSSFile> response) {
                        super.onSuccess(response);
                        Log.e(TAG, "onSuccess: " + response.body.ossUrl);
                        // 上传头像成功后 开始提交用户名和密码
                        //  java.lang.IllegalStateException: Cannot invoke setValue on a background thread
                        mUserViewModel.userAvatar.postValue(response.body.ossUrl);
                        mUserViewModel.register();
                    }

                    @Override
                    public void onError(ApiResponse<OSSFile> response) {
                        super.onError(response);
                        Log.e(TAG, "onError: " + response.message);
                    }
                });
    }


}
