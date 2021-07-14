package com.geektech.weatherapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.geektech.weatherapp.R
import com.geektech.weatherapp.base.BaseActivity
import com.geektech.weatherapp.databinding.ActivityMainBinding
import com.geektech.weatherapp.extensions.convertDate
import com.geektech.weatherapp.extensions.toast
import com.geektech.weatherapp.model.MainWeather
import com.geektech.weatherapp.network.Resource
import com.geektech.weatherapp.utils.ProgrDialog
import com.geektech.weatherapp.utils.Status
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException
import java.util.*

class MainActivity : BaseActivity<ActivityMainBinding>(), LocationListener {
    override fun viewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    private var geoCoder: Geocoder? = null
    private var locationManager: LocationManager? = null
    private val dialog by lazy { ProgrDialog(this) }
    private val viewModel: MainViewModel by viewModel()

    @RequiresApi(Build.VERSION_CODES.P)
    override fun uiFunc() {

        vb.imageGetLocation.setOnClickListener {
            if (permissionsAllowed()) {
                getLocation()
                geoCoder = Geocoder(this, Locale.getDefault())
            } else {
                requestPermissionWithRationale()
            }
        }

        vb.tvGetLocation.setOnClickListener {
            if (permissionsAllowed()) {
                getLocation()
                geoCoder = Geocoder(this, Locale.getDefault())
            } else {
                // app doesn't have permissions, So i m requesting permissions.
                requestPermissionWithRationale()
            }
        }
        vb.imageSearchCity.setOnClickListener {
            liveData()
            vb.tvGetLocation.text = vb.etSearch.text.toString()
            hideKeyboard()
        }
    }

    override fun liveData() {
        viewModel.loading.observe(this, { })
        if (vb.etSearch.text.toString().isNotEmpty()) {
            getWeather(vb.etSearch.text.toString())
        }
    }

    private fun getWeather(city: String?) {
        if (city != null) {
            viewModel.getWeather(city, "metric").observe(this, { response ->
                when (response.status) {
                    Status.SUCCESS -> {
                        viewModel.loading.postValue(false)
                        vb.tvWeatherMain.text = response?.data?.weather?.get(0)?.main
                        vb.tvTemp.text = (response.data?.main?.temp.toString() + "°C")
                        vb.tvTime.text =
                            ("${getString(R.string.data_for)} " + response.data?.dt?.convertDate())
                        // visibility data in km/h
                        vb.tvVisibility.text = ("${getString(R.string.visibilty)} " +
                                response?.data?.visibility?.div(1000).toString() + " ${getString(R.string.kmh)}")
                        // dividing by 1000 to show pressure in millibar
                        vb.tvPressure.text = ("${getString(R.string.pressure)} " +
                                (response?.data?.main?.pressure?.div(1000)).toString() + " ${getString(R.string.millibar)}")
                        vb.tvFeelsLike.text =
                            ("${getString(R.string.feels_like)} " + response?.data?.main?.feels_like.toString() + "°C")
                        vb.tvTempMax.text =
                            ("${getString(R.string.max_temp)} " + response?.data?.main?.temp_max.toString() + "°C")
                        vb.tvTempMin.text =
                            ("${getString(R.string.temp_min)} " + response?.data?.main?.temp_min.toString() + "°C")
                        vb.tvHumidity.text =
                            ("${getString(R.string.humidity)} " + response?.data?.main?.humidity.toString() + "%")
                        vb.tvWindSpeed.text = ("${getString(R.string.wind_speed)} " +
                                response?.data?.wind?.speed.toString() + " ${getString(R.string.kmh)}")
                        vb.tvSunrise.text =
                            ("${getString(R.string.sunrise)} " + response?.data?.sys?.sunrise?.convertDate())
                        vb.tvSunset.text =
                            ("${getString(R.string.sunset)} " + response?.data?.sys?.sunset?.convertDate())

                        weatherIcons(response)

                    }
                    Status.ERROR -> toast(response.message + "  ${response.code}")

                    Status.LOADING -> viewModel.loading.postValue(false)
                }
            })
        }
    }

    companion object {
        private const val RC_GPS_PERMISSION = 1
    }

