package com.example.dataapp2

import retrofit2.http.GET

interface RocketLaunchAPI {

    @GET("launches")
    suspend fun getRocketLaunches(): Array<RocketLaunch>
}