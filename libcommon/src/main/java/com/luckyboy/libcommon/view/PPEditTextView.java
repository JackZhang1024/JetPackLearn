package com.luckyboy.libcommon.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.appcompat.widget.AppCompatEditText;

/**
 * dispatchKeyEventPreIme 复写这个方案 可以在对话框中 监听backpress事件
 * 以销毁对话框
 **/
public class PPEditTextView extends AppCompatEditText {

    private OnBackKeyEvent keyEvent;


    public PPEditTextView(Context context) {
        super(context);
    }

    public PPEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PPEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (keyEvent != null) {
                if (keyEvent.onKeyEvent()) {
                    return true;
                }
            }
        }
        return super.dispatchKeyEventPreIme(event);
    }


    public void setOnBackKeyEventListener(OnBackKeyEvent event) {
        this.keyEvent = event;
    }


    public interface OnBackKeyEvent {
        boolean onKeyEvent();
    }
}
