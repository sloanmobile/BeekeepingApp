package com.reedsloan.beekeepingapp.data.repo.remote.user_data_repo

import android.app.Application
import android.util.Log
import com.reedsloan.beekeepingapp.R
import com.reedsloan.beekeepingapp.data.remote.WeatherResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository
@Inject constructor(
    private val client: HttpClient,
    app: Application
) {
    private val apiKey = app.getString(R.string.weather_api_key)
    private val baseUrl = "http://api.weatherapi.com/v1/current.json"

    suspend fun getWeatherData(location: String): Result<WeatherResponse> {
        return runCatching {
            withContext(Dispatchers.IO) {
                val url = "$baseUrl?key=$apiKey&q=$location"
                val response: HttpResponse = client.get(url)
                if (response.status.value == 200) {
                    Log.d(this::class.simpleName, "response: ${response.bodyAsText()}")
                    Json.decodeFromString(WeatherResponse.serializer(), response.bodyAsText())
                } else {
                    throw Exception("Error getting weather data, status code: ${response.bodyAsText()}")
                }
            }
        }
    }
}