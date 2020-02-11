package com.luckyboy.jetpacklearn.ui.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.luckyboy.jetpacklearn.databinding.ShoeRecyclerItemBinding
import com.luckyboy.jetpacklearn.db.data.Shoe

// 鞋子的适配器 配合DataBinding 使用
class ShoeAdapter constructor(val context: Context):
        PagedListAdapter<Shoe, ShoeAdapter.ViewHolder>(ShoeDiffCallback()){

    private val TAG:String by lazy {
        this::class.java.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ShoeRecyclerItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val shoe = getItem(position)
        holder.apply {
            bind(onCreateListener(shoe!!.id), shoe)
            itemView.tag = shoe
        }
    }

    // holder的点击事件
    private fun onCreateListener(id:Long):View.OnClickListener{
        return View.OnClickListener {
//            val intent = Intent(context, )
            Log.e(TAG, "position $id")
            Toast.makeText(context, "position $id 被点击了", Toast.LENGTH_SHORT).show()
        }
    }


    class ViewHolder(private val binding: ShoeRecyclerItemBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(listener: View.OnClickListener, item:Shoe){
            binding.apply {
                this.listener = listener
                this.shoe = item
                executePendingBindings()
            }
        }
    }






















}
























