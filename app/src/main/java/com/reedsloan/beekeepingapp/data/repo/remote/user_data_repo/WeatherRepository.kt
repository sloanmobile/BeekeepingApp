package com.reedsloan.beekeepingapp.data.repo.remote.user_data_repo

import android.app.Application
import android.util.Log
import com.reedsloan.beekeepingapp.R
import com.reedsloan.beekeepingapp.data.remote.WeatherResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository
@Inject constructor(
    private val client: HttpClient,
    app: Application
) {
    private val apiKey = app.getString(R.string.weather_api_key)
    private val baseUrl = "https://api.tomorrow.io/v4"
    private val realtimeWeatherRoute = "/weather/realtime"

    suspend fun getWeatherData(location: String, units: String): Result<WeatherResponse> {
        return runCatching {
            withContext(Dispatchers.IO) {
                val url =
                    "$baseUrl$realtimeWeatherRoute&apikey=$apiKey?location=$location&units=$units"

                return@withContext client.get(url).body<WeatherResponse>()
                    .also<WeatherResponse> { Log.d("WeatherRepository", it.toString()) }
            }
        }
    }
}