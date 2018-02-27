package com.owly.owlyandroidapp

import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.Menu
import android.view.MenuInflater
import com.owly.owlyandroidapp.bean.Item
import android.view.MenuItem
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*

import com.facebook.AccessToken
import com.facebook.Profile
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.owly.owlyandroidapp.fresco.FrescoAdapter
import com.owly.owlyandroidapp.view.EndlessRecyclerViewScrollListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.*
import kotterknife.bindOptionalView
import kotterknife.bindView

import java.util.ArrayList

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

  private var mDrawerLayout: DrawerLayout? = null
  private var mToggle: ActionBarDrawerToggle? = null
  private val home_nav_view:NavigationView? by bindOptionalView(R.id.home_nav_view)

  internal var accessToken: AccessToken? = null

  internal var mAuth: FirebaseAuth? = null
  internal lateinit var mAuthListener: FirebaseAuth.AuthStateListener
  internal val rvList: RecyclerView? by bindOptionalView(R.id.rvList)
  val dropdown: Spinner? by bindOptionalView(R.id.category)
  val search: EditText? by bindOptionalView(R.id.editText)
  val searchButton: ImageButton? by bindOptionalView(R.id.search)
  internal var tvLastItem: TextView? = null
  private var itemAdapter: FrescoAdapter? = null
  private val listItem2 = ArrayList<Item>()
  var notInSearch = true
  private var disposable: Disposable? = null

  private val walmartAPIService by lazy {
    WalmartApiService.create()
  }



  private var endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener? = null

  private val isLoggedInFacebook: Boolean
    get() = accessToken != null

  private val isLoggedInGoogle: Boolean
    get() = mAuth != null

  override fun onStart() {
    super.onStart()
    if (mAuth != null)
      mAuth!!.addAuthStateListener(mAuthListener)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_home)

    initializeFacebook()
    initializeGoogle()
    initializeGUI()

    showLoginStatus()
    Fresco.initialize(this)


    val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    staggeredGridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS

    val manager = GridLayoutManager(this, 6)
    manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
      override fun getSpanSize(position: Int): Int {
        val index = position % 5
        when (index) {
          0 -> return 2
          1 -> return 2
          2 -> return 2
          3 -> return 3
          4 -> return 3
        }
        return -1
      }
    }



    search!!.setOnEditorActionListener() { v, actionId, event ->
      if(actionId == EditorInfo.IME_ACTION_DONE){
        searchButton!!.performClick()
        true
      } else {
        false
      }
    }

    rvList!!.layoutManager = staggeredGridLayoutManager
    itemAdapter = FrescoAdapter(this)
    WalmartApiService.create().trends()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe ({
          result ->
          //Log.d("Result", "There are ${result.items.size} Java developers in Lagos")
          listItem2.addAll(result.items.subList(0,9))
          itemAdapter!!.setListItem(listItem2)
          rvList!!.adapter = itemAdapter
          rvList!!.itemAnimator = null

        }, { error ->
          error.printStackTrace()
        })



    endlessRecyclerViewScrollListener = object : EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {

      override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
        /* Log.e("", "onLoadMore: ---------------------------------------->" + page);*/
        val localList = ArrayList<Item>()
        if (page == 1 && notInSearch) {
          WalmartApiService.create().trends()
              .observeOn(AndroidSchedulers.mainThread())
              .subscribeOn(Schedulers.io())
              .subscribe ({
                result ->
                localList.addAll(result.items.subList(10,19))
                listItem2.addAll(localList)
                itemAdapter!!.setListItem(listItem2)
              }, { error ->
                error.printStackTrace()
              })
        }else if (page == 2 && notInSearch) {
          WalmartApiService.create().search("SmartPhone")
              .observeOn(AndroidSchedulers.mainThread())
              .subscribeOn(Schedulers.io())
              .subscribe ({
                result ->
                localList.addAll(result.items)
                listItem2.addAll(localList)
                itemAdapter!!.setListItem(listItem2)
              }, { error ->
                error.printStackTrace()
              })
        }


      }

      override fun lastVisibleItem(lastVisibleItem: Int) {
        //            Log.e("", "lastVisibleItem: -------------------------------------->" + lastVisibleItem);
        //            tvLastItem.setText("With in " + lastVisibleItem);
      }
    }
    rvList!!.addOnScrollListener(endlessRecyclerViewScrollListener)

  }

  fun beginSearch(v: View) {

    if (v.id == R.id.search) {
      Log.d("Info",dropdown!!.selectedItem.toString())
      Log.d("Info2",search!!.text.toString())

      notInSearch = false
      WalmartApiService.create().searchWith(getText(),getCategoryId())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribeOn(Schedulers.io())
          .subscribe ({
            result ->
            //Log.d("Result", "There are ${result.items.size} Java developers in Lagos")
            listItem2.clear()
            listItem2.addAll(result.items)
            itemAdapter!!.setListItem(listItem2)

          }, { error ->
            error.printStackTrace()
          })
    }
  }

  fun getCategoryId() : Int{
    val item = dropdown!!.selectedItem.toString()
    return when (item){
      "Electronics" -> 3944
      "Home" -> 4044
      "Video Games" -> 2636
      "Health" -> 976760
      "Books" -> 3920
      else -> 0
    }
  }

  fun getText():String = search!!.text.toString()

  private fun initializeGUI() {
    val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
    setSupportActionBar(toolbar)

    val dropdown = findViewById<Spinner>(R.id.category)
    val items = arrayOf("Electronics", "Home", "Video Games","Books")
    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
    dropdown.adapter = adapter

    mDrawerLayout = findViewById<View>(R.id.drawerLayout) as DrawerLayout
    mToggle = ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close)
    mDrawerLayout!!.addDrawerListener(mToggle!!)
    mToggle!!.syncState()
    this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    home_nav_view
    home_nav_view!!.setNavigationItemSelectedListener(this)
  }

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.profile_item -> {
        val i = Intent(this@HomeActivity, ProfileActivity::class.java)
        startActivity(i)
      }
      R.id.logout_item -> {
        logout()
      }
      R.id.settings_item -> {
      }
      R.id.about_us_item -> {
        val i = Intent(this@HomeActivity, AboutusActivity::class.java)
        startActivity(i)
      }
    }

    mDrawerLayout!!.closeDrawer(GravityCompat.START)
    return true
  }

  private fun showLoginStatus() {
    if (isLoggedInFacebook) {
      val profile = Profile.getCurrentProfile()
      if (profile != null) {
        val firstName = profile.firstName
        Snackbar.make(findViewById<View>(R.id.search), "Hi $firstName\nYou are logged in using Facebook account", Snackbar.LENGTH_SHORT).show()
      } else {
        Snackbar.make(findViewById<View>(R.id.search), "Hi\nYou are logged in using Facebook account", Snackbar.LENGTH_SHORT).show()
      }
    } else if (isLoggedInGoogle) {
      val acct = GoogleSignIn.getLastSignedInAccount(this)
      if (acct != null) {
        val personGivenName = acct.givenName
        Snackbar.make(findViewById<View>(R.id.search), "Hi $personGivenName\nYou are logged in using Google account", Snackbar.LENGTH_SHORT).show()
      }
    }

  }

  private fun initializeFacebook() {
    accessToken = AccessToken.getCurrentAccessToken()
  }

  private fun initializeGoogle() {
    mAuth = FirebaseAuth.getInstance()
    mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
      if (firebaseAuth.currentUser == null && !isLoggedInFacebook) {
        goToLoginActivity("You are logged out Google successfully!")
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val menuInflater = menuInflater
    menuInflater.inflate(R.menu.bar_menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if(mToggle!!.onOptionsItemSelected(item)) {
      return true
    }
    when (item.itemId) {
      R.id.logout_item -> logout()
    }
    return super.onOptionsItemSelected(item)
  }

  private fun logout() {
    if (isLoggedInFacebook) {
      LoginManager.getInstance().logOut()
      goToLoginActivity("You are logged out Facebook successfully!")
    } else if (isLoggedInGoogle) {
      mAuth!!.signOut()
    }
  }


  private fun goToLoginActivity(msg: String) {
    val i = Intent(this@HomeActivity, LoginActivity::class.java)
    i.putExtra("txtStatusMsg", msg)
    startActivity(i)
  }
}
