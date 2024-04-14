package org.deepsymmetry.beatlink.data

import org.deepsymmetry.beatlink.CdjStatus
import org.deepsymmetry.beatlink.DeviceUpdateListener
import org.deepsymmetry.beatlink.Util
import org.deepsymmetry.beatlink.VirtualCdj
import org.deepsymmetry.cratedigger.pdb.RekordboxAnlz.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.*
import java.awt.geom.Rectangle2D
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import javax.swing.JComponent
import javax.swing.JScrollPane
import javax.swing.UIManager
import kotlin.math.ceil

/**
 * Provides a convenient way to draw waveform detail in a user interface, including annotations like the
 * location at the current time, and cue point markers and loops (if you supply [TrackMetadata] so their
 * location can be determined), and beat markers (if you also supply a [BeatGrid]). Can also
 * be configured to automatically update itself to reflect the state of a specified player, showing the current
 * track, playback state, and position, as long as it is able to load appropriate metadata. When tracking a live
 * player, if that player is a CDJ-3000, dynamic loops (set up on the fly by the DJ) are also displayed, and
 * inactive loops from the track metadata are drawn in gray rather than orange.
 */
class WaveformDetailComponent : JComponent {
    /**
     * If not zero, automatically update the waveform, position, and metadata in response to the activity of the
     * specified player number.
     */
    private val monitoredPlayer = AtomicInteger(0)

    /**
     * Determines how we decide what to draw. The default behavior is to draw as much of the waveform as fits
     * within our current size at the current scale around the current playback position (or, if we are tracking
     * multiple players, the furthest playback position, prioritizing active players even if they are not as far as
     * an inactive player). If this is changed to `false` then changing the scale actually changes the size
     * of the component, and we always draw the full waveform at the chosen scale, allowing an outer scroll pane to
     * control what is visible.
     */
    private val autoScroll = AtomicBoolean(true)

    /**
     * The color to which the background is cleared before drawing the waveform. The default is black,
     * but can be changed (including to a transparent color) for use in other contexts, like the OBS overlay.
     */
    private val backgroundColor = AtomicReference(Color.BLACK)

    /**
     * The color with which the playback position and tick markers are drawn. The default is white,
     * but can be changed (including to a transparent color) for use in other contexts, like the OBS overlay.
     */
    private val indicatorColor = AtomicReference(Color.WHITE)

    /**
     * The color with which the playback position is drawn while playback is active. The default is red,
     * but can be changed (including to a transparent color) for use in other contexts, like the OBS overlay.
     */
    private val emphasisColor = AtomicReference(Color.RED)

    /**
     * Determines the font to use when drawing hot cue, memory point, and loop labels. If `null`, they are
     * not drawn at all.
     */
    private val labelFont = AtomicReference(UIManager.getDefaults().getFont("Label.font"))

    /**
     * The waveform preview that we are drawing.
     */
    private val waveform = AtomicReference<WaveformDetail?>()

    /**
     * Track the playback state for the players that have the track loaded.
     */
    private val playbackStateMap: MutableMap<Int, PlaybackState> = ConcurrentHashMap(4)

    /**
     * Track how many segments we average into a column of pixels; larger values zoom out, 1 is full scale.
     */
    private val scale = AtomicInteger(1)

    /**
     * Information about the cues, memory points, and loops in the track.
     */
    private val cueList = AtomicReference<CueList?>()

    /**
     * Information about where all the beats in the track fall, so we can draw them.
     */
    private val beatGrid = AtomicReference<BeatGrid?>()

    /**
     * Controls whether we should obtain and display song structure information (phrases) at the bottom of the
     * waveform.
     */
    private val fetchSongStructures = AtomicBoolean(true)

    /**
     * Information about the musical phrases that make up the current track, if we have it, so we can draw them.
     */
    private val songStructure = AtomicReference<SongStructureTag?>()

    /**
     * The overlay painter that has been registered, if any.
     */
    private val overlayPainter = AtomicReference<OverlayPainter>()

    /**
     * Control whether the component should automatically center itself on the playback position of the player
     * that is furthest into the track. This is the default behavior of the component, and will allow it to be
     * useful at any size, showing a currently-relevant portion of the waveform. If set to `false` the
     * component must be inside a scroll pane so the user can control what portion of the waveform is visible.
     *
     * @param auto should the waveform be centered on the playback position
     */
    fun setAutoScroll(auto: Boolean) {
        if (autoScroll.getAndSet(auto) != auto) {
            size = preferredSize
            repaint()
        }
    }

    /**
     * Check whether the component should automatically center itself on the playback position of the player
     * that is furthest into the track. This is the default behavior of the component, and will allow it to be
     * useful at any size, showing a currently-relevant portion of the waveform. If set to `false` the
     * component must be inside a scroll pane so the user can control what portion of the waveform is visible.
     *
     * @return `true` if the waveform will be centered on the playback position
     */
    fun getAutoScroll(): Boolean {
        return autoScroll.get()
    }

    /**
     * Examine the color to which the background is cleared before drawing the waveform. The default is black,
     * but can be changed (including to a transparent color) for use in other contexts, like the OBS overlay.
     *
     * @return the color used to draw the component background
     */
    fun getBackgroundColor(): Color {
        return backgroundColor.get()
    }

