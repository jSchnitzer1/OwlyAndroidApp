package com.owly.owlyandroidapp

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.facebook.AccessToken
import com.facebook.Profile
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.owly.owlyandroidapp.bean.Item
import com.owly.owlyandroidapp.fresco.FrescoAdapter
import com.owly.owlyandroidapp.view.EndlessRecyclerViewScrollListener
import com.squareup.picasso.Picasso

import java.io.IOException
import java.util.ArrayList

import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotterknife.bindOptionalView


class ProfileActivity : Activity, OnMapReadyCallback {

  internal var accessToken: AccessToken? = null
  internal var mAuth: FirebaseAuth? = null

  internal lateinit var username: TextView
  internal lateinit var imgUser: CircleImageView

  internal lateinit var map: GoogleMap
  internal var marker: Marker? = null
  internal val rvList: RecyclerView? by bindOptionalView(R.id.rvListProfile)
  private var endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener? = null
  private val listItem2 = ArrayList<Item>()

  private val isLoggedInFacebook: Boolean
    get() = accessToken != null

  private val isLoggedInGoogle: Boolean
    get() = mAuth != null
  private var itemAdapter: FrescoAdapter? = null

  constructor() : super()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_profile)

    initializeGUI()
    initializeFacebook()
    initializeGoogle()
    showLoginInfo()
    initializeGoogleMaps()

    initRecyclerView()
  }

  private fun initRecyclerView() {
    Fresco.initialize(this)

    val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    staggeredGridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS

    val manager = GridLayoutManager(this, 6)
    manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
      override fun getSpanSize(position: Int): Int {
        val index = position % 5
        when (index) {
          in 0..2 -> return 2
          3, 4 -> return 3
        }
        return -1
      }
    }

    rvList!!.layoutManager = staggeredGridLayoutManager
    itemAdapter = FrescoAdapter(this, { goToProductDetailWith(it) })
    WalmartApiService.create().trends()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe({ result ->
          //Log.d("Result", "There are ${result.items.size} Java developers in Lagos")
          listItem2.addAll(result.items.subList(0, 9))
          itemAdapter!!.setListItem(listItem2)
          rvList!!.adapter = itemAdapter
          rvList!!.itemAnimator = null

        }, { error ->
          error.printStackTrace()
        })
    endlessRecyclerViewScrollListener = object : EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
      override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {}
      override fun lastVisibleItem(lastVisibleItem: Int) {}
    }
    rvList!!.addOnScrollListener(endlessRecyclerViewScrollListener)
  }

  private fun initializeGoogleMaps() {
    val mapFragment = fragmentManager.findFragmentById(R.id.map) as MapFragment
    mapFragment.getMapAsync(this)
  }

  private fun initializeGUI() {
    val str1 = findViewById<View>(R.id.str1) as ImageView
    str1.setBackgroundResource(R.drawable.star)
    val str2 = findViewById<View>(R.id.str2) as ImageView
    str2.setBackgroundResource(R.drawable.star)
    val str3 = findViewById<View>(R.id.str3) as ImageView
    str3.setBackgroundResource(R.drawable.star)
    val str4 = findViewById<View>(R.id.str4) as ImageView
    str4.setBackgroundResource(R.drawable.star)
    val str5 = findViewById<View>(R.id.str5) as ImageView
    str5.setBackgroundResource(R.drawable.star)

    imgUser = findViewById(R.id.imgUser)
    username = findViewById(R.id.username)
  }

  private fun showLoginInfo() {
    if (isLoggedInFacebook) {
      val profile = Profile.getCurrentProfile()
      if (profile != null) {
        val name = profile.name
        val imgUrl = profile.getProfilePictureUri(70, 70).toString()

        username.text = name
        Picasso.with(applicationContext).load(imgUrl)
            .placeholder(R.drawable.default_user).error(R.drawable.ic_launcher_background)
            .into(imgUser)

        return
      }
    } else if (isLoggedInGoogle) {
      val acct = GoogleSignIn.getLastSignedInAccount(this)
      if (acct != null) {
        val name = acct.displayName
        val imgUrl = acct.photoUrl!!.toString()

        username.text = name
        Picasso.with(applicationContext).load(imgUrl)
            .placeholder(R.drawable.default_user).error(R.drawable.ic_launcher_background)
            .into(imgUser)
        return
      }
    }

    username.text = "Unknown"
    imgUser.setImageResource(R.drawable.default_user)

  }

  private fun initializeFacebook() {
    accessToken = AccessToken.getCurrentAccessToken()
  }

  private fun initializeGoogle() {
    mAuth = FirebaseAuth.getInstance()
  }

  override fun onMapReady(googleMap: GoogleMap) {
    map = googleMap
    //goToLocation(59.329073, 18.068043, 13);
    geoLocate()
  }

  private fun goToLocation(lat: Double, lng: Double, zoom: Int) {
    val ll = LatLng(lat, lng)
    val update = CameraUpdateFactory.newLatLngZoom(ll, zoom.toFloat())
    map.moveCamera(update)
  }

  private fun geoLocate() {
    val location = "HM Stockholm"
    val gc = Geocoder(this)
    try {
      val list = gc.getFromLocationName(location, 1)
      if (list.size > 0) {
        val address = list[0]
        val locality = address.locality
        val lat = address.latitude
        val lng = address.longitude

        setMarker(locality, lat, lng, location)
      }

    } catch (e: IOException) {
    }

  }

  private fun goToProductDetailWith(item: Item) {
    val i = Intent(this@ProfileActivity, ProductDetailActivity::class.java)
    i.putExtra("name", item.name)
    i.putExtra("price", item.salePrice.toString() + " $")
    i.putExtra("description", item.shortDescription)
    i.putExtra("imageURL", item.mediumImage)
    startActivity(i)
  }
  private fun setMarker(locality: String, lat: Double, lng: Double, location: String) {
    if (marker != null)
      marker!!.remove()

    val options = MarkerOptions()
        .title(locality)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.hm_48))
        .position(LatLng(lat, lng))
        .snippet(location)

    marker = map.addMarker(options)
    goToLocation(lat, lng, 17)
  }
}
