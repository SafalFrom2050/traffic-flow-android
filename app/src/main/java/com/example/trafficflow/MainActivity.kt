package com.example.trafficflow

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.trafficflow.auth.Model.User
import com.example.trafficflow.auth.Repository.UserRepository
import com.example.trafficflow.auth.View.AuthActivity
import com.example.trafficflow.databinding.ActivityMainBinding
import com.example.trafficflow.ui.base.ViewPagerNoSwipe
import com.example.trafficflow.ui.bottomsheets.View.ReportBottomSheetFragment
import com.example.trafficflow.ui.main.SectionsPagerAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigationProvider
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PermissionManager(this).askPermissions()
        checkAuth()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPagerNoSwipe = binding.viewPager

        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs

        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = binding.fab


        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        initMapbox()
        binding.iconMenu.setOnClickListener{
            it.isSelected = true
            showAccountDialog(it)
        }

        binding.fab.setOnClickListener {
            showReportPage()
        }
    }

    private fun checkAuth() {
        val accessToken = RetrofitInstance.retrieveAccessToken(this)

        Log.d("MainActivity", "Access Token: $accessToken")
        val userRepository = UserRepository()
        if (accessToken != ""){
            if (RetrofitInstance.currentUser.id == null){
                lifecycleScope.launch {
                    val result = userRepository.getCurrentUser(this@MainActivity)
                    if (!result) redirectAuthActivity()
                }
            }
        }else{
            redirectAuthActivity()
        }
    }

    private fun redirectAuthActivity() {
        val i = Intent(this, AuthActivity::class.java)
        startActivity(i)
        finish()
    }

    private fun showAccountDialog(profileBtn: View){
        val user: User = RetrofitInstance.currentUser
        val dialog = BottomSheetDialog(this, R.style.DialogTheme)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_account, null)

        dialog.setContentView(view)
        dialog.setOnDismissListener {
            profileBtn.isSelected = false
        }
        dialog.show()
        val profileImage: ImageView? = dialog.findViewById<ImageView>(R.id.imageProfileLarge)
        val y = Glide.with(this).load(RetrofitInstance.BASE_URL + user.profileImage)
            .apply(RequestOptions.circleCropTransform()).into(profileImage!!)

        dialog.findViewById<TextView>(R.id.labelName)?.text = "${user.fname} ${user.lname}"
        dialog.findViewById<TextView>(R.id.labelRewards)?.text = user.rewardPoints.toString()

        dialog.findViewById<View>(R.id.menuAchievement)?.setOnClickListener {
            val i = Intent(this, AchievementsActivity::class.java)
            startActivity(i)
        }

        dialog.findViewById<View>(R.id.menuEditProfile)?.setOnClickListener{
            val i = Intent(this, EditProfileActivity::class.java)
            startActivity(i)
        }
    }

    private fun showReportPage() {
        val reportBottomSheetFragment = ReportBottomSheetFragment()
        reportBottomSheetFragment.show(supportFragmentManager, reportBottomSheetFragment.tag)
    }



    private fun initMapbox() {
        val navigationOptions = NavigationOptions.Builder(this)
            .accessToken(resources.getString(R.string.mapbox_access_token))
            .build()
        val mapboxNavigation = MapboxNavigationProvider.create(navigationOptions)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        PermissionManager(this@MainActivity).handlePermissionResponse(requestCode, grantResults)
    }
}