    /**
     * Change the color to which the background is cleared before drawing the waveform. The default is black,
     * but can be changed (including to a transparent color) for use in other contexts, like the OBS overlay.
     *
     * @param color the color used to draw the component background
     */
    fun setBackgroundColor(color: Color) {
        backgroundColor.set(color)
    }

    /**
     * Examine the color with which the playback position and tick markers are drawn. The default is white,
     * but can be changed (including to a transparent color) for use in other contexts, like the OBS overlay.
     *
     * @return the color used to draw the playback and tick markers
     */
    fun getIndicatorColor(): Color {
        return indicatorColor.get()
    }

    /**
     * Change the color with which the playback position and tick markers are drawn. The default is white,
     * but can be changed (including to a transparent color) for use in other contexts, like the OBS overlay.
     *
     * @param color the color used to draw the playback marker when actively playing
     */
    fun setIndicatorColor(color: Color) {
        indicatorColor.set(color)
    }

    /**
     * Examine the color with which the playback position is drawn when playback is active. The default is red,
     * but can be changed (including to a transparent color) for use in other contexts, like the OBS overlay.
     *
     * @return the color used to draw the playback and tick markers
     */
    fun getEmphasisColor(): Color {
        return emphasisColor.get()
    }

    /**
     * Change the color with which the playback position is drawn when playback is active. The default is red,
     * but can be changed (including to a transparent color) for use in other contexts, like the OBS overlay.
     *
     * @param color the color used to draw the playback marker when actively playing
     */
    fun setEmphasisColor(color: Color) {
        emphasisColor.set(color)
    }

    /**
     * Specify the font to be used when drawing hot cue, memory point, and loop labels. If `null`, do not draw
     * them at all. The default is the standard label font defined by the current Swing look and feel.
     *
     * @param font if not `null`, draw labels for hot cues and named memory points and loops, and use this font
     */
    fun setLabelFont(font: Font) {
        labelFont.set(font)
        repaint()
    }

    /**
     * Check the font being used to draw hot cue, memory point, and loop labels. If `null`, they are not being
     * drawn at all.
     *
     * @return if not `null`, labels are being drawn for hot cues and named memory points and loops, in this font
     */
    fun getLabelFont(): Font {
        return labelFont.get()
    }

    /**
     * Establish a song structure (phrase analysis) to be displayed on the waveform. If we are configured to monitor
     * a player, then this will be overwritten the next time a track loads.
     *
     * @param songStructure the phrase information to be painted at the bottom of the waveform, or `null` to display none
     */
    fun setSongStructure(songStructure: SongStructureTag?) {
        this.songStructure.set(songStructure)
        repaint()
    }

    /**
     * Unwrap the tagged section to find the song structure inside it if it is not null, otherwise set our song
     * structure to null.
     *
     * @param taggedSection a possible tagged section holding song structure information.
     */
    private fun setSongStructureWrapper(taggedSection: TaggedSection?) {
        if (taggedSection == null) {
            setSongStructure(null)
        } else if (taggedSection.fourcc() == SectionTags.SONG_STRUCTURE) {
            setSongStructure(taggedSection.body() as SongStructureTag)
        } else {
            logger.warn("Received unexpected analysis tag type:$taggedSection")
        }
    }

    /**
     * Determine whether we should try to obtain the song structure for tracks that we are displaying, and paint
     * the phrase information at the bottom of the waveform. Only has effect if we are monitoring a player.
     *
     * @param fetchSongStructures `true` if we should try to obtain and display phrase analysis information
     */
    @Synchronized
    fun setFetchSongStructures(fetchSongStructures: Boolean) {
        this.fetchSongStructures.set(fetchSongStructures)
        if (fetchSongStructures && monitoredPlayer.get() > 0) {
            AnalysisTagFinder.getInstance().addAnalysisTagListener(analysisTagListener, ".EXT", "PSSI")
            if (AnalysisTagFinder.getInstance().isRunning) {
                setSongStructureWrapper(
                    AnalysisTagFinder.getInstance().getLatestTrackAnalysisFor(monitoredPlayer.get(), ".EXT", "PSSI")
                )
            }
        } else {
            AnalysisTagFinder.getInstance().removeAnalysisTagListener(analysisTagListener, ".EXT", "PSSI")
        }
    }

    /**
     * Check whether we are supposed to obtain the song structure for tracks we are displaying when we are monitoring
     * a player.
     *
     * @return `true` if we should try to obtain and display phrase analysis information
     */
    fun getFetchSongStructures(): Boolean {
        return fetchSongStructures.get()
    }

    /**
     * Arrange for an overlay to be painted on top of the component.
     *
     * @param painter if not `null`, its [OverlayPainter.paintOverlay] method will
     * be called once this component has done its own painting
     */
    fun setOverlayPainter(painter: OverlayPainter) {
        overlayPainter.set(painter)
    }

