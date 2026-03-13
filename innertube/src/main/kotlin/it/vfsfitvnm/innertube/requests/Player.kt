package it.vfsfitvnm.innertube.requests

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import it.vfsfitvnm.innertube.Innertube
import it.vfsfitvnm.innertube.models.Context
import it.vfsfitvnm.innertube.models.PlayerResponse
import it.vfsfitvnm.innertube.models.bodies.PlayerBody
import it.vfsfitvnm.innertube.utils.runCatchingNonCancellable
import kotlinx.serialization.Serializable

suspend fun Innertube.player(body: PlayerBody) = runCatchingNonCancellable {
    val response = try {
        client.post(player) {
            setBody(body)
            mask("playabilityStatus.status,playerConfig.audioConfig,streamingData.adaptiveFormats,videoDetails.videoId")
        }.body<PlayerResponse>()
    } catch (e: Exception) {
        return@runCatchingNonCancellable PlayerResponse(
            playabilityStatus = PlayerResponse.PlayabilityStatus("LOGIN_REQUIRED"),
            playerConfig = null,
            streamingData = null,
            videoDetails = null
        )
    }

    if (response.playabilityStatus?.status == "OK") {
        response
    } else {
        val safePlayerResponse = try {
            client.post(player) {
                setBody(
                    body.copy(
                        context = Context.DefaultAgeRestrictionBypass.copy(
                            thirdParty = Context.ThirdParty(
                                embedUrl = "https://www.youtube.com/watch?v=${body.videoId}"
                            )
                        ),
                    )
                )
                mask("playabilityStatus.status,playerConfig.audioConfig,streamingData.adaptiveFormats,videoDetails.videoId")
            }.body<PlayerResponse>()
        } catch (e: Exception) {
            return@runCatchingNonCancellable response
        }

        if (safePlayerResponse.playabilityStatus?.status != "OK") {
            return@runCatchingNonCancellable response
        }

        safePlayerResponse
    }
}
