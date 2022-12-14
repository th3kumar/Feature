package com.example.feature


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SeekBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    // handle binding to the player service
    private var playerService: PlayerService? = null






    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {}

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            playerService = (service as PlayerService.PlayerBinder).getService()
            // update the FAB
            if (playerService?.isPlaying() == true) fab.show() else fab.hide()
            playerService?.playerChangeListener = playerChangeListener
        }

    }

    private val playerChangeListener = {
        if (playerService?.isPlaying() == true) fab.show() else fab.hide()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        next_btn.setOnClickListener{
            val intent = Intent(this,SecondActivity::class.java)
            startActivity(intent)

        }

//        val play_rain: Button = findViewById(R.id.play_rain)
//        val rain_volume: SeekBar = findViewById(R.id.rain_volume)
        play_rain.setOnClickListener {
            playerService?.toggleSound(PlayerService.Sound.RAIN)
            toggleProgressBar(rain_volume)
        }
       // val play_storm: Button = findViewById(R.id.play_storm)
       // val storm_volume: SeekBar = findViewById(R.id.storm_volume)
        play_storm.setOnClickListener {
            playerService?.toggleSound(PlayerService.Sound.STORM)
            toggleProgressBar(storm_volume)
        }
       // val play_water: Button = findViewById(R.id.play_water)
        //val water_volume: SeekBar = findViewById(R.id.water_volume)
        play_water.setOnClickListener {
            playerService?.toggleSound(PlayerService.Sound.WATER)
            toggleProgressBar(water_volume)
        }

       // val play_fire: Button = findViewById(R.id.play_fire)
       // val fire_volume: SeekBar = findViewById(R.id.fire_volume)
        play_fire.setOnClickListener {
            playerService?.toggleSound(PlayerService.Sound.FIRE)
            toggleProgressBar(fire_volume)
        }
       // val play_wind: Button = findViewById(R.id.play_wind)
       // val wind_volume: SeekBar = findViewById(R.id.wind_volume)
        play_wind.setOnClickListener {
            playerService?.toggleSound(PlayerService.Sound.WIND)
            toggleProgressBar(wind_volume)
        }
       // val play_night: Button = findViewById(R.id.play_night)
        //val night_volume: SeekBar = findViewById(R.id.night_volume)
        play_night.setOnClickListener {
            playerService?.toggleSound(PlayerService.Sound.NIGHT)
            toggleProgressBar(night_volume)
        }
        //val play_cat: Button = findViewById(R.id.play_cat)
       // val cat_volume: SeekBar = findViewById(R.id.cat_volume)
        play_cat.setOnClickListener {
            playerService?.toggleSound(PlayerService.Sound.PURR)
            toggleProgressBar(cat_volume)
        }

        rain_volume.setOnSeekBarChangeListener(VolumeChangeListener(PlayerService.Sound.RAIN))
        storm_volume.setOnSeekBarChangeListener(VolumeChangeListener(PlayerService.Sound.STORM))
        water_volume.setOnSeekBarChangeListener(VolumeChangeListener(PlayerService.Sound.WATER))
        fire_volume.setOnSeekBarChangeListener(VolumeChangeListener(PlayerService.Sound.FIRE))
        wind_volume.setOnSeekBarChangeListener(VolumeChangeListener(PlayerService.Sound.WIND))
        night_volume.setOnSeekBarChangeListener(VolumeChangeListener(PlayerService.Sound.NIGHT))
        cat_volume.setOnSeekBarChangeListener(VolumeChangeListener(PlayerService.Sound.PURR))

        fab.setOnClickListener {
            playerService?.stopPlaying()
            fab.hide()
            // hide all volume bars
            arrayOf(rain_volume, storm_volume, water_volume, fire_volume, wind_volume, night_volume, cat_volume).forEach { bar ->
                bar.visibility = View.INVISIBLE
            }
        }
    }

    private fun toggleProgressBar(progressBar: ProgressBar) {
        progressBar.visibility = if (progressBar.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
    }

    inner class VolumeChangeListener(private val sound: PlayerService.Sound) : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            playerService?.setVolume(sound, (progress + 1) / 20f)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

    override fun onStart() {
        super.onStart()
        val playerIntent = Intent(this, PlayerService::class.java)
        startService(playerIntent)
        bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        unbindService(serviceConnection)
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        playerService?.stopForeground()
    }

    override fun onPause() {
        playerService?.startForeground()
        super.onPause()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_MIN
            val channel = NotificationChannel("feature", name, importance)

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }



}