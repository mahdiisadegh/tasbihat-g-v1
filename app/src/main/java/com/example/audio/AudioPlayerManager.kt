package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import com.example.R
import com.example.model.DhikrSection

class AudioPlayerManager(private val context: Context) {

    private var bgMusicPlayer: MediaPlayer? = null
    private var dhikrSoundPlayer: MediaPlayer? = null

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    fun startBackgroundMusic(enabled: Boolean) {
        if (!enabled) {
            stopBackgroundMusic()
            return
        }

        if (bgMusicPlayer == null) {
            val resId = getRawResourceId("audio4", R.raw.audio4)
            if (resId != 0) {
                try {
                    bgMusicPlayer = MediaPlayer.create(context, resId, audioAttributes, 0)?.apply {
                        isLooping = true
                        setVolume(0.45f, 0.45f)
                        start()
                    }
                } catch (e: Exception) {
                    Log.e("AudioPlayerManager", "Error starting background music", e)
                }
            }
        } else {
            try {
                if (bgMusicPlayer?.isPlaying == false) {
                    bgMusicPlayer?.start()
                }
            } catch (e: Exception) {
                Log.e("AudioPlayerManager", "Error resuming background music", e)
            }
        }
    }

    fun stopBackgroundMusic() {
        try {
            bgMusicPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                }
            }
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Error pausing background music", e)
        }
    }

    fun restartBackgroundMusic(enabled: Boolean) {
        try {
            bgMusicPlayer?.let {
                it.stop()
                it.release()
            }
            bgMusicPlayer = null
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Error releasing bg music", e)
            bgMusicPlayer = null
        }
        if (enabled) {
            startBackgroundMusic(true)
        }
    }

    fun playDhikrSound(section: DhikrSection, enabled: Boolean) {
        if (!enabled) return

        val defaultRawRes = when (section) {
            DhikrSection.ALLAHU_AKBAR -> R.raw.audio1
            DhikrSection.ALHAMDULILLAH -> R.raw.audio2
            DhikrSection.SUBHANALLAH -> R.raw.audio3
        }

        val resId = getRawResourceId(section.soundResName, defaultRawRes)
        if (resId == 0) return

        try {
            dhikrSoundPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
            dhikrSoundPlayer = MediaPlayer.create(context, resId, audioAttributes, 0)?.apply {
                setVolume(1.0f, 1.0f)
                setOnCompletionListener { mp ->
                    try {
                        mp.release()
                        if (dhikrSoundPlayer == mp) {
                            dhikrSoundPlayer = null
                        }
                    } catch (_: Exception) {}
                }
                start()
            }
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Error playing dhikr sound", e)
        }
    }

    fun onPause() {
        try {
            bgMusicPlayer?.let {
                if (it.isPlaying) it.pause()
            }
        } catch (_: Exception) {}
    }

    fun onResume(musicEnabled: Boolean) {
        if (musicEnabled) {
            try {
                if (bgMusicPlayer != null && bgMusicPlayer?.isPlaying == false) {
                    bgMusicPlayer?.start()
                } else if (bgMusicPlayer == null) {
                    startBackgroundMusic(true)
                }
            } catch (_: Exception) {
                startBackgroundMusic(true)
            }
        }
    }

    fun release() {
        try {
            bgMusicPlayer?.release()
            bgMusicPlayer = null
            dhikrSoundPlayer?.release()
            dhikrSoundPlayer = null
        } catch (_: Exception) {}
    }

    private fun getRawResourceId(name: String, fallbackRes: Int): Int {
        val id = try {
            context.resources.getIdentifier(name, "raw", context.packageName)
        } catch (_: Exception) {
            0
        }
        return if (id != 0) id else fallbackRes
    }
}
