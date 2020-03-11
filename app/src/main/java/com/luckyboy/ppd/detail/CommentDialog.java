package com.luckyboy.ppd.detail;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.luckyboy.jetpacklearn.R;
import com.luckyboy.jetpacklearn.databinding.LayoutCommentDialogBinding;
import com.luckyboy.libcommon.utils.PixUtils;
import com.luckyboy.libcommon.utils.ToastManager;
import com.luckyboy.libcommon.view.PPEditTextView;
import com.luckyboy.libcommon.view.ViewHelper;

public class CommentDialog extends AppCompatDialogFragment implements View.OnClickListener {

    private LayoutCommentDialogBinding mBinding;
    private long itemId;
    private static final String KEY_ITEM_ID = "key_item_id";
    private String filePath;
    private int width, height;
    private boolean isVideo;
    private String coverUrl;
    private String fileUrl;

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
            ToastManager.showToast("发送评论");
        } else if (v.getId() == R.id.comment_video) {
            ToastManager.showToast("拍摄视频");
        } else if (v.getId() == R.id.comment_delete) {
            ToastManager.showToast("删除拍摄的视频");
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }


}
