package com.owly.owlyandroidapp

import android.content.ClipData
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.design.widget.NavigationView
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import com.owly.owlyandroidapp.bean.Item
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*

import com.facebook.AccessToken
import com.facebook.Profile
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.login.LoginManager
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.mancj.materialsearchbar.MaterialSearchBar
import com.owly.owlyandroidapp.fresco.FrescoAdapter
import com.owly.owlyandroidapp.view.EndlessRecyclerViewScrollListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.*
import kotterknife.bindOptionalView
import kotterknife.bindView

import java.util.ArrayList

class HomeActivity() : AppCompatActivity(), OnNavigationItemSelectedListener, MaterialSearchBar.OnSearchActionListener, PopupMenu.OnMenuItemClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

  private var mDrawerLayout: DrawerLayout? = null
  private var mToggle: ActionBarDrawerToggle? = null
  private val home_nav_view: NavigationView? by bindOptionalView(R.id.home_nav_view)

  internal var accessToken: AccessToken? = null

  internal var mAuth: FirebaseAuth? = null
  internal lateinit var mAuthListener: FirebaseAuth.AuthStateListener
  private var mGoogleApiClient: GoogleApiClient? = null

  internal val rvList: RecyclerView? by bindOptionalView(R.id.rvList)
  internal var tvLastItem: TextView? = null
  private var itemAdapter: FrescoAdapter? = null
  private val listItem2 = ArrayList<Item>()
  var notInSearch = true

  var lastSearches: List<String>? = null

  val searchBar: MaterialSearchBar? by bindOptionalView(R.id.searchBar)

  val error: TextView? by bindOptionalView(R.id.error)
  val spin_kit: com.github.ybq.android.spinkit.SpinKitView? by bindOptionalView(R.id.spin_kit)


  private var endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener? = null

  private val isLoggedInFacebook: Boolean
    get() = accessToken != null

  private val isLoggedInGoogle: Boolean
    get() = mAuth != null

  constructor(parcel: Parcel) : this() {
    accessToken = parcel.readParcelable(AccessToken::class.java.classLoader)
    notInSearch = parcel.readByte() != 0.toByte()
    lastSearches = parcel.createStringArrayList()
  }

  override fun onStart() {
    super.onStart()
    if (mAuth != null)
      mAuth!!.addAuthStateListener(mAuthListener)
  }

  override fun onConnectionSuspended(p0: Int) {
  }

  override fun onConnected(p0: Bundle?) {
  }

  override fun onConnectionFailed(p0: ConnectionResult) {
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_home)

    initializeFacebook()
    initializeGoogle()
    initializeGUI()

    showLoginStatus()
    Fresco.initialize(this)

    //enable searchbar callbacks
    searchBar!!.setOnSearchActionListener(this)
    //restore last queries from disk
    //Inflate menu and setup OnMenuItemClickListener
    searchBar!!.inflateMenu(R.menu.bar_menu)

    searchBar!!.getMenu().setOnMenuItemClickListener(this);


    val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    staggeredGridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS

    val manager = GridLayoutManager(this, 6)
    manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
      override fun getSpanSize(position: Int): Int {
        val index = position % 5
        when (index) {
          in 0..2 -> return 2
          3,4 -> return 3
        }
        return -1
      }
    }

    rvList!!.layoutManager = staggeredGridLayoutManager
    itemAdapter = FrescoAdapter(this, {
      goToProductDetailWith(it)
    })
    WalmartApiService.create().trends()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe({ result ->
          //Log.d("Result", "There are ${result.items.size} Java developers in Lagos")
          error!!.visibility = View.GONE
          spin_kit!!.visibility = View.GONE
          listItem2.addAll(result.items.subList(0, 9))
          itemAdapter!!.setListItem(listItem2)
          rvList!!.adapter = itemAdapter
          rvList!!.itemAnimator = null

        }, { err ->
          spin_kit!!.visibility = View.GONE
          error!!.visibility = View.VISIBLE

          err.printStackTrace()
        })



    endlessRecyclerViewScrollListener = object : EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {

      override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
        /* Log.e("", "onLoadMore: ---------------------------------------->" + page);*/
        val localList = ArrayList<Item>()
        if (page == 1 && notInSearch) {
          WalmartApiService.create().trends()
              .observeOn(AndroidSchedulers.mainThread())
              .subscribeOn(Schedulers.io())
              .subscribe({ result ->
                localList.addAll(result.items.subList(10, 19))
                listItem2.addAll(localList)
                itemAdapter!!.setListItem(listItem2)
              }, { error ->
                error.printStackTrace()
              })
        } else if (page == 2 && notInSearch) {
          WalmartApiService.create().search("SmartPhone")
              .observeOn(AndroidSchedulers.mainThread())
              .subscribeOn(Schedulers.io())
              .subscribe({ result ->
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


  private fun initializeGUI() {

    mDrawerLayout = findViewById<View>(R.id.drawerLayout) as DrawerLayout

    home_nav_view!!.setNavigationItemSelectedListener(this)
  }

  override fun onSearchStateChanged(enabled: Boolean) {
    val s = if (enabled) "enabled" else "disabled";
    Toast.makeText(this, "Search " + s, Toast.LENGTH_SHORT).show();
  }

  override fun onSearchConfirmed(text: CharSequence?) {
    spin_kit!!.visibility = View.VISIBLE
    WalmartApiService.create().search(text.toString())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe({ result ->
          error!!.visibility = View.GONE
          spin_kit!!.visibility = View.GONE
          listItem2.clear()
          listItem2.addAll(result.items.subList(0, 9))
          itemAdapter!!.setListItem(listItem2)
          searchBar!!.disableSearch()

        }, { err ->
          spin_kit!!.visibility = View.GONE
          error!!.visibility = View.VISIBLE
          err.printStackTrace()
        })

  }

  private fun goToProductDetailWith(item: Item) {
    val i = Intent(this@HomeActivity, ProductDetailActivity::class.java)
    i.putExtra("name", item.name)
    i.putExtra("price", item.salePrice.toString() + " €")
    i.putExtra("description", item.shortDescription)
    i.putExtra("imageURL", item.mediumImage)
    startActivity(i)
  }

  override fun onButtonClicked(buttonCode: Int) {
    when (buttonCode) {
      MaterialSearchBar.BUTTON_NAVIGATION -> mDrawerLayout!!.openDrawer(Gravity.LEFT)
      MaterialSearchBar.BUTTON_SPEECH -> {
      }
      MaterialSearchBar.BUTTON_BACK -> searchBar!!.disableSearch()
    }
  }

  override fun onMenuItemClick(item: MenuItem?): Boolean {
    when (item!!.itemId) {
      R.id.logout_item -> {
        logout()
      }
    }

    mDrawerLayout!!.closeDrawer(GravityCompat.START)
    return true
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
        Snackbar.make(findViewById<View>(R.id.searchBar), "Hi $firstName\nYou are logged in using Facebook account", Snackbar.LENGTH_SHORT).show()
      } else {
        Snackbar.make(findViewById<View>(R.id.searchBar), "Hi\nYou are logged in using Facebook account", Snackbar.LENGTH_SHORT).show()
      }
    } else if (isLoggedInGoogle) {
      val acct = GoogleSignIn.getLastSignedInAccount(this)
      if (acct != null) {
        val personGivenName = acct.givenName
        Snackbar.make(findViewById<View>(R.id.searchBar), "Hi $personGivenName\nYou are logged in using Google account", Snackbar.LENGTH_SHORT).show()
      }
    }

  }

  private fun initializeFacebook() {
    accessToken = AccessToken.getCurrentAccessToken()
  }

  private fun initializeGoogle() {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(R.string.default_web_client_id.toString())
            .requestEmail()
            .build()

    mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

    mAuth = FirebaseAuth.getInstance()
    mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
      if (firebaseAuth.currentUser == null && !isLoggedInFacebook) {

        val aBuilder = AlertDialog.Builder(this)
        aBuilder.setMessage("Do you also to revoke all access from OWLY?")
                .setCancelable(false)
                .setPositiveButton("Yes", object: DialogInterface.OnClickListener {
                  override fun onClick(dialog:DialogInterface, which:Int) {
                    Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback {
                      goToLoginActivity("You are logged out and revoked Google Sign in!")
                    }
                  }
                })
                .setNegativeButton("No", object: DialogInterface.OnClickListener {
                  override fun onClick(dialog:DialogInterface, which:Int) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback {
                      goToLoginActivity("You are logged out Google successfully!")
                    }
                  }
                })
        val alert = aBuilder.create()
        alert.setTitle("Logout Alert")
        alert.show()
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val menuInflater = menuInflater
    menuInflater.inflate(R.menu.bar_menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (mToggle!!.onOptionsItemSelected(item)) {
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


  companion object CREATOR : Parcelable.Creator<HomeActivity> {
    override fun createFromParcel(parcel: Parcel): HomeActivity {
      return HomeActivity(parcel)
    }

    override fun newArray(size: Int): Array<HomeActivity?> {
      return arrayOfNulls(size)
    }
  }
}
