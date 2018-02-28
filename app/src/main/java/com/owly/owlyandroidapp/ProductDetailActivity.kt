package com.owly.owlyandroidapp

import android.graphics.Color
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.owly.owlyandroidapp.fresco.WrapContentDrawerView
import kotterknife.bindView
import org.w3c.dom.Text
import android.text.method.ScrollingMovementMethod



class ProductDetailActivity : AppCompatActivity() {


  val productLabel: TextView by bindView(R.id.ProductName)
  val descriptionLbl: TextView by bindView(R.id.desc)
  val priceLbl: TextView by bindView(R.id.Price)
  val imgView: WrapContentDrawerView by bindView(R.id.imageView22)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_product)
    val extras = intent.extras
    if (extras != null) {
      val name: String? = extras.getString("name")
      val price: String? = extras.getString("price")
      val description: String? = extras.getString("description")
      val url: String? = extras.getString("imageURL")

      productLabel.text = name
      descriptionLbl.text = description
      priceLbl.text = price
      imgView.setImageURI(url)

      descriptionLbl.setMovementMethod(ScrollingMovementMethod())
      priceLbl.setTextColor(Color.parseColor("#ff7e00"));

    }
  }
}
