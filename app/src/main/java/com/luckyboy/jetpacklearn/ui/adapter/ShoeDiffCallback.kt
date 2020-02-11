package com.luckyboy.jetpacklearn.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.luckyboy.jetpacklearn.db.data.Shoe

// ShoeDiffCallBack ??
class ShoeDiffCallback :DiffUtil.ItemCallback<Shoe>(){

    override fun areItemsTheSame(oldItem: Shoe, newItem: Shoe): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Shoe, newItem: Shoe): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return oldItem == newItem
    }


}