package com.luckyboy.jetpacklearn.binding

import android.widget.EditText
import android.widget.ImageView
import android.widget.SimpleAdapter
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.luckyboy.jetpacklearn.R
import com.luckyboy.jetpacklearn.common.listener.SimpleWatcher
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

// 全局可以使用
@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageURL:String?){
    if (!imageURL.isNullOrEmpty()){
        Glide.with(view.context)
            .asBitmap()
            .load(imageURL)
            .placeholder(R.drawable.glide_placeholder)
            .centerCrop()
            .into(view)
    }
}


// 加载带圆角的头像
@BindingAdapter("imageTransFromUrl")
fun bindImageTransFromUrl(view: ImageView, imageURL: String?){
    if (!imageURL.isNullOrEmpty()){
        Glide.with(view.context)
            .load(imageURL)
            .apply(bitmapTransform(RoundedCornersTransformation(20,0,
                RoundedCornersTransformation.CornerType.ALL)))
            .into(view)
    }
}

// 文本监听器
@BindingAdapter("addTextChangedListener")
fun addTextChangedListener(editText: EditText, simpleWatcher: SimpleWatcher){
    editText.addTextChangedListener(simpleWatcher)
}