    private fun endGPS() {
        try {
            locationManager!!.removeUpdates(this)
            locationManager = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun getLocation() {
        try {
            dialog.show()
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 5f, this)
            if (!locationManager!!.isLocationEnabled) {
                toast(getString(R.string.pls_enable_gps_perms))
            }
        } catch (e: SecurityException) {
            toast(e.message.toString())
            dialog.dismiss()
        }
    }

    override fun onLocationChanged(location: Location) {
        try {
            val addresses = geoCoder!!.getFromLocation(location.latitude,
                location.longitude,
                1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            val address =
                addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            val city = addresses[0].locality
            val state = addresses[0].adminArea
            val country = addresses[0].countryName
            val postalCode = addresses[0].postalCode
            val knownName = addresses[0].featureName

            if (city != null) {
                vb.etSearch.setText(city)
                vb.tvGetLocation.text = city
                getWeather(city)
            } else {
                vb.etSearch.setText(address)
                vb.tvGetLocation.text = address
                getWeather(address)
            }
            dialog.dismiss()
        } catch (e: IOException) {
            toast(e.message.toString())
            dialog.dismiss()
        }
    }

    private fun permissionsAllowed(): Boolean {
        var res: Int
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        for (perms in permissions) {
            res = checkCallingOrSelfPermission(perms)
            if (res != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun requestPermissionWithRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            val message = getString(R.string.snackbar_gps_message)
            Snackbar.make(vb.root, message, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.grant)) { requestPerms() }
                .show()
        } else {
            requestPerms()
        }
    }

    private fun requestPerms() {
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, RC_GPS_PERMISSION)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var allowed = true
        if (requestCode == RC_GPS_PERMISSION) {
            for (res in grantResults) {
                // if user granted all permissions.
                allowed = allowed && res == PackageManager.PERMISSION_GRANTED
            }
        } else {
            allowed = false
        }
        if (allowed) {
            getLocation()
            geoCoder = Geocoder(this, Locale.getDefault())
        } else {
            // giving warning to user that user haven't granted permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    toast(getString(R.string.gps_perms_denied))
                } else {
                    showGPSPermissionSnackBar()
                }
            }
        }
    }

    private fun showGPSPermissionSnackBar() {
        Snackbar.make(vb.root, getString(R.string.gps_perm_s_not_allowed), Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.settings)) {
                openApplicationSettings()
                toast(getString(R.string.open_n_grant_perms))
            }.show()
    }

    private fun openApplicationSettings() {
        val appSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:$packageName"))
        startActivityForResult(appSettingsIntent, RC_GPS_PERMISSION)
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GPS_PERMISSION) {
            getLocation()
            geoCoder = Geocoder(this, Locale.getDefault())
            return
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onStart() {
        super.onStart()
        if (permissionsAllowed()) {
            getLocation()
            geoCoder = Geocoder(this, Locale.getDefault())
        } else {
            requestPermissionWithRationale()
        }
    }

    override fun onStop() {
        super.onStop()
        endGPS()
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        var view = currentFocus
        if (view == null) view = View(this)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun weatherIcons(resource: Resource<MainWeather?>) {
        //working on it, obeshshaiu pridumaiu luchsheie reshenie
        vb.lottieIcons.speed = 2.0f
        vb.lottieIcons.progress = 1.0f
        vb.lottieIcons.repeatCount = Animation.INFINITE

        when (resource.data?.weather?.get(0)?.icon) {
            "01d" -> vb.lottieIcons.setAnimation(R.raw.sunny)
            "01n" -> vb.lottieIcons.setAnimation(R.raw.clear_night)
            "02d" -> vb.lottieIcons.setAnimation(R.raw.few_cloud)
            "02n" -> vb.lottieIcons.setAnimation(R.raw.cloudynight)
            "03d" -> vb.lottieIcons.setAnimation(R.raw.scattered_clouds_origin)
            "03n" -> vb.lottieIcons.setAnimation(R.raw.scattered_clouds_origin)
            "04d" -> vb.lottieIcons.setAnimation(R.raw.cloudy_orirgin)
            "04n" -> vb.lottieIcons.setAnimation(R.raw.cloudy_orirgin)
            "09d" -> vb.lottieIcons.setAnimation(R.raw.shower_rain)
            "09n" -> vb.lottieIcons.setAnimation(R.raw.shower_rain)
            "10d" -> vb.lottieIcons.setAnimation(R.raw.rain)
            "10n" -> vb.lottieIcons.setAnimation(R.raw.rainynight)
            "11d" -> vb.lottieIcons.setAnimation(R.raw.thunderstorm)
            "11n" -> vb.lottieIcons.setAnimation(R.raw.thunderstorm)
            "13d" -> vb.lottieIcons.setAnimation(R.raw.snow)
            "13n" -> vb.lottieIcons.setAnimation(R.raw.snownight)
            "50d" -> vb.lottieIcons.setAnimation(R.raw.mist)
        }
    }
}