    /**
     * Helper method to mark the parts of the component that need repainting due to a change to the
     * tracked playback positions.
     *
     * @param oldState the old position of a marker being moved, or `null` if we are adding a marker
     * @param newState the new position of a marker being moved, or `null` if we are removing a marker
     * @param oldFurthestState the position at which the waveform was centered before this update, if we are auto-scrolling
     */
    private fun repaintDueToPlaybackStateChange(
        oldState: PlaybackState?,
        newState: PlaybackState?,
        oldFurthestState: PlaybackState?
    ) {
        if (autoScroll.get()) {
            // See if we need to repaint the whole component because our center point has shifted
            var oldFurthest: Long = 0
            if (oldFurthestState != null) {
                oldFurthest = oldFurthestState.position
            }
            var newFurthest: Long = 0
            val newFurthestState = furthestPlaybackState
            if (newFurthestState != null) {
                newFurthest = newFurthestState.position
            }
            if (oldFurthest != newFurthest) {
                repaint()
                return
            }
        }
        // Refresh where the specific marker was moved from and/or to.
        if (oldState != null) {
            val left = millisecondsToX(oldState.position) - 6
            val right = millisecondsToX(oldState.position) + 6
            repaint(left, 0, right - left, height)
        }
        if (newState != null) {
            val left = millisecondsToX(newState.position) - 6
            val right = millisecondsToX(newState.position) + 6
            repaint(left, 0, right - left, height)
        }
    }

    /**
     *
     * Set the current playback state for a player.
     *
     * Will cause part of the component to be redrawn if the player state has
     * changed (and we have the [TrackMetadata] we need to translate the time into a position in the
     * component). This will be quickly overruled if a player is being monitored, but
     * can be used in other contexts.
     *
     * @param player the player number whose playback state is being recorded
     * @param position the current playback position of that player in milliseconds
     * @param playing whether the player is actively playing the track
     *
     * @throws IllegalStateException if the component is configured to monitor a player, and this is called
     * with state for a different player
     * @throws IllegalArgumentException if player is less than one
     *
     * @since 0.5.0
     */
    @Synchronized
    fun setPlaybackState(player: Int, position: Long, playing: Boolean) {
        check(!(getMonitoredPlayer() != 0 && player != getMonitoredPlayer())) { "Cannot setPlaybackState for another player when monitoring player " + getMonitoredPlayer() }
        require(player >= 1) { "player must be positive" }
        val oldFurthestState = furthestPlaybackState
        val newState = PlaybackState(player, position, playing)
        val oldState = playbackStateMap.put(player, newState)
        if (oldState == null || oldState.position != newState.position || oldState.playing != newState.playing) {
            repaintDueToPlaybackStateChange(oldState, newState, oldFurthestState)
        }
    }

    /**
     * Clear the playback state stored for a player, such as when it has unloaded the track.
     *
     * @param player the player number whose playback state is no longer valid
     * @since 0.5.0
     */
    @Synchronized
    fun clearPlaybackState(player: Int) {
        val oldFurthestState = furthestPlaybackState
        val oldState = playbackStateMap.remove(player)
        repaintDueToPlaybackStateChange(oldState, null, oldFurthestState)
    }

    /**
     * Removes all stored playback state.
     * @since 0.5.0
     */
    @Synchronized
    fun clearPlaybackState() {
        for (state in playbackStateMap.values) {
            clearPlaybackState(state.player)
        }
    }

    /**
     * Look up the playback state recorded for a particular player.
     *
     * @param player the player number whose playback state information is desired
     * @return the corresponding playback state, if any has been stored
     * @since 0.5.0
     */
    fun getPlaybackState(player: Int): PlaybackState? {
        return playbackStateMap[player]
    }

    val playbackState: Set<PlaybackState>
        /**
         * Look up all recorded playback state information.
         *
         * @return the playback state recorded for any player
         * @since 0.5.0
         */
        get() {
            val result: Set<PlaybackState> = HashSet(playbackStateMap.values)
            return Collections.unmodifiableSet(result)
        }

    /**
     * Helper method to find the single current playback state when used in single-player mode.
     *
     * @return either the single stored playback state
     */
    private fun currentSimpleState(): PlaybackState? {
        if (!playbackStateMap.isEmpty()) {  // Avoid exceptions during animation loop shutdown.
            return playbackStateMap.values.iterator().next()
        }
        return null
    }

    /**
     *
     * Set the current playback position. This method can only be used in situations where the component is
     * tied to a single player, and therefore always has a single playback position.
     *
     * Will cause part of the component to be redrawn if the position has
     * changed. This will be quickly overruled if a player is being monitored, but
     * can be used in other contexts.
     *
     * @param milliseconds how far into the track has been played
     *
     * @see .setPlaybackState
     */
    private fun setPlaybackPosition(milliseconds: Long) {
        val oldState = currentSimpleState()
        if (oldState != null && oldState.position != milliseconds) {
            setPlaybackState(oldState.player, milliseconds, oldState.playing)
        }
    }

    /**
     * Set the zoom scale of the view. a value of 1 (the smallest allowed) draws the waveform at full scale.
     * Larger values combine more and more segments into a single column of pixels, zooming out to see more at once.
     *
     * @param scale the number of waveform segments that should be averaged into a single column of pixels
     *
     * @throws IllegalArgumentException if scale is less than 1 or greater than 256
     */
    fun setScale(scale: Int) {
        require(!((scale < 1) || (scale > 256))) { "Scale must be between 1 and 256" }
        val oldScale = this.scale.getAndSet(scale)
        if (oldScale != scale) {
            repaint()
            if (!autoScroll.get()) {
                size = preferredSize
            }
        }
    }

    /**
     * Check the zoom scale of the view. a value of 1 (the smallest allowed) draws the waveform at full scale.
     * Larger values combine more and more segments into a single column of pixels, zooming out to see more at once.
     *
     * @return the current zoom scale.
     */
    fun getScale(): Int {
        return scale.get()
    }

