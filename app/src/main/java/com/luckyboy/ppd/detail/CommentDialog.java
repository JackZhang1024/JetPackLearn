package com.luckyboy.ppd.detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.Observer;

import com.luckyboy.jetpacklearn.R;
import com.luckyboy.jetpacklearn.databinding.LayoutCommentDialogBinding;
import com.luckyboy.libcommon.dialog.LoadingDialog;
import com.luckyboy.libcommon.utils.FileUtils;
import com.luckyboy.libcommon.utils.PixUtils;
import com.luckyboy.libcommon.utils.ToastManager;
import com.luckyboy.libcommon.view.PPEditTextView;
import com.luckyboy.libcommon.view.ViewHelper;
import com.luckyboy.libnetwork.ApiResponse;
import com.luckyboy.libnetwork.ApiService;
import com.luckyboy.libnetwork.JsonCallback;
import com.luckyboy.ppd.core.model.Comment;
import com.luckyboy.ppd.core.model.OSSFile;
import com.luckyboy.ppd.login.UserManager;
import com.luckyboy.ppd.publish.CaptureActivity;

import java.util.concurrent.atomic.AtomicInteger;

public class CommentDialog extends AppCompatDialogFragment implements View.OnClickListener {

    private static final String TAG = "CommentDialog";

    private LayoutCommentDialogBinding mBinding;
    private long itemId;
    private static final String KEY_ITEM_ID = "key_item_id";
    private String filePath;
    private int width, height;
    private boolean isVideo;
    private String coverUrl;
    private String fileUrl;
    private LoadingDialog loadingDialog;
    // 加载进度条对话框

    public static CommentDialog newInstance(long itemId) {
        Bundle args = new Bundle();
        args.putLong(KEY_ITEM_ID, itemId);
        CommentDialog fragment = new CommentDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        window.setWindowAnimations(0);

        ViewGroup viewGroup = window.findViewById(android.R.id.content);
        mBinding = LayoutCommentDialogBinding.inflate(inflater, viewGroup, false);
        mBinding.commentVideo.setOnClickListener(this);
        mBinding.commentDelete.setOnClickListener(this);
        mBinding.commentSend.setOnClickListener(this);

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        this.itemId = getArguments().getLong(KEY_ITEM_ID);
        ViewHelper.setViewOutline(mBinding.getRoot(), PixUtils.dp2px(10), ViewHelper.RADIUS_TOP);

        mBinding.getRoot().post(() -> showSoftInputMethod());

        dismissWhenPressBack();
        return mBinding.getRoot();
    }

    private void dismissWhenPressBack() {
        mBinding.inputView.setOnBackKeyEventListener(new PPEditTextView.OnBackKeyEvent() {
            @Override
            public boolean onKeyEvent() {
                mBinding.inputView.postDelayed(() -> {
                    dismiss();
                }, 200);
                return true;
            }
        });
    }

