package com.owly.owlyandroidapp.home

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.view.View
import com.owly.owlyandroidapp.model.Item
import com.owly.owlyandroidapp.R


class MainAdapter(activity: Activity) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {
  private val layoutInflater: LayoutInflater = LayoutInflater.from(activity)
  private var listItem: List<Item>? = null

  fun setListItem(listItem: List<Item>) {
    this.listItem = listItem
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
    val view = layoutInflater.inflate(R.layout.row_fresco, viewGroup, false)
    return ViewHolder(view!!)
  }

  override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
    viewHolder.ivImageView?.setImageURI(listItem!![i].URL)
    viewHolder.txtViewName.text = listItem!![i].Name
    viewHolder.txtViewPrice.text = listItem!![i].Price.toString() + " €"
  }

  override fun getItemCount(): Int {
    return listItem!!.size
  }

  inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

     val ivImageView: WrapContentDrawerView? = itemView.findViewById<View>(R.id.ivImageView) as WrapContentDrawerView?
    val txtViewName: TextView = itemView.findViewById<View>(R.id.txtViewName) as TextView
    val txtViewPrice: TextView = itemView.findViewById<View>(R.id.txtViewPrice) as TextView

  }
}