    /**
     * Set whether the player holding the waveform is playing, which changes the indicator color to white from red.
     * This method can only be used in situations where the component is tied to a single player, and therefore has
     * a single playback position.
     *
     * @param playing if `true`, draw the position marker in white, otherwise red
     *
     * @see .setPlaybackState
     */
    private fun setPlaying(playing: Boolean) {
        val oldState = currentSimpleState()
        if (oldState != null && oldState.playing != playing) {
            setPlaybackState(oldState.player, oldState.position, playing)
        }
    }

    /**
     * Change the waveform preview being drawn. This will be quickly overruled if a player is being monitored, but
     * can be used in other contexts.
     *
     * @param waveform the waveform detail to display
     * @param metadata information about the track whose waveform we are drawing, so we can draw cue and memory points
     * @param beatGrid the locations of the beats, so they can be drawn
     */
    fun setWaveform(waveform: WaveformDetail?, metadata: TrackMetadata?, beatGrid: BeatGrid?) {
        this.waveform.set(waveform)
        if (metadata != null) {
            cueList.set(metadata.cueList)
        } else {
            cueList.set(null)
        }
        this.beatGrid.set(beatGrid)
        clearPlaybackState()
        repaint()
        if (!autoScroll.get()) {
            invalidate()
        }
    }

    /**
     * Change the waveform preview being drawn. This will be quickly overruled if a player is being monitored, but
     * can be used in other contexts.
     *
     * @param waveform the waveform detail to display
     * @param cueList used to draw cue and memory points
     * @param beatGrid the locations of the beats, so they can be drawn
     */
    fun setWaveform(waveform: WaveformDetail?, cueList: CueList?, beatGrid: BeatGrid?) {
        this.waveform.set(waveform)
        this.cueList.set(cueList)
        this.beatGrid.set(beatGrid)
        clearPlaybackState()
        repaint()
        if (!autoScroll.get()) {
            invalidate()
        }
    }

    /**
     * Obtain the waveform detail being drawn.
     *
     * @return the waveform detail being displayed by this component.
     */
    fun getWaveform(): WaveformDetail? {
        return waveform.get()
    }

    /**
     * Used to signal our animation thread to stop when we are no longer monitoring a player.
     */
    private val animating = AtomicBoolean(false)

    /**
     * Configures the player whose current track waveforms and status will automatically be reflected. Whenever a new
     * track is loaded on that player, the waveform and metadata will be updated, and the current playback position and
     * state of the player will be reflected by the component.
     *
     * @param player the player number to monitor, or zero if monitoring should stop
     */
    @Synchronized
    fun setMonitoredPlayer(player: Int) {
        require(player >= 0) { "player cannot be negative" }
        clearPlaybackState()
        monitoredPlayer.set(player)
        if (player > 0) {  // Start monitoring the specified player
            setPlaybackState(player, 0, false) // Start with default values for required simple state.
            VirtualCdj.getInstance().addUpdateListener(updateListener)
            MetadataFinder.getInstance().addTrackMetadataListener(metadataListener)
            cueList.set(null) // Assume the worst, but see if we have one available next.
            if (MetadataFinder.getInstance().isRunning) {
                val metadata = MetadataFinder.getInstance().getLatestMetadataFor(player)
                if (metadata != null) {
                    cueList.set(metadata.cueList)
                }
            }
            WaveformFinder.getInstance().addWaveformListener(waveformListener)
            if (WaveformFinder.getInstance().isRunning && WaveformFinder.getInstance().isFindingDetails) {
                waveform.set(WaveformFinder.getInstance().getLatestDetailFor(player))
            } else {
                waveform.set(null)
            }
            BeatGridFinder.getInstance().addBeatGridListener(beatGridListener)
            if (BeatGridFinder.getInstance().isRunning) {
                beatGrid.set(BeatGridFinder.getInstance().getLatestBeatGridFor(player))
            } else {
                beatGrid.set(null)
            }
            if (fetchSongStructures.get()) {
                AnalysisTagFinder.getInstance().addAnalysisTagListener(analysisTagListener, ".EXT", "PSSI")
                if (AnalysisTagFinder.getInstance().isRunning) {
                    setSongStructureWrapper(
                        AnalysisTagFinder.getInstance().getLatestTrackAnalysisFor(player, ".EXT", "PSSI")
                    )
                }
            }
            try {
                TimeFinder.getInstance().start()
                if (!animating.getAndSet(true)) {
                    // Create the thread to update our position smoothly as the track plays
                    Thread {
                        while (animating.get()) {
                            try {
                                Thread.sleep(33) // Animate at 30 fps
                            } catch (e: InterruptedException) {
                                logger.warn("Waveform animation thread interrupted; ending")
                                animating.set(false)
                            }
                            setPlaybackPosition(
                                TimeFinder.getInstance().getTimeFor(getMonitoredPlayer())
                            )
                        }
                    }.start()
                }
            } catch (e: Exception) {
                logger.error("Unable to start the TimeFinder to animate the waveform detail view")
                animating.set(false)
            }
        } else {  // Stop monitoring any player
            animating.set(false)
            VirtualCdj.getInstance().removeUpdateListener(updateListener)
            MetadataFinder.getInstance().removeTrackMetadataListener(metadataListener)
            WaveformFinder.getInstance().removeWaveformListener(waveformListener)
            AnalysisTagFinder.getInstance().removeAnalysisTagListener(analysisTagListener, ".EXT", "PSSI")
            cueList.set(null)
            waveform.set(null)
            beatGrid.set(null)
            songStructure.set(null)
        }
        if (!autoScroll.get()) {
            invalidate()
        }
        repaint()
    }

