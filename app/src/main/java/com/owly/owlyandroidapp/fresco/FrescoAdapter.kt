package com.owly.owlyandroidapp.fresco

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.owly.owlyandroidapp.R
import com.owly.owlyandroidapp.bean.Item


class FrescoAdapter(activity: Activity) : RecyclerView.Adapter<FrescoAdapter.ViewHolder>() {
  private val layoutInflater: LayoutInflater = LayoutInflater.from(activity)
  private var listItem2: List<Item>? = null

  fun setListItem(listItem2: List<Item>) {
    this.listItem2 = listItem2
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
    val view = layoutInflater.inflate(R.layout.row_fresco, viewGroup, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
    viewHolder.ivImageView?.setImageURI(listItem2!![i].thumbnailImage)
    viewHolder.txtViewName.text = listItem2!![i].name
    viewHolder.txtViewPrice.text = listItem2!![i].salePrice.toString() + " €"
  }

  override fun getItemCount(): Int {
    return listItem2!!.size
  }

  inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

     val ivImageView: WrapContentDrawerView? = itemView.findViewById<View>(R.id.ivImageView) as WrapContentDrawerView?
    val txtViewName: TextView = itemView.findViewById<View>(R.id.txtViewName) as TextView
    val txtViewPrice: TextView = itemView.findViewById<View>(R.id.txtViewPrice) as TextView

  }
}
