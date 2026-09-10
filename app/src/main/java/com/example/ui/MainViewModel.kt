package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioPlayerManager
import com.example.data.ImageRepository
import com.example.data.PreferencesManager
import com.example.model.DhikrScreenState
import com.example.model.DhikrSection
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsManager = PreferencesManager(application)
    val imageRepository = ImageRepository(application)
    private val audioManager = AudioPlayerManager(application)

    private val _screenState = MutableStateFlow<DhikrScreenState>(DhikrScreenState.Splash)
    val screenState: StateFlow<DhikrScreenState> = _screenState.asStateFlow()

    private val _isMusicEnabled = MutableStateFlow(prefsManager.isMusicEnabled)
    val isMusicEnabled: StateFlow<Boolean> = _isMusicEnabled.asStateFlow()

    private val _isDhikrSoundEnabled = MutableStateFlow(prefsManager.isDhikrSoundEnabled)
    val isDhikrSoundEnabled: StateFlow<Boolean> = _isDhikrSoundEnabled.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    private val _stepSequenceNumber = MutableStateFlow(0)
    val stepSequenceNumber: StateFlow<Int> = _stepSequenceNumber.asStateFlow()

    init {
        // Start background ambient music immediately on launch
        audioManager.startBackgroundMusic(_isMusicEnabled.value)

        // 2-second splash screen transition
        viewModelScope.launch {
            delay(2000)
            if (_screenState.value is DhikrScreenState.Splash) {
                _screenState.value = DhikrScreenState.Active(DhikrSection.ALLAHU_AKBAR, 1)
                _stepSequenceNumber.value = 1
                audioManager.playDhikrSound(DhikrSection.ALLAHU_AKBAR, _isDhikrSoundEnabled.value)
            }
        }
    }

    fun onScreenTap() {
        val current = _screenState.value
        if (current !is DhikrScreenState.Active) return

        if (current.currentIndex < current.section.totalCount) {
            val nextIndex = current.currentIndex + 1
            _screenState.value = DhikrScreenState.Active(current.section, nextIndex)
            _stepSequenceNumber.value += 1
            audioManager.playDhikrSound(current.section, _isDhikrSoundEnabled.value)
        } else {
            // End of section -> transition to next section or completed
            when (current.section) {
                DhikrSection.ALLAHU_AKBAR -> {
                    _screenState.value = DhikrScreenState.Active(DhikrSection.ALHAMDULILLAH, 1)
                    _stepSequenceNumber.value += 1
                    audioManager.playDhikrSound(DhikrSection.ALHAMDULILLAH, _isDhikrSoundEnabled.value)
                }
                DhikrSection.ALHAMDULILLAH -> {
                    _screenState.value = DhikrScreenState.Active(DhikrSection.SUBHANALLAH, 1)
                    _stepSequenceNumber.value += 1
                    audioManager.playDhikrSound(DhikrSection.SUBHANALLAH, _isDhikrSoundEnabled.value)
                }
                DhikrSection.SUBHANALLAH -> {
                    _screenState.value = DhikrScreenState.Completed
                    _stepSequenceNumber.value += 1
                }
            }
        }
    }

    fun setMusicEnabled(enabled: Boolean) {
        prefsManager.isMusicEnabled = enabled
        _isMusicEnabled.value = enabled
        if (enabled) {
            audioManager.startBackgroundMusic(true)
        } else {
            audioManager.stopBackgroundMusic()
        }
    }

    fun setDhikrSoundEnabled(enabled: Boolean) {
        prefsManager.isDhikrSoundEnabled = enabled
        _isDhikrSoundEnabled.value = enabled
    }

    fun restart() {
        _screenState.value = DhikrScreenState.Active(DhikrSection.ALLAHU_AKBAR, 1)
        _stepSequenceNumber.value += 1
        audioManager.restartBackgroundMusic(_isMusicEnabled.value)
        audioManager.playDhikrSound(DhikrSection.ALLAHU_AKBAR, _isDhikrSoundEnabled.value)
    }

    fun setSettingsOpen(isOpen: Boolean) {
        _isSettingsOpen.value = isOpen
    }

    fun onPause() {
        audioManager.onPause()
    }

    fun onResume() {
        audioManager.onResume(_isMusicEnabled.value)
    }

    override fun onCleared() {
        super.onCleared()
        audioManager.release()
    }
}
