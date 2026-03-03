package it.vfsfitvnm.vimusic.ui.screens.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import java.util.Calendar
import it.vfsfitvnm.compose.persist.persist
import it.vfsfitvnm.innertube.Innertube
import it.vfsfitvnm.innertube.models.NavigationEndpoint
import it.vfsfitvnm.innertube.models.bodies.NextBody
import it.vfsfitvnm.innertube.requests.relatedPage
import it.vfsfitvnm.vimusic.Database
import it.vfsfitvnm.vimusic.LocalPlayerAwareWindowInsets
import it.vfsfitvnm.vimusic.LocalPlayerServiceBinder
import it.vfsfitvnm.vimusic.R
import it.vfsfitvnm.vimusic.models.Song
import it.vfsfitvnm.vimusic.query
import it.vfsfitvnm.vimusic.ui.components.LocalMenuState
import it.vfsfitvnm.vimusic.ui.components.ShimmerHost
import it.vfsfitvnm.vimusic.ui.components.themed.FloatingActionsContainerWithScrollToTop
import it.vfsfitvnm.vimusic.ui.components.themed.Header
import it.vfsfitvnm.vimusic.ui.components.themed.NonQueuedMediaItemMenu
import it.vfsfitvnm.vimusic.ui.components.themed.TextPlaceholder
import it.vfsfitvnm.vimusic.ui.items.AlbumItem
import it.vfsfitvnm.vimusic.ui.items.AlbumItemPlaceholder
import it.vfsfitvnm.vimusic.ui.items.ArtistItem
import it.vfsfitvnm.vimusic.ui.items.ArtistItemPlaceholder
import it.vfsfitvnm.vimusic.ui.items.PlaylistItem
import it.vfsfitvnm.vimusic.ui.items.PlaylistItemPlaceholder
import it.vfsfitvnm.vimusic.ui.items.SongItem
import it.vfsfitvnm.vimusic.ui.items.SongItemPlaceholder
import it.vfsfitvnm.vimusic.ui.styling.Dimensions
import it.vfsfitvnm.vimusic.ui.styling.LocalAppearance
import it.vfsfitvnm.vimusic.ui.styling.PKMusicGradientColors
import it.vfsfitvnm.vimusic.ui.styling.px
import it.vfsfitvnm.vimusic.utils.SnapLayoutInfoProvider
import it.vfsfitvnm.vimusic.utils.asMediaItem
import it.vfsfitvnm.vimusic.utils.center
import it.vfsfitvnm.vimusic.utils.forcePlay
import it.vfsfitvnm.vimusic.utils.bold
import it.vfsfitvnm.vimusic.utils.isLandscape
import it.vfsfitvnm.vimusic.utils.secondary
import it.vfsfitvnm.vimusic.utils.semiBold
import kotlinx.coroutines.flow.distinctUntilChanged

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun QuickPicks(
    onAlbumClick: (String) -> Unit,
    onArtistClick: (String) -> Unit,
    onPlaylistClick: (String) -> Unit,
    onSearchClick: () -> Unit,
) {
    val (colorPalette, typography) = LocalAppearance.current
    val binder = LocalPlayerServiceBinder.current
    val menuState = LocalMenuState.current
    val windowInsets = LocalPlayerAwareWindowInsets.current

    var trending by persist<Song?>("home/trending")

    var relatedPageResult by persist<Result<Innertube.RelatedPage?>?>(tag = "home/relatedPageResult")

    LaunchedEffect(Unit) {
        Database.trending().distinctUntilChanged().collect { song ->
            if ((song == null && relatedPageResult == null) || trending?.id != song?.id) {
                relatedPageResult =
                    Innertube.relatedPage(NextBody(videoId = (song?.id ?: "J7p4bzqLvCw")))
            }
            trending = song
        }
    }

    val songThumbnailSizeDp = Dimensions.thumbnails.song
    val songThumbnailSizePx = songThumbnailSizeDp.px
    val albumThumbnailSizeDp = 108.dp
    val albumThumbnailSizePx = albumThumbnailSizeDp.px
    val artistThumbnailSizeDp = 92.dp
    val artistThumbnailSizePx = artistThumbnailSizeDp.px
    val playlistThumbnailSizeDp = 108.dp
    val playlistThumbnailSizePx = playlistThumbnailSizeDp.px

    val scrollState = rememberScrollState()
    val quickPicksLazyGridState = rememberLazyGridState()

    val endPaddingValues = windowInsets.only(WindowInsetsSides.End).asPaddingValues()

    val sectionTextModifier = Modifier
        .padding(horizontal = 16.dp)
        .padding(top = 24.dp, bottom = 8.dp)
        .padding(endPaddingValues)

    BoxWithConstraints {
        val quickPicksLazyGridItemWidthFactor = if (isLandscape && maxWidth * 0.475f >= 320.dp) {
            0.475f
        } else {
            0.9f
        }

        val snapLayoutInfoProvider = remember(quickPicksLazyGridState) {
            SnapLayoutInfoProvider(
                lazyGridState = quickPicksLazyGridState,
                positionInLayout = { layoutSize, itemSize ->
                    (layoutSize * quickPicksLazyGridItemWidthFactor / 2f - itemSize / 2f)
                }
            )
        }

        val itemInHorizontalGridWidth = maxWidth * quickPicksLazyGridItemWidthFactor

        Column(
            modifier = Modifier
                .background(colorPalette.background0)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(
                    windowInsets
                        .only(WindowInsetsSides.Vertical)
                        .asPaddingValues()
                )
        ) {
            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val greeting = when {
                currentHour < 12 -> "Good Morning"
                currentHour < 18 -> "Good Afternoon"
                else -> "Good Evening"
            }

            // --- EXACT UI: Top Bar (Logo, Profile, Bell) ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .padding(endPaddingValues)
            ) {
                // Logo & Name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.sparkles), // Using sparkles as dummy logo icon
                        contentDescription = "Logo",
                        colorFilter = ColorFilter.tint(colorPalette.accent),
                        modifier = Modifier.size(24.dp)
                    )
                    BasicText(
                        text = "MusicHub",
                        style = typography.l.bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                // Profile and Notification
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(colorPalette.background1)) {
                        // Dummy profile pic placeholder
                        Image(painterResource(R.drawable.person), null, modifier = Modifier.align(Alignment.Center).size(20.dp), colorFilter = ColorFilter.tint(colorPalette.text))
                    }
                    Box(modifier = Modifier.size(24.dp)) {
                        Image(painterResource(R.drawable.notifications), null, modifier = Modifier.align(Alignment.Center).size(24.dp), colorFilter = ColorFilter.tint(colorPalette.text))
                        // Red dot (using accent)
                        Box(modifier = Modifier.align(Alignment.TopEnd).size(8.dp).clip(CircleShape).background(colorPalette.accent))
                    }
                }
            }

            // --- EXACT UI: Search Pill ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(endPaddingValues)
                    .clip(RoundedCornerShape(24.dp))
                    .background(colorPalette.background1)
                    .clickable(onClick = onSearchClick)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.search),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(colorPalette.textDisabled),
                    modifier = Modifier.size(20.dp)
                )
                BasicText(
                    text = "Search for songs, artists...",
                    style = typography.s.secondary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // --- EXACT UI: Greeting ---
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 24.dp, bottom = 24.dp)
                    .padding(endPaddingValues)
            ) {

                BasicText(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("$greeting, ")
                        }
                        withStyle(style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = colorPalette.accent
                        )) {
                            append("Prabhat!")
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(" \uD83D\uDC4B") // Waving hand emoji
                        }
                    },
                    style = typography.xxl
                )
                BasicText(
                    text = "Let's play some music \uD83C\uDFB5",
                    style = typography.s.secondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            relatedPageResult?.getOrNull()?.let { related ->
                // --- EXACT UI: Filter Pills ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(Brush.horizontalGradient(PKMusicGradientColors))
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(painterResource(R.drawable.musical_notes), null, Modifier.size(16.dp), colorFilter = ColorFilter.tint(Color.White))
                        BasicText("Music", style = typography.s.semiBold.copy(color = Color.White))
                    }
                    
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(colorPalette.background1)
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(painterResource(R.drawable.information), null, Modifier.size(16.dp), colorFilter = ColorFilter.tint(colorPalette.textDisabled))
                        BasicText("Podcasts", style = typography.s.semiBold.copy(color = colorPalette.textDisabled))
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(colorPalette.background1)
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(painterResource(R.drawable.radio), null, Modifier.size(16.dp), colorFilter = ColorFilter.tint(colorPalette.textDisabled))
                        BasicText("Radio", style = typography.s.semiBold.copy(color = colorPalette.textDisabled))
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp, bottom = 8.dp)
                        .padding(endPaddingValues),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicText(
                        text = "Recommended For You",
                        style = typography.m.semiBold
                    )
                }

                val recommendedThumbnailSizeDp = 160.dp
                val recommendedThumbnailSizePx = recommendedThumbnailSizeDp.px

                LazyRow(
                    contentPadding = endPaddingValues,
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    trending?.let { song ->
                        item {
                            SongItem(
                                song = song,
                                thumbnailSizePx = recommendedThumbnailSizePx,
                                thumbnailSizeDp = recommendedThumbnailSizeDp,
                                alternative = true,
                                trailingContent = {
                                    Image(
                                        painter = painterResource(R.drawable.star),
                                        contentDescription = null,
                                        colorFilter = ColorFilter.tint(colorPalette.accent),
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                modifier = Modifier
                                    .combinedClickable(
                                        onLongClick = {
                                            menuState.display {
                                                NonQueuedMediaItemMenu(
                                                    onDismiss = menuState::hide,
                                                    mediaItem = song.asMediaItem,
                                                    onRemoveFromQuickPicks = {
                                                        query {
                                                            Database.clearEventsFor(song.id)
                                                        }
                                                    }
                                                )
                                            }
                                        },
                                        onClick = {
                                            val mediaItem = song.asMediaItem
                                            binder?.stopRadio()
                                            binder?.player?.forcePlay(mediaItem)
                                            binder?.setupRadio(
                                                NavigationEndpoint.Endpoint.Watch(videoId = mediaItem.mediaId)
                                            )
                                        }
                                    )
                                    .animateItemPlacement()
                            )
                        }
                    }

                    items(
                        items = related.songs?.dropLast(if (trending == null) 0 else 1) ?: emptyList(),
                        key = Innertube.SongItem::key
                    ) { song ->
                        SongItem(
                            song = song,
                            thumbnailSizePx = recommendedThumbnailSizePx,
                            thumbnailSizeDp = recommendedThumbnailSizeDp,
                            alternative = true,
                            modifier = Modifier
                                .combinedClickable(
                                    onLongClick = {
                                        menuState.display {
                                            NonQueuedMediaItemMenu(
                                                onDismiss = menuState::hide,
                                                mediaItem = song.asMediaItem
                                            )
                                        }
                                    },
                                    onClick = {
                                        val mediaItem = song.asMediaItem
                                        binder?.stopRadio()
                                        binder?.player?.forcePlay(mediaItem)
                                        binder?.setupRadio(
                                            NavigationEndpoint.Endpoint.Watch(videoId = mediaItem.mediaId)
                                        )
                                    }
                                )
                                .animateItemPlacement()
                        )
                    }
                }

                related.albums?.let { albums ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 24.dp, bottom = 8.dp)
                            .padding(endPaddingValues),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicText(
                            text = "Recently Played",
                            style = typography.m.semiBold
                        )
                        BasicText(
                            text = "See All",
                            style = typography.xs.semiBold.copy(color = colorPalette.accent)
                        )
                    }

                    LazyRow(contentPadding = endPaddingValues) {
                        items(
                            items = albums,
                            key = Innertube.AlbumItem::key
                        ) { album ->
                            AlbumItem(
                                album = album,
                                thumbnailSizePx = albumThumbnailSizePx,
                                thumbnailSizeDp = albumThumbnailSizeDp,
                                alternative = true,
                                modifier = Modifier
                                    .clickable(onClick = { onAlbumClick(album.key) })
                            )
                        }
                    }
                }

                related.artists?.let { artists ->
                    BasicText(
                        text = "Similar artists",
                        style = typography.m.semiBold,
                        modifier = sectionTextModifier
                    )

                    LazyRow(contentPadding = endPaddingValues) {
                        items(
                            items = artists,
                            key = Innertube.ArtistItem::key,
                        ) { artist ->
                            ArtistItem(
                                artist = artist,
                                thumbnailSizePx = artistThumbnailSizePx,
                                thumbnailSizeDp = artistThumbnailSizeDp,
                                alternative = true,
                                modifier = Modifier
                                    .clickable(onClick = { onArtistClick(artist.key) })
                            )
                        }
                    }
                }

                related.playlists?.let { playlists ->
                    BasicText(
                        text = "Playlists you might like",
                        style = typography.m.semiBold,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 24.dp, bottom = 8.dp)
                    )

                    LazyRow(contentPadding = endPaddingValues) {
                        items(
                            items = playlists,
                            key = Innertube.PlaylistItem::key,
                        ) { playlist ->
                            PlaylistItem(
                                playlist = playlist,
                                thumbnailSizePx = playlistThumbnailSizePx,
                                thumbnailSizeDp = playlistThumbnailSizeDp,
                                alternative = true,
                                modifier = Modifier
                                    .clickable(onClick = { onPlaylistClick(playlist.key) })
                            )
                        }
                    }
                }

                Unit
            } ?: relatedPageResult?.exceptionOrNull()?.let {
                BasicText(
                    text = "An error has occurred",
                    style = typography.s.secondary.center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(all = 16.dp)
                )
            } ?: ShimmerHost {
                repeat(4) {
                    SongItemPlaceholder(
                        thumbnailSizeDp = songThumbnailSizeDp,
                    )
                }

                TextPlaceholder(modifier = sectionTextModifier)

                Row {
                    repeat(2) {
                        AlbumItemPlaceholder(
                            thumbnailSizeDp = albumThumbnailSizeDp,
                            alternative = true
                        )
                    }
                }

                TextPlaceholder(modifier = sectionTextModifier)

                Row {
                    repeat(2) {
                        ArtistItemPlaceholder(
                            thumbnailSizeDp = albumThumbnailSizeDp,
                            alternative = true
                        )
                    }
                }

                TextPlaceholder(modifier = sectionTextModifier)

                Row {
                    repeat(2) {
                        PlaylistItemPlaceholder(
                            thumbnailSizeDp = albumThumbnailSizeDp,
                            alternative = true
                        )
                    }
                }
            }
        }
    }
}
