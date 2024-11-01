package com.example.dataapp2

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.dataapp2.ui.theme.DataApp2Theme
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Arrays
import kotlin.coroutines.coroutineContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DataApp2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Launches(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Launches(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val buttonClick: () -> Unit = {
        coroutineScope.launch {
            val rocketLaunches = performApiCall()
            println(rocketLaunches.contentToString())

            val json: String = Gson().toJson(rocketLaunches).toString()
            context.getSharedPreferences("rockets", MODE_PRIVATE).edit().apply(){
                putString("launches", json)
                commit()
                println("Saved to Shared Preferences")
            }
        }
    }
    Column(modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Button( buttonClick) {
            Text("Get and save launches")
        }
    }
}

suspend fun performApiCall(): Array<RocketLaunch> {
    val retrofit: Retrofit = Retrofit.Builder().baseUrl("https://api.spacexdata.com/v3/")
        .addConverterFactory(GsonConverterFactory.create()).build()
    val service: RocketLaunchAPI = retrofit.create(RocketLaunchAPI::class.java)
    return service.getRocketLaunches()
}


data class RocketLaunch(
    val flight_number: Int,
    val mission_name: String,
    val launch_date_utc: String,
    val details: String?,
    val launch_success: Boolean?,
)