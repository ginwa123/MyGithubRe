package com.m.ginwa.dicoding.mygithubre.ui


import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.AppBarLayout
import com.m.ginwa.dicoding.mygithubre.R
import com.m.ginwa.dicoding.mygithubre.receiver.AlarmReceiver
import com.m.ginwa.dicoding.mygithubre.receiver.CALENDAR_HOUR_OF_DAY
import com.m.ginwa.dicoding.mygithubre.receiver.CALENDAR_MINUTE
import com.m.ginwa.dicoding.mygithubre.ui.settings.REMINDER_SETTING
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import javax.inject.Inject
import kotlin.math.abs


@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private var isAppBarExpanded = false
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private val activityViewModel: ActivityViewModel by viewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setInitReminder()
        setSupportActionBar(toolbar)
        setNav()
        setToolbarListener()
        setFabListener()
    }

    private fun setInitReminder() {
        val reminderSetting = sharedPreferences.getBoolean(REMINDER_SETTING, false)
        if (reminderSetting) {
            // set alarm if reminder setting is true
            val calendarHour = sharedPreferences.getInt(CALENDAR_HOUR_OF_DAY, 0)
            val calendarMinute = sharedPreferences.getInt(CALENDAR_MINUTE, 0)
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, calendarHour)
            calendar.set(Calendar.MINUTE, calendarMinute)
            val alarmReceiver =
                AlarmReceiver()
            alarmReceiver.setRepeatingAlarm(this, calendar)
        }
    }

    private fun setToolbarListener() {
        activityViewModel.progressBarLive.observe(this, Observer { bool ->
            if (bool != null && bool) {
                progressBar.visibility = View.VISIBLE
                return@Observer
            }
            progressBar.visibility = View.GONE
        })

        activityViewModel.imageToolbarListener.observe(this, Observer {
            if (it != null && navController.currentDestination?.id == R.id.detailUserFragment) {
                Glide.with(this)
                    .load(it)
                    .addListener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            showToast(this@MainActivity, e.toString())
                            activityViewModel.imageToolbarListener.value = null
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }
                    })
                    .into(imageUser)
                return@Observer
            }
            imageUser.setImageDrawable(null)
        })

        activityViewModel.toolbarListener.observe(this, Observer {
            if (navController.currentDestination?.id == R.id.detailUserFragment) {
                if (it != null && it) {
                    appBarLayout.setExpanded(true, true)
                    if (activityViewModel.fabIconListener.value != null) fabFavorite.show()
                    return@Observer
                }
                appBarLayout.setExpanded(false, true)
                fabFavorite.hide()
            }
        })

        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) == appBarLayout.totalScrollRange) {
                // Collapsed
                isAppBarExpanded = false
            } else if (verticalOffset == 0) {
                // Expanded
                isAppBarExpanded = true
            }
        })
    }

    private fun setFabListener() {
        fabFavorite.setOnClickListener {
            val user = activityViewModel.fabIconListener.value
            if (user?.isFavorite == true) {
                user.isFavorite = false
                fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_48)
            } else {
                user?.isFavorite = true
                fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_white_24)
            }
            activityViewModel.insertUser(user)
        }
        activityViewModel.fabIconListener.observe(this, Observer { user ->
            // handle fab favorite icon
            user?.let {
                fabFavorite.show()
                if (it.isFavorite) {
                    fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_white_24)
                    return@Observer
                }
                fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_48)
                return@Observer
            }
            fabFavorite.hide()
        })
    }

    private fun setNav() {
        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        collapsingToolbar.setupWithNavController(toolbar, navController, appBarConfiguration)


        navController.addOnDestinationChangedListener { _, destination, _ ->
            destination.id.let {
                if (it == R.id.detailUserFragment && activityViewModel.toolbarListener.value == true) {
                    appBarLayout.setExpanded(true, true)
                } else {
                    if (isAppBarExpanded) appBarLayout.setExpanded(false, true)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    companion object {
        fun showToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}