    /**
     * See which player is having its state tracked automatically by the component, if any.
     *
     * @return the player number being monitored, or zero if none
     */
    fun getMonitoredPlayer(): Int {
        return monitoredPlayer.get()
    }

    /**
     * Reacts to changes in the track metadata associated with the player we are monitoring.
     */
    private val metadataListener = TrackMetadataListener { update ->
        if (update.player == getMonitoredPlayer()) {
            if (update.metadata != null) {
                cueList.set(update.metadata.cueList)
            } else {
                cueList.set(null)
            }
            repaint()
        }
    }

    /**
     * Reacts to changes in the waveform associated with the player we are monitoring.
     */
    private val waveformListener: WaveformListener = object : WaveformListener {
        override fun previewChanged(update: WaveformPreviewUpdate) {
            // Nothing to do.
        }

        override fun detailChanged(update: WaveformDetailUpdate) {
            logger.debug("Got waveform detail update: {}", update)
            if (update.player == getMonitoredPlayer()) {
                waveform.set(update.detail)
                if (!autoScroll.get()) {
                    invalidate()
                }
                repaint()
            }
        }
    }

    /**
     * Reacts to changes in the beat grid associated with the player we are monitoring.
     */
    private val beatGridListener = BeatGridListener { update ->
        if (update.player == getMonitoredPlayer()) {
            beatGrid.set(update.beatGrid)
            repaint()
        }
    }

    /**
     * Reacts to player status updates to reflect the current playback state.
     */
    private val updateListener = DeviceUpdateListener { update ->
        if ((update is CdjStatus) && (update.getDeviceNumber() == getMonitoredPlayer()) &&
            (cueList.get() != null) && (beatGrid.get() != null)
        ) {
            setPlaying(update.isPlaying)
        }
    }

    private val analysisTagListener = AnalysisTagListener { update ->
        if (update.player == getMonitoredPlayer()) {
            setSongStructureWrapper(update.taggedSection)
        }
    }

    /**
     * Create a view which updates itself to reflect the track loaded on a particular player, and that player's
     * playback progress.
     *
     * @param player the player number to monitor, or zero if it should start out monitoring no player
     */
    constructor(player: Int) {
        setMonitoredPlayer(player)
    }

    /**
     * Create a view which draws a specific waveform, even if it is not currently loaded in a player.
     *
     * @param waveform the waveform detail to display
     * @param metadata information about the track whose waveform we are drawing, so we can draw cues and memory points
     * @param beatGrid the locations of the beats, so they can be drawn
     */
    constructor(waveform: WaveformDetail?, metadata: TrackMetadata?, beatGrid: BeatGrid?) {
        this.waveform.set(waveform)
        if (metadata != null) {
            cueList.set(metadata.cueList)
        }
        this.beatGrid.set(beatGrid)
    }

    /**
     * Create a view which draws a specific waveform, even if it is not currently loaded in a player.
     *
     * @param waveform the waveform detail to display
     * @param cueList used to draw cues and memory points
     * @param beatGrid the locations of the beats, so they can be drawn
     */
    constructor(waveform: WaveformDetail?, cueList: CueList?, beatGrid: BeatGrid?) {
        this.waveform.set(waveform)
        this.cueList.set(cueList)
        this.beatGrid.set(beatGrid)
    }

    override fun getMinimumSize(): Dimension {
        val detail = waveform.get()
        if (autoScroll.get() || detail == null) {
            return Dimension(300, 92)
        }
        return Dimension(detail.frameCount / scale.get(), 92)
    }

    override fun getPreferredSize(): Dimension {
        return minimumSize
    }

    val furthestPlaybackState: PlaybackState?
        /**
         * Look up the playback state that has reached furthest in the track, but give playing players priority over stopped players.
         * This is used to choose the scroll center when auto-scrolling is active.
         *
         * @return the playback state, if any, with the highest playing [PlaybackState.position] value
         */
        get() {
            var result: PlaybackState? = null
            for (state in playbackStateMap.values) {
                if (result == null || (!result.playing && state.playing) || (result.position < state.position) && (state.playing || !result.playing)) {
                    result = state
                }
            }
            return result
        }

    val furthestPlaybackPosition: Long
        /**
         * Look up the furthest position, in milliseconds, that has been reached, giving playing players priority over stopped players.
         * If there are no playback positions, returns 0.
         *
         * @return The position in milliseconds of the furthest playback state reached, or 0 if there are no playback states
         */
        get() {
            val state = furthestPlaybackState
            if (state != null) {
                return state.position
            }
            return 0
        }

    /**
     * Figure out the starting waveform segment that corresponds to the specified coordinate in the window.
     *
     * @param x the column being drawn
     *
     * @return the offset into the waveform at the current scale and playback time that should be drawn there
     */
    private fun getSegmentForX(x: Int): Int {
        if (autoScroll.get()) {
            val playHead = (x - (width / 2))
            val offset = Util.timeToHalfFrame(furthestPlaybackPosition) / scale.get()
            return (playHead + offset) * scale.get()
        }
        return x * scale.get()
    }

    /**
     * Determine the playback time that corresponds to a particular X coordinate in the component given the current
     * scale.
     * @param x the horizontal position within the component coordinate space
     * @return the number of milliseconds into the track this would correspond to (may fall outside the actual track)
     */
    fun getTimeForX(x: Int): Long {
        return Util.halfFrameToTime(getSegmentForX(x).toLong())
    }

