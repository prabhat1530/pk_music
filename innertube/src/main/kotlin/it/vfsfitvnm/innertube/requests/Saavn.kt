package it.vfsfitvnm.innertube.requests

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import it.vfsfitvnm.innertube.Innertube
import it.vfsfitvnm.innertube.utils.runCatchingNonCancellable
import kotlinx.serialization.Serializable

@Serializable
data class SaavnSong(
    val id: String,
    val name: String,
    val downloadUrl: List<DownloadUrl>? = null
) {
    @Serializable
    data class DownloadUrl(
        val quality: String,
        val link: String
    )
}

@Serializable
data class SaavnSearchResponse(
    val status: String,
    val data: SaavnSearchData? = null
) {
    @Serializable
    data class SaavnSearchData(
        val results: List<SaavnSong>? = null
    )
}

@Serializable
data class SaavnSongResponse(
    val status: String,
    val data: List<SaavnSong>? = null
)

suspend fun Innertube.saavnSearch(query: String, fallbackQuery: String? = null): Result<String?>? = runCatchingNonCancellable {
    val searchResponse = try {
        client.get {
            url("https://jiosaavn-api-privatecvc2.vercel.app/search/songs")
            parameter("query", query)
        }.body<SaavnSearchResponse>()
    } catch (e: Exception) {
        null
    }

    var songId = searchResponse?.data?.results?.firstOrNull()?.id

    // Fallback: If title+artist failed, try fallbackQuery
    if (songId == null && fallbackQuery != null) {
        val fallbackResponse = try {
            client.get {
                url("https://jiosaavn-api-privatecvc2.vercel.app/search/songs")
                parameter("query", fallbackQuery)
            }.body<SaavnSearchResponse>()
        } catch (e: Exception) {
            null
        }
        songId = fallbackResponse?.data?.results?.firstOrNull()?.id
    }

    if (songId == null) return@runCatchingNonCancellable null

    val songResponse = try {
        client.get {
            url("https://jiosaavn-api-privatecvc2.vercel.app/songs")
            parameter("id", songId)
        }.body<SaavnSongResponse>()
    } catch (e: Exception) {
        null
    }

    songResponse?.data?.firstOrNull()?.downloadUrl?.lastOrNull()?.link
}
