package com.example.chat_bot.ui.components

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

/**
 * Video Player optimizado para LSM (Lengua de Señas Mexicana)
 * Características:
 * - Auto-loop para práctica
 * - Control de velocidad (0.5x, 1x, 1.5x)
 * - Auto-release de recursos
 * - Soporte URLs locales y remotas
 */
@Composable
fun LSMVideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
    loop: Boolean = true,
    showControls: Boolean = true,
    onPlayerReady: (() -> Unit)? = null,
    onError: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(autoPlay) }
    var playbackSpeed by remember { mutableFloatStateOf(1f) }
    var showSpeedMenu by remember { mutableStateOf(false) }
    
    // Create ExoPlayer instance
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
            
            // Configure loop mode
            repeatMode = if (loop) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
            
            // Auto-play if enabled
            playWhenReady = autoPlay
            
            // Add listener for errors
            addListener(object : Player.Listener {
                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    onError?.invoke(error.message ?: "Error al reproducir video")
                }
                
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        onPlayerReady?.invoke()
                    }
                }
            })
        }
    }
    
    // Update playback speed
    LaunchedEffect(playbackSpeed) {
        exoPlayer.setPlaybackSpeed(playbackSpeed)
    }
    
    // Release player when composable leaves composition
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
    
    Box(modifier = modifier) {
        // ExoPlayer View
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = false // Use custom controls
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Custom Controls Overlay
        if (showControls) {
            VideoControlsOverlay(
                isPlaying = isPlaying,
                playbackSpeed = playbackSpeed,
                showSpeedMenu = showSpeedMenu,
                onPlayPauseClick = {
                    isPlaying = !isPlaying
                    if (isPlaying) {
                        exoPlayer.play()
                    } else {
                        exoPlayer.pause()
                    }
                },
                onReplayClick = {
                    exoPlayer.seekTo(0)
                    exoPlayer.play()
                    isPlaying = true
                },
                onSpeedClick = {
                    showSpeedMenu = !showSpeedMenu
                },
                onSpeedSelected = { speed ->
                    playbackSpeed = speed
                    showSpeedMenu = false
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

/**
 * Controles personalizados del video player
 */
@Composable
private fun VideoControlsOverlay(
    isPlaying: Boolean,
    playbackSpeed: Float,
    showSpeedMenu: Boolean,
    onPlayPauseClick: () -> Unit,
    onReplayClick: () -> Unit,
    onSpeedClick: () -> Unit,
    onSpeedSelected: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.3f))
            .padding(8.dp)
    ) {
        // Speed selector menu
        if (showSpeedMenu) {
            SpeedSelectorMenu(
                currentSpeed = playbackSpeed,
                onSpeedSelected = onSpeedSelected,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }
        
        // Control buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Replay button
            IconButton(onClick = onReplayClick) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = "Repetir",
                    tint = Color.White
                )
            }
            
            // Play/Pause button
            IconButton(onClick = onPlayPauseClick) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            // Speed control button
            TextButton(
                onClick = onSpeedClick,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text("${playbackSpeed}x")
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = "Velocidad",
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

/**
 * Menu selector de velocidad de reproducción
 */
@Composable
private fun SpeedSelectorMenu(
    currentSpeed: Float,
    onSpeedSelected: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val speeds = listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 2f)
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.8f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = "Velocidad de reproducción",
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                speeds.forEach { speed ->
                    FilterChip(
                        selected = speed == currentSpeed,
                        onClick = { onSpeedSelected(speed) },
                        label = {
                            Text(
                                text = "${speed}x",
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }
    }
}

/**
 * Video Player compacto sin controles (para thumbnails o previews)
 */
@Composable
fun LSMVideoPlayerCompact(
    videoUrl: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = false,
    muted: Boolean = true
) {
    val context = LocalContext.current
    
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = autoPlay
            repeatMode = Player.REPEAT_MODE_ONE
            volume = if (muted) 0f else 1f
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
    
    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                player = exoPlayer
                useController = false
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = modifier
    )
}
