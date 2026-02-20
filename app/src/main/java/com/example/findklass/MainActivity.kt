package com.example.findklass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val singapore = LatLng(1.35, 103.87)

            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(singapore, 10f)
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            )
        }
    }
}