    /**
     * Determine the beat that corresponds to a particular X coordinate in the component, given the current scale.
     * Clicks past the end of the track will return the final beat, clicks before the first beat (or if there is no
     * beat grid) will return -1.
     *
     * @param x the horizontal position within the component coordinate space
     * @return the beat number being played at that point, or -1 if the point is before the first beat
     */
    fun getBeatForX(x: Int): Int {
        val grid = beatGrid.get()
        if (grid != null) {
            return grid.findBeatAtTime(getTimeForX(x))
        }
        return -1
    }

    /**
     * Determine the X coordinate within the component at which the specified beat begins.
     *
     * @param beat the beat number whose position is desired
     * @return the horizontal position within the component coordinate space where that beat begins
     * @throws IllegalArgumentException if the beat number exceeds the number of beats in the track.
     */
    fun getXForBeat(beat: Int): Int {
        val grid = beatGrid.get()
        if (grid != null) {
            return millisecondsToX(grid.getTimeWithinTrack(beat))
        }
        return 0
    }

    /**
     * Converts a time in milliseconds to the appropriate x coordinate for drawing something at that time.
     *
     * @param milliseconds the time at which something should be drawn
     *
     * @return the component x coordinate at which it should be drawn
     */
    fun millisecondsToX(milliseconds: Long): Int {
        if (autoScroll.get()) {
            val playHead = (width / 2) + 2
            val offset = milliseconds - furthestPlaybackPosition
            return playHead + (Util.timeToHalfFrame(offset) / scale.get())
        }
        return Util.timeToHalfFrame(milliseconds) / scale.get()
    }

    val isDynamicLoopDataAvailable: Boolean
        /**
         * Checks whether any players we are tracking are capable of sending information about current loop status.
         * This is safe to call even when not online, and will simply return `false` in that circumstance.
         *
         * @return whether we have seen a status packet from a tracked player that is a CDJ-3000 or equivalent
         */
        get() {
            if (!VirtualCdj.getInstance().isRunning) return false
            for (state in playbackStateMap.values) {
                val status = VirtualCdj.getInstance().getLatestStatusFor(state.player) as CdjStatus
                if (status.canReportLooping()) return true
            }
            return false
        }

    override fun paintComponent(g: Graphics) {
        val clipRect = g.clipBounds // We only need to draw the part that is visible or dirty
        g.color = backgroundColor.get() // Clear the background
        g.fillRect(clipRect.x, clipRect.y, clipRect.width, clipRect.height)

        val currentCueList = cueList.get() // Avoid crashes if the value changes mid-render.
        val currentSongStructure = songStructure.get() // Same.

        // Draw the loop regions of any visible loops in the tracks. If there is a player sending us dynamic
        // loop information, draw them in gray to distinguish them from known-active loops.
        val drawingDynamicLoops = isDynamicLoopDataAvailable
        val axis = height / 2
        val maxHeight = axis - VERTICAL_MARGIN
        if (currentCueList != null) {
            g.color = if (drawingDynamicLoops) INACTIVE_LOOP_BACKGROUND else LOOP_BACKGROUND
            for (entry in currentCueList.entries) {
                if (entry.isLoop) {
                    val start = millisecondsToX(entry.cueTime)
                    val end = millisecondsToX(entry.loopTime)
                    g.fillRect(start, axis - maxHeight, end - start, maxHeight * 2)
                }
            }
        }
        if (drawingDynamicLoops) {
            // Draw dynamic loop information reported by players actually looping
            g.color = LOOP_BACKGROUND
            for (state in playbackStateMap.values) {
                val status = VirtualCdj.getInstance().getLatestStatusFor(state.player) as CdjStatus
                if (status.loopEnd > 0) {
                    val start = millisecondsToX(status.loopStart)
                    val end = millisecondsToX(status.loopEnd)
                    g.fillRect(start, axis - maxHeight, end - start, maxHeight * 2)
                }
            }
        }

        var lastBeat = 0
        if (beatGrid.get() != null) {  // Find what beat was represented by the column just before the first we draw.
            lastBeat = beatGrid.get()!!.findBeatAtTime(Util.halfFrameToTime(getSegmentForX(clipRect.x - 1).toLong()))
        }
        val g2 = g as Graphics2D
        val standardStroke = g2.stroke
        val wideStroke: Stroke = BasicStroke(2f)
        for (x in clipRect.x..clipRect.x + clipRect.width) {
            val segment = getSegmentForX(x)
            if (waveform.get() != null) { // Drawing the waveform itself
                if ((segment >= 0) && (segment < waveform.get()!!.frameCount)) {
                    g.setColor(waveform.get()!!.segmentColor(segment, scale.get()))
                    val height = (waveform.get()!!.segmentHeight(segment, scale.get()) * maxHeight) / 31
                    g.drawLine(x, axis - height, x, axis + height)
                }
            }
            if (beatGrid.get() != null) {  // Draw the beat markers
                val inBeat = beatGrid.get()!!.findBeatAtTime(Util.halfFrameToTime(segment.toLong()))
                if ((inBeat > 0) && (inBeat != lastBeat)) {  // Start of a new beat, so prepare to draw it
                    val beatWithinBar = beatGrid.get()!!.getBeatWithinBar(inBeat)
                    if (scale.get() <= MAX_BEAT_SCALE || beatWithinBar == 1) {
                        // Once scale gets large enough, we only draw the downbeats, like CDJs.
                        g.setColor(if ((beatWithinBar == 1)) emphasisColor.get() else indicatorColor.get())
                        g2.stroke = if ((beatWithinBar == 1)) wideStroke else standardStroke
                        g.drawLine(x, axis - maxHeight - 2 - BEAT_MARKER_HEIGHT, x, axis - maxHeight - 2)
                        g.drawLine(x, axis + maxHeight + 2, x, axis + maxHeight + BEAT_MARKER_HEIGHT + 2)
                        g2.stroke = standardStroke
                    }
                    lastBeat = inBeat
                }
            }
        }

        // Draw the cue and memory point markers, first the memory cues and then the hot cues, since some are in
        // the same place, and we want the hot cues to stand out.
        if (currentCueList != null) {
            paintCueList(g, clipRect, currentCueList, axis, maxHeight)
        }

        // Draw the song structure if we have one for the track.
        if (currentSongStructure != null) {
            paintPhrases(g, clipRect, currentSongStructure, axis, maxHeight)
        }

        // Draw the non-playing markers first, so the playing ones will be seen if they are in the same spot.
        g.setColor(Util.buildColor(indicatorColor.get(), PLAYBACK_MARKER_STOPPED))
        for (state in playbackStateMap.values) {
            if (!state.playing) {
                g.fillRect(
                    millisecondsToX(state.position) - (PLAYBACK_MARKER_WIDTH / 2), 0,
                    PLAYBACK_MARKER_WIDTH,
                    height
                )
            }
        }

        // Then draw the playing markers on top of the non-playing ones.
        g.setColor(Util.buildColor(emphasisColor.get(), PLAYBACK_MARKER_PLAYING))
        for (state in playbackStateMap.values) {
            if (state.playing) {
                g.fillRect(
                    millisecondsToX(state.position) - (PLAYBACK_MARKER_WIDTH / 2), 0,
                    PLAYBACK_MARKER_WIDTH,
                    height
                )
            }
        }

        // Finally, if an overlay painter has been attached, let it paint its overlay.
        val painter = overlayPainter.get()
        painter?.paintOverlay(this, g)
    }

