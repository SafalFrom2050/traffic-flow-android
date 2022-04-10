package com.example.trafficflow

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    private fun showAccountDialog(profileBtn: View){
        val dialog = BottomSheetDialog(this, R.style.DialogTheme)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_account, null)

        dialog.setContentView(view)
        dialog.setOnDismissListener {
            profileBtn.isSelected = false
        }
        dialog.show()
        val profileImage: ImageView? = dialog.findViewById<ImageView>(R.id.imageProfileLarge)
        val y = Glide.with(this).load("https://images.pexels.com/photos/1081685/pexels-photo-1081685.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1")
            .apply(RequestOptions.circleCropTransform()).into(profileImage!!)

        dialog.findViewById<View>(R.id.menuAchievement)?.setOnClickListener {
            val i = Intent(this, AchievementsActivity::class.java)
            startActivity(i)
        }
    }

    private fun showReportPage() {
//        val dialog = BottomSheetDialog(this, R.style.DialogTheme)
//        val view = layoutInflater.inflate(R.layout.bottom_sheet_report, null)
//        dialog.setContentView(view)
//        dialog.show()
//
//        setUpReportPageRV(dialog)

        val reportBottomSheetFragment = ReportBottomSheetFragment()
        reportBottomSheetFragment.show(supportFragmentManager, reportBottomSheetFragment.tag)
    }



    fun initMapbox() {
        val navigationOptions = NavigationOptions.Builder(this)
            .accessToken(resources.getString(R.string.mapbox_access_token))
            .build()
        val mapboxNavigation = MapboxNavigationProvider.create(navigationOptions)
    }
}