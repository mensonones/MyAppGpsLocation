package com.example.myappgpslocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            LocationMapScreen(fusedLocationClient)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun LocationMapScreen(fusedLocationClient: FusedLocationProviderClient) {
    var location by remember { mutableStateOf<Location?>(null) }
    var isUpdating by remember { mutableStateOf(false) }

    val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    val locationRequest = remember {
        LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 5000L // Request location every 5 seconds
        ).build()
    }

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                location = locationResult.lastLocation
                Log.d("LocationMapScreen", "New location received: ${location?.latitude}, ${location?.longitude}")
                isUpdating = true
            }
        }
    }

    LaunchedEffect(locationPermissionState.hasPermission) {
        Log.d("LocationMapScreen", "TESTE 1")
        if (locationPermissionState.hasPermission) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    LaunchedEffect(location) {
        Log.d("LocationMapScreen", "TESTE 2")
        isUpdating = false
    }

    val context = LocalContext.current
    val mapView = rememberMapView(context)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (locationPermissionState.hasPermission) {
            Text(text = "Latitude: ${location?.latitude ?: "Unknown"}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Longitude: ${location?.longitude ?: "Unknown"}")

            Spacer(modifier = Modifier.height(16.dp))
            if (location != null) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AndroidView(
                        factory = { mapView },
                        update = { mapView ->
                            mapView.getMapAsync { googleMap ->
                                googleMap.isMyLocationEnabled = true // Enable the blue dot for location
                                googleMap.uiSettings.isMyLocationButtonEnabled = true // Enable the "My Location" button
                                googleMap.clear() // Clear previous markers
                                val userLocation = LatLng(location!!.latitude, location!!.longitude)
                                googleMap.addMarker(MarkerOptions().position(userLocation).title("You are here"))
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                            }
                        }
                    )
                }
            } else {
                CircularProgressIndicator()  // Visual indicator of ongoing updates
                Text(text = "Atualizando localização...")
            }
        } else {
            if (locationPermissionState.shouldShowRationale) {
                Text(text = "A permissão de localização é necessária para mostrar o mapa.")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                    Text(text = "Request Location Permission")
                }
            } else {
                Text(text = "A permissão de localização foi negada permanentemente. \n Por favor, habilite-a nas configurações do aplicativo.", textAlign = TextAlign.Center )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) {
                    Text(text = "Abrir Configurações")
                }
            }
        }
    }
}

@Composable
fun rememberMapView(context: Context): MapView {
    val mapView = remember { MapView(context) }
    DisposableEffect(context) {
        mapView.onCreate(null)
        mapView.onResume() // Ensure the map view is resumed
        onDispose {
            mapView.onPause() // Pause the map view
            mapView.onDestroy() // Destroy the map view
        }
    }
    return mapView
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    // Preview function
}