package com.example.tiendaonline.presentation.contacto

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.tiendaonline.R
import com.example.tiendaonline.databinding.ActivityContactoBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class Contacto : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var binding: ActivityContactoBinding
    private var locationOverlay: MyLocationNewOverlay? = null

    private val ubicacionTienda = GeoPoint(4.60971, -74.08175)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        binding = ActivityContactoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        val mapController = map.controller
        mapController.setZoom(15.0)
        mapController.setCenter(ubicacionTienda)

        val marker = Marker(map)
        marker.position = ubicacionTienda
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Tienda Online Central"
        marker.snippet = "¡Visítanos aquí!"
        map.overlays.add(marker)

        activarUbicacionUsuario()
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
        locationOverlay?.enableMyLocation()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
        locationOverlay?.disableMyLocation()
    }

    @SuppressLint("MissingPermission")
    private fun activarUbicacionUsuario() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            if (locationOverlay == null) {
                val provider = GpsMyLocationProvider(applicationContext)
                provider.addLocationSource(android.location.LocationManager.NETWORK_PROVIDER)

                locationOverlay = MyLocationNewOverlay(provider, map)
                locationOverlay?.enableMyLocation()

                locationOverlay?.enableFollowLocation() 

                locationOverlay?.runOnFirstFix {
                    runOnUiThread {
                        map.controller.animateTo(locationOverlay?.myLocation)
                        map.controller.setZoom(18.0)
                        Toast.makeText(this, "Ubicandote...", Toast.LENGTH_SHORT).show()
                    }
                }

                map.overlays.add(locationOverlay)
            }
            
            map.invalidate()
            
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                activarUbicacionUsuario()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
}