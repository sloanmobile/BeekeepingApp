package com.reedsloan.beekeepingapp.presentation.hives_screen

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.icu.text.DateFormat
import android.net.Uri
import android.os.Build
import android.system.ErrnoException
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider.getUriForFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.reedsloan.beekeepingapp.R
import com.reedsloan.beekeepingapp.data.TimeFormat
import com.reedsloan.beekeepingapp.data.UserPreferences
import com.reedsloan.beekeepingapp.data.local.TemperatureMeasurement
import com.reedsloan.beekeepingapp.data.local.UserData
import com.reedsloan.beekeepingapp.data.local.hive.*
import com.reedsloan.beekeepingapp.data.repo.remote.user_data_repo.WeatherRepository
import com.reedsloan.beekeepingapp.domain.repo.LocalUserDataRepository
import com.reedsloan.beekeepingapp.domain.repo.UserDataRepository
import com.reedsloan.beekeepingapp.presentation.HiveScreenState
import com.reedsloan.beekeepingapp.presentation.common.MenuState
import com.reedsloan.beekeepingapp.presentation.common.Screen
import com.reedsloan.beekeepingapp.presentation.common.data.PermissionRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HiveViewModel @Inject constructor(
    private val app: Application,
    private val localUserDataRepository: LocalUserDataRepository,
    private val remoteUserDataRepository: UserDataRepository,
    private val weatherRepository: WeatherRepository,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : ViewModel() {
    private val _state = MutableStateFlow(HiveScreenState())
    val state = _state.asStateFlow()

    private val _hives = MutableStateFlow<List<Hive>>(emptyList())
    val hives = _hives.asStateFlow()

    val visiblePermissionDialogQueue = mutableStateListOf<PermissionRequest>()

    init {
        viewModelScope.launch {
            getUserPreferences()
            if (!doesUserHavePermission(Manifest.permission.POST_NOTIFICATIONS)) {
                visiblePermissionDialogQueue.add(PermissionRequest.NotificationPermissionRequest)
            }
        }
    }

    private fun doesUserHavePermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            app, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isPermissionPermanentlyDeclined(
        activity: Activity, permission: String
    ): Boolean {
        return !ActivityCompat.shouldShowRequestPermissionRationale(
            activity, permission
        ) && !isPermissionRequestFirstTime(permission)
    }

    /**
     * Saves the user data to the remote database.
     * This should be used when the user adds, updates, or deletes a hive.
     */
    private fun saveUserDataToRemote() {
        viewModelScope.launch {
            runCatching {
                _state.update {
                    state.value.copy(
                        userData = state.value.userData.copy(
                            hives = hives.value, lastUpdated = System.currentTimeMillis()
                        )
                    )
                }
                remoteUserDataRepository.updateUserData(state.value.userData)
            }.onSuccess {
                showSuccess()
            }.onFailure {
                val error = it.message ?: "Unknown error updating user data."
                showError(error)
                Toast.makeText(app, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun getUserDataFromLocal() {
        _state.update {
            it.copy(
                isLoading = true,
                error = false,
            )
        }
        localUserDataRepository.getUserData().onSuccess {
            Log.d(this::class.simpleName, "getUserDataFromLocal: $it")
            updateUserData(it)
            showSuccess()
        }.onFailure {
            showError(it.message ?: "Unknown error")
        }
    }

    private fun isPermissionRequestFirstTime(permission: String): Boolean {
        return !state.value.userPreferences.requestedPermissions.contains(permission)
    }

    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    /**
     * On permission result, remove the permission from the queue if it was granted.
     * If it was denied, the open app settings dialog will be shown.
     */
    fun onPermissionResult(permission: String, granted: Boolean) {
        // add the permission requested to the user preferences
        // so the permanently denied dialog will be shown if needed
        // only add one of each permission
        if (!state.value.userPreferences.requestedPermissions.contains(permission)) {
            updateUserPreferences(
                state.value.userPreferences.copy(
                    requestedPermissions = state.value.userPreferences.requestedPermissions + permission
                )
            )
        }

        if (granted) {
            dismissDialog()
        } else {
            dismissDialog()
            getPermissionRequest(permission = permission)?.let { visiblePermissionDialogQueue.add(it) }
        }
    }

    fun onPermissionRequested(permission: String) {
        getPermissionRequest(permission)?.let { visiblePermissionDialogQueue.add(it) }
    }

    private fun getPermissionRequest(permission: String): PermissionRequest? {
        return when (permission) {
            android.Manifest.permission.CAMERA -> {
                PermissionRequest.CameraPermissionRequest
            }

            android.Manifest.permission.READ_MEDIA_IMAGES -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    PermissionRequest.StoragePermissionRequestAPI33
                } else {
                    return null
                }
            }

            android.Manifest.permission.ACCESS_NOTIFICATION_POLICY -> {
                PermissionRequest.NotificationPermissionRequest
            }

            else -> {
                throw IllegalArgumentException(
                    "Unknown permission: $permission, please add it to the getPermissionRequest function."
                )
            }
        }

    }

    fun getHourMinuteString(date: LocalDateTime): String {
        // adjust to user preferences of 24 hour or 12 hour
        return when (state.value.userPreferences.timeFormat) {
            TimeFormat.TWENTY_FOUR_HOUR -> {
                date.format(DateTimeFormatter.ofPattern("HH:mm"))
            }

            TimeFormat.TWELVE_HOUR -> {
                date.format(DateTimeFormatter.ofPattern("hh:mm a"))
            }
        }
    }

    fun getHourString(date: LocalDateTime): String {
        return when (state.value.userPreferences.timeFormat) {
            TimeFormat.TWENTY_FOUR_HOUR -> {
                date.format(DateTimeFormatter.ofPattern("HH"))
            }

            TimeFormat.TWELVE_HOUR -> {
                date.format(DateTimeFormatter.ofPattern("h"))
            }
        }
    }


    private suspend fun getUserPreferences() {
        localUserDataRepository.getUserData().onSuccess {
            _state.update {
                it.copy(
                    userPreferences = it.userPreferences
                )
            }
        }
    }

    /**
     * Use this function to handle the back button press instead of using popBackStack() directly.
     */
    fun backHandler(navController: NavController) {
        if (state.value.hiveDeleteMode) {
            toggleHiveDeleteMode()
            return
        } else if (state.value.editingTextField) {
            toggleEditingTextField()
            return
        } else if (state.value.showDeleteHiveDialog) {
            closeOpenMenus()
            return
        }

        // pop backstack if there is a previous screen
        if (navController.previousBackStackEntry != null) {
            navController.popBackStack()
        }
    }

    private fun toggleEditingTextField() {
        _state.update { it.copy(editingTextField = !it.editingTextField) }
    }

    fun onTapAddHiveButton() {
        // make a toast notification
        Toast.makeText(app, "New hive created.", Toast.LENGTH_SHORT).show()
        closeOpenMenus()
        addHive()
    }


    fun onTapNavigationExpandButton() {
        toggleNavigationBarMenuState()
    }

    fun onTapDeleteSelectedHiveButton() {
        viewModelScope.launch {
            deleteSelectedHives()
            toggleHiveDeleteMode()
        }
    }

    private fun showDeleteHiveDialog(selectedHive: String) {
        setSelectedHive(selectedHive)
        // show confirmation dialog
        _state.update { it.copy(showDeleteHiveDialog = true) }
    }

    fun dismissDeleteHiveDialog() {
        _state.update {
            it.copy(showDeleteHiveDialog = false)
        }
    }

    fun onTapDeleteHiveConfirmationButton(selectedHiveId: String) {
        viewModelScope.launch {
            deleteHive(selectedHiveId)
            closeOpenMenus()
        }
    }

    private fun addToSelectionList(hiveId: String) {
        _state.update { it.copy(selectionList = it.selectionList + hiveId) }
    }

    private fun removeFromSelectionList(hiveId: String) {
        _state.update { it.copy(selectionList = it.selectionList - hiveId) }
    }

    private fun clearSelectionList() {
        _state.update { it.copy(selectionList = emptyList()) }
    }

    private fun toggleHiveDeleteMode() {
        _state.value = state.value.copy(hiveDeleteMode = !state.value.hiveDeleteMode)
    }

    /**
     * Closes all open menus and clears the selection list.
     */
    private fun closeOpenMenus() {
        _state.update {
            state.value.copy(
                navigationBarMenuState = MenuState.CLOSED,
                hiveDeleteMode = false,
                showExtraButtons = false,
                showDeleteHiveDialog = false,
                selectionList = emptyList()
            )
        }
        clearSelectionList()
    }

    private fun deleteSelectedHives() {
        // delete all hives in selection list
        state.value.selectionList.forEach { deleteHive(it) }
        clearSelectionList()
    }

    private fun updateUserPreferences(userPreferences: UserPreferences) {
        _state.update {
            it.copy(
                userPreferences = userPreferences
            )
        }
        syncUserData()
    }

    private suspend fun saveUserDataToLocal() {
        localUserDataRepository.updateUserData(state.value.userData)
    }

    /**
     * Sets the user's preferred temperature unit based on the [TemperatureMeasurement.displayValue]
     * (e.g. "Fahrenheit")
     *
     * @param [TemperatureMeasurement.displayValue] The display value of the temperature unit.
     */
    fun setTemperatureMeasurement(string: String) {
        val temperatureMeasurement =
            TemperatureMeasurement.values().find { it.displayValue == string }
        runCatching { temperatureMeasurement!! }.onSuccess {
            updateUserPreferences(state.value.userPreferences.copy(temperatureMeasurement = it))
        }.onFailure {
            Toast.makeText(
                app, "Error updating temperature unit measurement", Toast.LENGTH_SHORT
            ).show()
            // log error
            Log.e(
                "HiveViewModel",
                "Error updating temperature unit measurement ${it.stackTraceToString()}"
            )
        }
    }

    /**
     * Sets the user's preferred time format based on the [TimeFormat.displayValue]
     * (e.g. "12-hour", "24-hour")
     * @param [TimeFormat.displayValue] The display value of the time format.
     * @see [TimeFormat]
     */
    fun setTimeFormat(string: String) {
        val timeFormat = TimeFormat.values().find { it.displayValue == string }
        runCatching { timeFormat!! }.onSuccess {
            updateUserPreferences(state.value.userPreferences.copy(timeFormat = it))
        }.onFailure {
            Toast.makeText(
                app, "Error updating time format", Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Hides the keyboard. This can be used when the user taps outside of the keyboard or
     * when the user taps the back button.
     * @param [Context] The context of the current activity.
     */
    fun hideKeyboard(context: Context) {
        // hide the keyboard
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputMethodManager.hideSoftInputFromWindow(
            (context as Activity).currentFocus?.windowToken, 0
        )
    }

    fun setSelectedHive(hiveId: String) {
        // Set the selected hive in the state by finding the hive with the matching id
        _state.update { hiveScreenState ->
            hiveScreenState.copy(selectedHive = hives.value.find { it.id == hiveId })
        }
    }

    private fun toggleNavigationBarMenuState() {
        _state.update {
            it.copy(navigationBarMenuState = it.navigationBarMenuState.toggle())
        }
    }

    private fun setIsLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }

    private fun showSuccess() {
        _state.update { it.copy(isSuccess = true, isLoading = false) }
        setIsLoading(false)
    }

    private fun showError(error: String) {
        _state.update {
            Toast.makeText(app, error, Toast.LENGTH_SHORT).show()
            it.copy(
                error = true, errorMessage = error, isLoading = false, isSuccess = false
            )
        }
    }

    fun dateMillisToDateString(dateMillis: String, longFormat: Boolean = false): String {
        val format = DateFormat.getDateInstance(DateFormat.LONG)
        return (format.format(dateMillis.toLong()) + if (longFormat) " at " + DateFormat.getTimeInstance(
            DateFormat.SHORT
        ).format(dateMillis.toLong()) else "")
    }

    fun getTemperatureValue(temperatureCelsius: Double): Double {
        return when (state.value.userPreferences.temperatureMeasurement) {
            TemperatureMeasurement.CELSIUS -> temperatureCelsius
            TemperatureMeasurement.FAHRENHEIT -> temperatureCelsius * 9 / 5 + 32
        }
    }

    private fun addHive(
        hive: Hive = Hive(
            id = UUID.randomUUID().toString(), HiveInfo(
                name = "Hive ${hives.value.size + 1}",
            ), displayOrder = hives.value.size + 1
        )
    ) {
        updateUserData(state.value.userData.copy(hives = hives.value + hive))
    }

    private fun updateHive(hive: Hive) {
        updateUserData(state.value.userData.copy(hives = hives.value.map {
            if (it.id == hive.id) {
                hive
            } else {
                it
            }
        }))
    }

    private fun updateSelectedHive(hive: Hive) {
        _state.update {
            it.copy(
                selectedHive = hive
            )
        }
    }

    private fun deleteHive(hiveId: String) {
        runCatching {
            setIsLoading(true)
            _hives.update {
                it.filter { h ->
                    h.id != hiveId
                }
            }
            _state.update {
                it.copy(
                    userData = state.value.userData.copy(
                        hives = hives.value
                    )
                )
            }
            deselectHive()
        }.onSuccess {
            showSuccess()
            syncUserData()
        }.onFailure {
            showError(it.message ?: "Unknown error")
            // Show error message
            Toast.makeText(app, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * **Use with caution.** This will delete all hives from the database.
     */
    private fun deleteAllHives() {
        runCatching {
            setIsLoading(true)
            _hives.update { emptyList() }
            _state.update {
                it.copy(
                    userData = state.value.userData.copy(
                        hives = hives.value
                    )
                )
            }
            deselectHive()
        }.onSuccess {
            showSuccess()
        }.onFailure {
            showError(it.message ?: "Unknown error")
            // Show error message
            Toast.makeText(app, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun exportToCsv() {
        viewModelScope.launch {
            setIsLoading(true)
            runCatching {
                localUserDataRepository.exportToCsv(state.value.selectedHive!!)
            }.onSuccess {
                Toast.makeText(
                    app, "CSV file(s) saved to the downloads folder.", Toast.LENGTH_SHORT
                ).show()
            }.onFailure {
                Log.e(this::class.simpleName, "Error exporting to CSV: ${it.stackTraceToString()}")
                Log.e(this::class.simpleName, "${it.cause}")
                when (it.cause) {
                    is ErrnoException -> {
                        Toast.makeText(
                            app, "Please empty your devices trash.", Toast.LENGTH_LONG
                        ).show()
                    }

                    else -> {
                        Toast.makeText(
                            app, "Error exporting to CSV", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    fun onEvent(event: HiveScreenEvent) {
        when (event) {

            is HiveScreenEvent.OnUpdateUserData -> {
                updateUserData(event.userData)
            }

            is HiveScreenEvent.OnNavigateToHiveDetailsScreen -> {
                viewModelScope.launch {
                    getUserDataFromLocal()
                    setSelectedHive(event.hiveId)
                }
            }

            is HiveScreenEvent.OnNavigateToHivesScreen -> {
                viewModelScope.launch {
                    getUserDataFromLocal()
                }
            }

            is HiveScreenEvent.OnNavigateToHiveInspectionScreen -> {
                viewModelScope.launch {
                    // Request the location permission if it is not granted
                    if (ActivityCompat.checkSelfPermission(
                            app.applicationContext,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            app.applicationContext,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        visiblePermissionDialogQueue.add(PermissionRequest.LocationPermissionRequest)
                    }
                    getUserDataFromLocal()
                    setSelectedHive(event.hiveId)
                }
            }

            is HiveScreenEvent.OnCreateNewInspection -> {
                createInspection()
                // make a toast notification
                Toast.makeText(app, "New inspection created.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getWeatherData() {
        runCatching {
            if (ActivityCompat.checkSelfPermission(
                    app.applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    app.applicationContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(this::class.simpleName, "Location permission not granted.")
                return
            }
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                viewModelScope.launch {
                    // Latitude and Longitude (Decimal degree) e.g: q=48.8567,2.3508
                    val locationString = "${location.latitude},${location.longitude}"
                    val temperatureUnit =
                        if (state
                                .value
                                .userPreferences
                                .temperatureMeasurement == TemperatureMeasurement.CELSIUS
                        ) {
                            "metric"
                        } else {
                            "imperial"
                        }

                    weatherRepository.getWeatherData(locationString, temperatureUnit)
                        .onSuccess { weatherResponse ->
                            Log.d(
                                this::class.simpleName,
                                "Weather data: $weatherResponse"
                            )

                            _state.update {
                                it.copy(
                                    weatherResponse = weatherResponse,
                                    selectedHiveInspection = state.value.selectedHiveInspection!!.copy(
                                        hiveConditions = state.value.selectedHiveInspection!!.hiveConditions.copy(
                                            // The temperature returns in the correct unit from the API
                                            temperature = weatherResponse.data.values.temperature,
                                            weatherCondition = weatherCodeToWeatherCondition(
                                                weatherResponse.data.values.weatherCode
                                            ),
                                            humidity = weatherResponse.data.values.humidity,
                                        )
                                    )
                                )
                            }
                        }.onFailure {
                            Log.e(
                                this::class.simpleName,
                                "Error getting weather data: ${it.stackTraceToString()}"
                            )
                        }
                }
            }
        }
    }

    private fun weatherCodeToWeatherCondition(weatherCode: Int): WeatherCondition {
        val weatherCodes = mapOf(
            0 to "Unknown",
            1000 to "Clear, Sunny",
            1100 to "Mostly Clear",
            1101 to "Partly Cloudy",
            1102 to "Mostly Cloudy",
            1001 to "Cloudy",
            2000 to "Fog",
            2100 to "Light Fog",
            4000 to "Drizzle",
            4001 to "Rain",
            4200 to "Light Rain",
            4201 to "Heavy Rain",
            5000 to "Snow",
            5001 to "Flurries",
            5100 to "Light Snow",
            5101 to "Heavy Snow",
            6000 to "Freezing Drizzle",
            6001 to "Freezing Rain",
            6200 to "Light Freezing Rain",
            6201 to "Heavy Freezing Rain",
            7000 to "Ice Pellets",
            7101 to "Heavy Ice Pellets",
            7102 to "Light Ice Pellets",
            8000 to "Thunderstorm"
        )

        return WeatherCondition.entries.find { it.displayValue == weatherCodes[weatherCode] }
            ?: WeatherCondition.UNKNOWN
    }

    private fun updateUserData(userData: UserData) {
        Log.d(this::class.simpleName, "updateUserData: $userData")
        _state.update {
            it.copy(
                isLoading = false,
                userData = userData,
                selectedHive = userData.hives.find { hive ->
                    hive.id == state.value.selectedHive?.id
                }
            )
        }
        _hives.update { userData.hives }
        syncUserData()
    }

    fun onTapOutside() {
        closeOpenMenus()
    }

    fun writeBitmapToFile(bitmap: Bitmap?) {
        viewModelScope.launch {
            setIsLoading(true)
            runCatching {
                bitmap?.let {
                    val uri = getImageUri("${state.value.selectedHive?.id}")
                    val file = File(
                        uri.path ?: throw NotFoundException("Error getting file path")
                    )
                    val out = FileOutputStream(file)
                    it.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    out.flush()
                    out.close()
                    // update the hive with the new image
                    state.value.selectedHive?.let { hive ->
                        updateHive(
                            hive.copy(
                                hiveDetails = hive.hiveDetails.copy(
                                    image = file.absolutePath
                                )
                            )
                        )
                    }
                }
            }.onSuccess {
                showSuccess()
                syncUserData()
            }.onFailure {
                showError(it.message ?: "Unknown error")
            }
        }
    }

    private fun syncUserData() {
        viewModelScope.launch {
            saveUserDataToLocal()
            saveUserDataToRemote()
        }
    }

    fun deleteImage() {
        setIsLoading(true)
        viewModelScope.launch {
            runCatching {
                state.value.selectedHive?.let { hive ->
                    updateHive(
                        hive.copy(
                            hiveDetails = hive.hiveDetails.copy(
                                image = ""
                            )
                        )
                    )
                }
            }.onSuccess {
                showSuccess()
                syncUserData()
            }.onFailure {
                showError(it.message ?: "Unknown error")
            }
        }
    }

    fun getImageUri(name: String): Uri {
        val directory = File(app.cacheDir, "images")
        directory.mkdirs()
        val file = File.createTempFile(
            "image_${name}",
            ".jpg",
            directory,
        )
        val authority = app.packageName + ".fileprovider"
        return getUriForFile(
            app,
            authority,
            file,
        )
    }

    /**
     * Copies the image from the external storage to the internal storage. This is needed because
     * the image uri from the external storage is not persistent.
     * @param [Uri] The uri of the image from the external storage.
     */
    private fun copyImageToInternalStorage(uriFromExternalStorage: Uri?) {
        viewModelScope.launch {
            // save the image uri to the disk
            uriFromExternalStorage?.let {
                // copy
                val uri = getImageUri(state.value.selectedHive!!.id)
                val inputStream = app.contentResolver.openInputStream(it)
                val outputStream = app.contentResolver.openOutputStream(uri)
                inputStream?.copyTo(outputStream!!)
                inputStream?.close()
                outputStream?.close()
                // update the hive with the new image
                state.value.selectedHive?.let { hive ->
                    updateHive(
                        hive.copy(
                            hiveDetails = hive.hiveDetails.copy(
                                image = uri.toString()
                            )
                        )
                    )
                }
            }
        }
    }

    /**
     * Deselects the selected hive. This is used in the deletion process to prevent the user from
     * editing the hive while it is being deleted.
     */
    private fun deselectHive() {
        _state.update { state.value.copy(selectedHive = null) }
    }

    fun deleteImage(uri: Uri) {
        viewModelScope.launch {
            setIsLoading(true)
            runCatching {
                app.contentResolver.delete(uri, null, null)
            }.onSuccess {
                showSuccess()
                syncUserData()
            }.onFailure {
                showError(it.message ?: "Unknown error")
            }
        }
    }

    private fun removeHiveDataEntry(hiveInspection: HiveInspection) {
        state.value.selectedHive?.let { hive ->
            viewModelScope.launch {
                updateHive(
                    hive.copy(
                        hiveInspections = hive.hiveInspections - hiveInspection
                    )
                )
            }
        }
    }

    private fun updateHiveInspection(hiveInspection: HiveInspection) {
        runCatching {
            updateHive(hive = state.value.selectedHive!!.copy(hiveInspections = state.value.selectedHive!!.hiveInspections.map {
                if (it.date == hiveInspection.date) {
                    hiveInspection
                } else {
                    it
                }
            }))
        }.onSuccess {
            showSuccess()
        }.onFailure {
            showError(it.message ?: "Error updating hive inspection")
        }
    }

    fun updateSelectedInspection(newHiveInspection: HiveInspection) {
        _state.update {
            state.value.copy(
                selectedHiveInspection = newHiveInspection
            )
        }
    }

    fun updateHiveHealthEstimation(value: Float) {
        updateSelectedInspection(
            state.value.selectedHiveInspection!!.copy(
                hiveHealth = state.value.selectedHiveInspection!!.hiveHealth.copy(
                    healthEstimation = value
                )
            )
        )
    }

    fun saveInspection(navController: NavController) {
        viewModelScope.launch {
            runCatching {
                state.value.selectedHive?.let { hive ->
                    updateHive(hive.copy(hiveInspections = hive.hiveInspections.map {
                        if (it.id == state.value.selectedHiveInspection?.id) {
                            state.value.selectedHiveInspection!!
                        } else {
                            it
                        }
                    }))
                }
            }.onSuccess {
                showSuccess()
                navController.popBackStack()
            }.onFailure {
                showError(it.message ?: "Error saving hive inspection")
            }
        }
    }

    fun onDoubleValueChange(
        previousString: String, updatedString: String, function: (Double) -> Unit
    ) {
        // do nothing if the user deletes a decimal or adds a second decimal
        // this is a hacky way to prevent the user from entering a decimal
        if ((updatedString.contains("..") || !updatedString.contains(".")) && previousString != "") {
            return
        }

        if (updatedString.toDoubleOrNull() == null) {
            function(0.0)
        } else if (previousString.toDoubleOrNull() == 0.0) {
            function(updatedString.first().toString().toDouble())
        } else {
            function(updatedString.toDouble())
        }
    }

    private fun getDefaultInspection(): HiveInspection {
        return HiveInspection(
            hiveId = UUID.randomUUID().toString(),
            date = LocalDate.now().toString(),
            hiveConditions = HiveConditions(),
            hiveHealth = HiveHealth(),
            feeding = HiveFeeding(),
            localPhotoUris = emptyList(),
            hiveTreatments = emptyList(),
        )
    }

    fun onTapSettingsButton(navController: NavController) {
        closeOpenMenus()
        navController.navigate(Screen.SettingsScreen.route)
    }

    fun onTapHiveCard(id: String, navController: NavController) {
        setSelectedHive(id)
        closeOpenMenus()
        navController.navigate(
            Screen.HiveDetailsScreen.route + Screen.HiveDetailsScreen.arguments?.replace(
                "{hiveId}",
                id
            )
        )
    }

    fun setImageForSelectedHive(uri: Uri?) {
        setIsLoading(true)
        viewModelScope.launch {
            runCatching {
                state.value.selectedHive?.let { hive ->

                    // delete the current image from the disk if it exists
                    hive.hiveDetails.image?.let {
                        removeImageForSelectedHive()
                    }

                    copyImageToInternalStorage(uri)

                    // update the hive with the new image
                    updateHive(
                        hive.copy(
                            hiveDetails = hive.hiveDetails.copy(
                                image = uri?.toString() ?: hive.hiveDetails.image
                            )
                        )
                    )
                }
            }.onSuccess {
                showSuccess()
            }.onFailure {
                showError(it.message ?: "Unknown error")
            }
        }
    }

    fun removeImageForSelectedHive() {
        setIsLoading(true)
        viewModelScope.launch {
            runCatching {
                // delete the image from the disk
                state.value.selectedHive?.hiveDetails?.image?.let { uriString ->
                    val uri = Uri.parse(uriString)
                    deleteImage(uri)
                    // update the hive with the new image
                }

                state.value.selectedHive?.let { hive ->
                    updateHive(
                        hive.copy(
                            hiveDetails = hive.hiveDetails.copy(
                                image = null
                            )
                        )
                    )
                }
            }.onSuccess {
                showSuccess()
            }.onFailure {
                showError(it.message ?: "Unknown error")
            }
        }
    }

    fun onTapInspectionsButton(navController: NavController) {
        closeOpenMenus()
        navController.navigate(Screen.InspectionsScreen.route)
    }

    /**
     * Creates a new hive inspection for the selected hive.
     * @see [HiveInspection]
     */
    private fun createInspection() {
        val newInspection = getDefaultInspection()
        updateUserData(state.value.userData.copy(
            hives = hives.value.map {
                if (it.id == state.value.selectedHive?.id) {
                    it.copy(
                        hiveInspections = it.hiveInspections + newInspection
                    )
                } else {
                    it
                }
            }
        ))

        // We must update the selected inspection so we can apply changes to it.
        _state.update {
            it.copy(
                selectedHiveInspection = newInspection
            )
        }

        getWeatherData()
    }

    fun onTapTasksButton(navController: NavController) {
        closeOpenMenus()
        navController.navigate(Screen.TasksScreen.route)
    }

    fun onTapInspectionButton(inspection: HiveInspection, navController: NavController) {
        updateSelectedInspection(inspection)
        closeOpenMenus()
        navController.navigate(Screen.LogInspectionScreen.route)
    }

    fun onTapDeleteInspectionButton(inspection: HiveInspection) {
        removeHiveDataEntry(inspection)
    }

    fun onTapManageHoneyButton(navController: NavController) {
        TODO()
    }

    fun onTapExportToCsvButton() {
        exportToCsv()
    }

    fun onTapDeleteHiveButton(hive: Hive) {
        showDeleteHiveDialog(hive.id)
    }

    fun onTapInspectionsInsightsButton(navController: NavController) {
        closeOpenMenus()
        navController.navigate(Screen.InspectionInsightsScreen.route)
    }

    fun resetDateToToday() {
        updateSelectedInspection(
            state.value.selectedHiveInspection!!.copy(
                date = LocalDate.now().toString()
            )
        )
    }
}