    /**
     * Determine the label to display below a cue marker.
     *
     * @param entry the cue list entry which might need labeling
     * @return the text to display, or an empty string if none is needed
     */
    private fun buildCueLabel(entry: CueList.Entry): String {
        if (entry.hotCueNumber > 0) {
            val label = (64 + entry.hotCueNumber).toChar().toString()
            if (entry.comment.isEmpty()) {
                return label
            }
            return label + ": " + entry.comment
        }
        return entry.comment
    }

    /**
     * Draw the visible memory cue points or hot cues.
     *
     * @param g the graphics object in which we are being rendered
     * @param clipRect the region that is being currently rendered
     * @param cueList the cues to  be drawn
     * @param axis the base on which the waveform is being drawn
     * @param maxHeight the highest waveform segment
     */
    private fun paintCueList(g: Graphics, clipRect: Rectangle, cueList: CueList, axis: Int, maxHeight: Int) {
        for (entry in cueList.entries) {
            val x = millisecondsToX(entry.cueTime)
            if ((x > clipRect.x - 4) && (x < clipRect.x + clipRect.width + 4)) {
                g.color = entry.color
                for (i in 0..3) {
                    g.drawLine(
                        x - 3 + i, axis - maxHeight - BEAT_MARKER_HEIGHT - CUE_MARKER_HEIGHT + i,
                        x + 3 - i, axis - maxHeight - BEAT_MARKER_HEIGHT - CUE_MARKER_HEIGHT + i
                    )
                }

                val label = buildCueLabel(entry)
                val font = labelFont.get()
                if (font != null && !label.isEmpty()) {
                    val g2 = g as Graphics2D
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                    g2.font = font
                    val renderContext = g2.fontRenderContext
                    val metrics = g2.font.getLineMetrics(label, renderContext)
                    val bounds = g2.font.getStringBounds(label, renderContext)
                    val textWidth = ceil(bounds.width).toInt()
                    val textHeight =
                        ceil((metrics.ascent + metrics.descent).toDouble()).toInt()
                    g2.fillRect(x, axis - maxHeight - 2, textWidth + 4, textHeight + 2)
                    g2.color = Color.black
                    g2.drawString(label, (x + 2).toFloat(), axis - maxHeight - 1 + metrics.ascent)
                }
            }
        }
    }

