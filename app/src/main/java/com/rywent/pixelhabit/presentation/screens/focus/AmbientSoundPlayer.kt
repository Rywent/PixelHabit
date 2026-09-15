package com.rywent.pixelhabit.presentation.screens.focus

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AmbientSoundPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var loopPlayer: MediaPlayer? = null
    private var chimePlayer: MediaPlayer? = null
    private var current: AmbientSound = AmbientSound.NONE

    fun playLoop(sound: AmbientSound) {
        stopLoop()
        current = sound
        if (sound == AmbientSound.NONE || sound.rawName.isBlank()) return

        val resId = resolveRaw(sound.rawName)
        if (resId == 0) return

        try {
            loopPlayer = MediaPlayer.create(context, resId)?.apply {
                isLooping = true
                setAudioAttributes(mediaAttrs())
                setVolume(0.5f, 0.5f)
                start()
            }
        } catch (_: Exception) {
            loopPlayer = null
        }
    }

    fun pauseLoop() {
        try { loopPlayer?.pause() } catch (_: Exception) { }
    }

    fun resumeLoop() {
        if (current == AmbientSound.NONE) return
        try {
            if (loopPlayer != null) loopPlayer?.start()
            else playLoop(current)
        } catch (_: Exception) { }
    }

    fun stopLoop() {
        try {
            loopPlayer?.stop()
            loopPlayer?.release()
        } catch (_: Exception) { }
        loopPlayer = null
        current = AmbientSound.NONE
    }

    fun playCountdownTick() {
        try {
            val tg = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 30)
            tg.startTone(ToneGenerator.TONE_PROP_BEEP, 70)
            Handler(Looper.getMainLooper()).postDelayed({
                try { tg.release() } catch (_: Exception) { }
            }, 180)
        } catch (_: Exception) { }
    }

    fun playCompletionChime() {
        val resId = resolveRaw("focus_complete")
        if (resId != 0) {
            try {
                chimePlayer?.release()
                chimePlayer = MediaPlayer.create(context, resId)?.apply {
                    setAudioAttributes(mediaAttrs())
                    setVolume(0.75f, 0.75f)
                    setOnCompletionListener {
                        it.release()
                        chimePlayer = null
                    }
                    start()
                }
                return
            } catch (_: Exception) { }
        }
        try {
            val tg = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 50)
            tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 350)
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    tg.startTone(ToneGenerator.TONE_PROP_ACK, 220)
                    Handler(Looper.getMainLooper()).postDelayed({
                        try { tg.release() } catch (_: Exception) { }
                    }, 280)
                } catch (_: Exception) { }
            }, 400)
        } catch (_: Exception) { }
    }

    fun releaseAll() {
        stopLoop()
        try { chimePlayer?.release() } catch (_: Exception) { }
        chimePlayer = null
    }

    private fun resolveRaw(name: String): Int =
        context.resources.getIdentifier(name, "raw", context.packageName)

    private fun mediaAttrs() = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()
}
