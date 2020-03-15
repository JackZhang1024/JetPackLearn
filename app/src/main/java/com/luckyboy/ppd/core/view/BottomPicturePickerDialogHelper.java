package com.luckyboy.ppd.core.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.luckyboy.jetpacklearn.R;

/*  底部图片相册拍照功能 */
public class BottomPicturePickerDialogHelper {

    private Dialog mBottomDialog;

    public BottomPicturePickerDialogHelper(Context context, OnBottomDialogItemClick onBottomDialogItemClick) {
        initBottomDialog(context, onBottomDialogItemClick);
    }

    private void initBottomDialog(Context context, final OnBottomDialogItemClick onBottomDialogItemClick) {
        mBottomDialog = new Dialog(context, R.style.BottomDialog);
        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_bottom_picker, null);
        mBottomDialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        mBottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        mBottomDialog.setCanceledOnTouchOutside(true);
        TextView btnSelectGallery = contentView.findViewById(R.id.btn_select_gallery);
        btnSelectGallery.setOnClickListener((view) -> {
            if (onBottomDialogItemClick != null) {
                onBottomDialogItemClick.onSelectGalleryClick();
            }
            mBottomDialog.dismiss();
        });
        TextView btnTakePhoto = contentView.findViewById(R.id.btn_take_photo);
        btnTakePhoto.setOnClickListener((view) -> {
            if (onBottomDialogItemClick != null) {
                onBottomDialogItemClick.onTakePhotoClick();
            }
            mBottomDialog.dismiss();
        });
        TextView btnCancel = contentView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener((view) -> {
            dismiss();
        });
    }

    public void show() {
        if (mBottomDialog != null && !mBottomDialog.isShowing()) {
            mBottomDialog.show();
        }
    }

    public void dismiss() {
        if (mBottomDialog != null && mBottomDialog.isShowing()) {
            mBottomDialog.dismiss();
        }
    }

    public OnBottomDialogItemClick onBottomDialogItemClick;

    public void setOnBottomDialogItemClick(OnBottomDialogItemClick onBottomDialogItemClick) {
        this.onBottomDialogItemClick = onBottomDialogItemClick;
    }

    public interface OnBottomDialogItemClick {
        void onSelectGalleryClick();

        void onTakePhotoClick();
    }

}
