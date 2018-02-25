package com.owly.owlyandroidapp.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.owly.owlyandroidapp.ItemsSingleton
import com.owly.owlyandroidapp.R
import com.owly.owlyandroidapp.view.DynamicHeightImageView
import com.squareup.picasso.Picasso
import q.rorbin.fastimagesize.FastImageSize
import q.rorbin.fastimagesize.request.ImageSizeCallback


class ItemAdapter(private val activity: Activity) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
  private val layoutInflater: LayoutInflater
  private var listItem: List<String>? = null

  fun setListItem(listItem: List<String>) {
    this.listItem = listItem
    notifyDataSetChanged()
  }

  init {
    layoutInflater = LayoutInflater.from(activity)
  }

  override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
    val view = layoutInflater.inflate(R.layout.row_item, viewGroup, false)
    val viewHolder = ViewHolder(view)
    view.tag = viewHolder
    return viewHolder
  }

  override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {


    FastImageSize.with(ItemsSingleton.ITEMS[i].URL).get { size ->
      val imageSize: IntArray
      imageSize = size

      val imageWidth = imageSize[0]
      val imageHeight = imageSize[1]
      Log.e("height", imageHeight.toString() + "")
      Log.e("width", imageWidth.toString() + "")

    }
  }

  override fun getItemCount(): Int {
    return listItem!!.size
  }

  inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @BindView(R.id.ivImageView) internal var ivImageView: DynamicHeightImageView? = null
    @BindView(R.id.tvName) internal var tvName: TextView? = null

    init {
      ButterKnife.bind(this, itemView)
    }
  }
}