    /**
     * Draw the visible phrases if the track has a structure analysis.
     *
     * @param g the graphics object in which we are being rendered
     * @param clipRect the region that is being currently rendered
     * @param songStructure contains the phrases to be drawn
     * @param axis the base on which the waveform is being drawn
     * @param maxHeight the highest waveform segment
     */
    private fun paintPhrases(
        g: Graphics,
        clipRect: Rectangle,
        songStructure: SongStructureTag?,
        axis: Int,
        maxHeight: Int
    ) {
        if (songStructure == null) {
            return
        }

        // Have the phrase labels stick to the left edge of the viewable area as they scroll by.
        // Start by finding our parent scroll pane, if there is one, so we can figure out its horizontal scroll position.
        var scrolledX = 0
        var parent = parent
        while (parent != null) {
            if (parent is JScrollPane) {
                scrolledX = parent.viewport.viewPosition.x
                parent = null // We are done searching for our scroll pane.
            } else {
                parent = parent.parent
            }
        }

        for (i in 0..<songStructure.lenEntries()) {
            val entry = songStructure.body().entries()[i]
            val endBeat =
                if ((i == songStructure.lenEntries() - 1)) songStructure.body().endBeat() else songStructure.body()
                    .entries()[i + 1].beat()
            val x1 = getXForBeat(entry.beat())
            val x2 = getXForBeat(endBeat) - 1
            if ((x1 >= clipRect.x && x1 <= clipRect.x + clipRect.width) || (x2 >= clipRect.x && x2 <= clipRect.x + clipRect.width) ||
                (x1 < clipRect.x && x2 > clipRect.x + clipRect.width)
            ) {  // Is any of this phrase visible?
                g.color =
                    Util.buildColor(Util.phraseColor(entry), PHRASE_TRANSPARENCY) // Render slightly transparently.
                val label = Util.phraseLabel(entry)
                val font = labelFont.get()
                if (font != null && !label.isEmpty()) {
                    val g2 = g as Graphics2D
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                    g2.font = font
                    val renderContext = g2.fontRenderContext
                    val metrics = g2.font.getLineMetrics(label, renderContext)
                    val textHeight =
                        ceil((metrics.ascent + metrics.descent).toDouble()).toInt()

                    val oldClip = g2.clip
                    g2.clipRect(x1, axis + maxHeight + 2 - textHeight, x2 - x1, textHeight + 2)
                    val phraseRect: Rectangle2D = Rectangle2D.Double(
                        x1.toDouble(),
                        (axis + maxHeight + 2 - textHeight).toDouble(),
                        (x2 - x1).toDouble(),
                        (textHeight + 2).toDouble()
                    )
                    g2.fill(phraseRect)
                    g2.color = Util.buildColor(Util.phraseTextColor(entry), PHRASE_TRANSPARENCY)

                    // See if the label for this phrase needs to be adjusted to stay visible as we scroll.
                    var labelX = x1
                    if (scrolledX > labelX) {  // We have scrolled past the start of the phrase.
                        labelX += (scrolledX - labelX) // Nudge the label back into view.
                    }
                    g2.drawString(label, labelX + 2, axis + maxHeight)

                    if (entry.fill() != 0) {  // There is a fill section at the end to draw.
                        val xFill = getXForBeat(entry.beatFill())
                        g2.color = Util.buildColor(Color.white, PHRASE_TRANSPARENCY)
                        val oldStroke = g2.stroke
                        g2.stroke = fillStroke
                        g2.drawLine(xFill, axis + maxHeight - 1, x2, axis + maxHeight - 1)
                        g2.stroke = oldStroke
                    }

                    g2.clip = oldClip
                }
            }
        }
    }


    override fun toString(): String {
        return "WaveformDetailComponent[cueList=" + cueList.get() + ", waveform=" + waveform.get() + ", beatGrid=" +
                beatGrid.get() + ", playbackStateMap=" + playbackStateMap + ", monitoredPlayer=" +
                getMonitoredPlayer() + "fetchSongStructures=" + fetchSongStructures.get() + "]"
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(WaveformDetailComponent::class.java)

        /**
         * How many pixels high are the beat markers.
         */
        private const val BEAT_MARKER_HEIGHT = 4

        /**
         * How many pixels high are the cue markers.
         */
        private const val CUE_MARKER_HEIGHT = 4

        /**
         * How many pixels beyond the waveform the playback indicator extends.
         */
        private const val VERTICAL_MARGIN = 15

        /**
         * How many pixels wide is the current time indicator.
         */
        private const val PLAYBACK_MARKER_WIDTH = 2

        /**
         * The color to draw the playback position when playing; a slightly transparent red. Note that if the indicator
         * color has been changed, only the transparency from this is used.
         *
         * @see .getIndicatorColor
         */
        val PLAYBACK_MARKER_PLAYING: Color = Color(255, 0, 0, 235)

        /**
         * The color to draw the playback position when stopped; a slightly transparent white. Note that if the indicator
         * color has been changed, only the transparency from this is used.
         *
         * @see .getIndicatorColor
         */
        val PLAYBACK_MARKER_STOPPED: Color = Color(255, 255, 255, 235)

        /**
         * The color drawn behind sections of the waveform which represent loops.
         */
        private val LOOP_BACKGROUND = Color(204, 121, 29)

        private val INACTIVE_LOOP_BACKGROUND = Color(80, 80, 80)

        /**
         * The transparency with which phrase bars are drawn.
         */
        private val PHRASE_TRANSPARENCY = Color(255, 255, 255, 220)

        /**
         * The largest scale at which we will draw individual beat markers; above this we show only bars.
         */
        private const val MAX_BEAT_SCALE = 9

        /**
         * Determine the color to use to draw a cue list entry. Hot cues are green, ordinary memory points are red,
         * and loops are orange.
         *
         * @param entry the entry being drawn
         *
         * @return the color with which it should be represented
         *
         */
        @Deprecated("use {@link CueList.Entry#getColor()} instead")
        fun cueColor(entry: CueList.Entry): Color {
            return entry.color
        }

        /**
         * The stroke pattern to use when marking the fill-in section of a phrase.
         */
        private val fillStroke = BasicStroke(
            3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
            0f, floatArrayOf(3f, 3f), 0f
        )
    }
}