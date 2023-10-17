package com.sunnyweather.android.logic.network

import com.sunnyweather.android.logic.model.DailyResponse
import com.sunnyweather.android.logic.model.PlaceResponse
import com.sunnyweather.android.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object SunnyWeatherNetwork {
    private val placeService = ServiceCreator.create(PlaceService::class.java)
    private val weatherService = ServiceCreator.create(WeatherService::class.java)

    suspend fun getDailyWeather(lng: String, lat: String): DailyResponse {
        val token = "VJ2IhXG42lWj0Rmv" // 替换为你的实际令牌
        return weatherService.getDailyWeather(token, lng, lat).await()
    }

    suspend fun getRealtimeWeather(lng: String, lat: String): RealtimeResponse {
        val token = "VJ2IhXG42lWj0Rmv" // 替换为你的实际令牌
        return weatherService.getRealtimeWeather(token, lng, lat).await()
    }



    suspend fun searchPlaces(query: String): PlaceResponse {
        return placeService.searchPlaces(query).await()
    }

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        continuation.resume(body)
                    } else {
                        continuation.resumeWithException(
                            RuntimeException("response body is null or request failed")
                        )
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}