    private void showSoftInputMethod() {
        mBinding.inputView.setFocusable(true);
        mBinding.inputView.setFocusableInTouchMode(true);
        // 请求获取焦点
        mBinding.inputView.requestFocus();
        InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(mBinding.inputView, 0);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.comment_send) {
            publishComment();
        } else if (v.getId() == R.id.comment_video) {
            CaptureActivity.startActivityForResult(getActivity());
        } else if (v.getId() == R.id.comment_delete) {
            filePath = null;
            fileUrl = null;
            coverUrl = null;
            isVideo = false;
            width = 0;
            height = 0;
            mBinding.commentVideo.setImageDrawable(null);
            mBinding.commentExtLayout.setVisibility(View.GONE);
            mBinding.commentVideo.setEnabled(true);
            mBinding.commentVideo.setAlpha(255);
        }
    }


    @Override
    public void dismiss() {
        super.dismiss();
        dismissLoadingDialog();
        filePath = null;
        fileUrl = null;
        coverUrl = null;
        isVideo = false;
        width = 0;
        height = 0;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e(TAG, "onActivityResult: 怎么就没有接到 ");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CaptureActivity.REQ_CAPTURE && resultCode == Activity.RESULT_OK) {
            filePath = data.getStringExtra(CaptureActivity.RESULT_FILE_PATH);
            Log.e(TAG, "onActivityResult: "+filePath);
            width = data.getIntExtra(CaptureActivity.RESULT_FILE_WIDTH, 0);
            height = data.getIntExtra(CaptureActivity.RESULT_FILE_HEIGHT, 0);
            isVideo = data.getBooleanExtra(CaptureActivity.RESULT_FILE_TYPE, false);
            Log.e(TAG, "onActivityResult: 评论对话框  " + filePath);
            if (!TextUtils.isEmpty(filePath)) {
                mBinding.commentExtLayout.setVisibility(View.VISIBLE);
                mBinding.commentCover.setImageUrl(filePath);
                if (isVideo) {
                    mBinding.commentIconVideo.setVisibility(View.VISIBLE);
                }
            }
            // 可以发送视频
            mBinding.commentVideo.setEnabled(false);
            mBinding.commentIconVideo.setAlpha(80);
        }
    }

    private void publishComment() {
        if (TextUtils.isEmpty(mBinding.inputView.getText())) {
            ToastManager.showToast("评论内容不能为空");
            return;
        }
        if (isVideo && !TextUtils.isEmpty(filePath)) {
            // 生成视频封面图片
            FileUtils.generateVideoCover(filePath).observe(this, new Observer<String>() {
                @Override
                public void onChanged(String coverPath) {
                    // 上传视频封面图片和视频文件
                    uploadFile(coverPath, filePath);
                }
            });
        } else if (!TextUtils.isEmpty(filePath)) {
            // 上传图片文件
            uploadFile(null, filePath);
        } else {
            // 发布平路
            publish();
        }
    }


    // 利用接口 上传 视频封面图片和视频
    private void uploadFile(String coverPath, String videoFilePath) {
        showLoadingDialog();
        AtomicInteger count = new AtomicInteger(1);
        if (!TextUtils.isEmpty(coverPath)) {
            count.set(2);
            String fileName = coverPath.substring(coverPath.lastIndexOf("/") + 1);
            ApiService.upload("/fileUploadOSS", coverPath, fileName)
                    .responseType(String.class)
                    .execute(new JsonCallback<OSSFile>() {
                        @Override
                        public void onSuccess(ApiResponse<OSSFile> response) {
                            super.onSuccess(response);
                            Log.e(TAG, "onSuccess: 1 " + response.body.ossUrl);
                            coverUrl = response.body.ossUrl;
                            int remain = count.decrementAndGet();
                            Log.e(TAG, "onSuccess: 1 remain " + remain);
                            if (remain <= 0) {
                                if (!TextUtils.isEmpty(fileUrl) && !TextUtils.isEmpty(coverUrl)) {
                                    publish();
                                } else {
                                    dismissLoadingDialog();
                                    ToastManager.showToast("文件上传失败，请重新发布");
                                }
                            }
                        }

                        @Override
                        public void onError(ApiResponse<OSSFile> response) {
                            super.onError(response);
                            Log.e(TAG, "onError: " + response.message);
                        }
                    });
        }
        String fileName = videoFilePath.substring(videoFilePath.lastIndexOf("/") + 1);
        ApiService.upload("/fileUploadOSS", videoFilePath, fileName)
                .responseType(String.class)
                .execute(new JsonCallback<OSSFile>() {
                    @Override
                    public void onSuccess(ApiResponse<OSSFile> response) {
                        super.onSuccess(response);
                        Log.e(TAG, "onSuccess: 2 " + response.body.ossUrl);
                        fileUrl = response.body.ossUrl;
                        int remain = count.decrementAndGet();
                        Log.e(TAG, "onSuccess: 2 remain " + remain);
                        if (remain <= 0) {
                            // 注意：这里的细节 是或的关系 就是前面如果是图片 也可以发布评论
                            if (!TextUtils.isEmpty(fileUrl) || !TextUtils.isEmpty(coverUrl) && !TextUtils.isEmpty(coverPath)) {
                                publish();
                            } else {
                                dismissLoadingDialog();
                                ToastManager.showToast("文件上传失败，请重新发布");
                            }
                        }
                    }

                    @Override
                    public void onError(ApiResponse<OSSFile> response) {
                        super.onError(response);
                        Log.e(TAG, "onError: " + response.message);
                    }
                });
    }

    // 发布帖子
    private void publish() {
        String commentText = mBinding.inputView.getText().toString();
        ApiService.post("/comment/addComment")
                .addParam("userId", UserManager.get().getUserId())
                .addParam("itemId", itemId)
                .addParam("commentText", commentText)
                .addParam("image_url", isVideo ? coverUrl : fileUrl)
                .addParam("video_url", isVideo ? fileUrl : null)
                .addParam("width", width)
                .addParam("height", height)
                .execute(new JsonCallback<Comment>() {
                    @Override
                    public void onSuccess(ApiResponse<Comment> response) {
                        onCommentSuccess(response.body);
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onError(ApiResponse<Comment> response) {
                        ToastManager.showToast("评论失败 " + response.message);
                        dismissLoadingDialog();
                    }
                });

    }

    private void onCommentSuccess(Comment body) {
        ToastManager.showToast("评论成功");
        // 评论成功后 添加评论到列表项上
        ArchTaskExecutor.getMainThreadExecutor().execute(() -> {
            if (mCommentAddListener != null) {
                mCommentAddListener.onAddComment(body);
            }
            dismiss();
        });
    }

    private void showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(getContext());
            loadingDialog.setLoadingText(getString(R.string.publishing));
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.setCancelable(false);
        }
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    private void dismissLoadingDialog() {
        if (loadingDialog != null) {
            // dismissoadingDialog 的调用可能出现在异步线程中
            if (Looper.myLooper() != Looper.getMainLooper()) {
                ArchTaskExecutor.getMainThreadExecutor().execute(() -> {
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                });
            } else if (loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }

        }
    }

    public interface CommentAddListener {

        void onAddComment(Comment comment);
    }

    private CommentAddListener mCommentAddListener;

    public void setCommentAddListener(CommentAddListener listener) {
        this.mCommentAddListener = listener;
    }

}
