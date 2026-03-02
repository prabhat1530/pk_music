package it.vfsfitvnm.innertube.models

import kotlinx.serialization.Serializable

@Serializable
data class Context(
    val client: Client,
    val thirdParty: ThirdParty? = null,
) {
    @Serializable
    data class Client(
        val clientName: String,
        val clientVersion: String,
        val platform: String,
        val hl: String = "en",
        val visitorData: String = "CgtEUlRINDFjdm1YayjX1pSaBg%3D%3D",
        val androidSdkVersion: Int? = null,
        val userAgent: String? = null
    )

    @Serializable
    data class ThirdParty(
        val embedUrl: String,
    )

    companion object {
        val DefaultWeb = Context(
            client = Client(
                clientName = "WEB_REMIX",
                clientVersion = "1.20241127.01.00",
                platform = "DESKTOP",
            )
        )

        val DefaultAndroid = Context(
            client = Client(
                clientName = "ANDROID_MUSIC",
                clientVersion = "6.41.52",
                platform = "MOBILE",
                androidSdkVersion = 30,
                userAgent = "com.google.android.apps.youtube.music/6.41.52 (Linux; U; Android 11) gzip"
            )
        )

        val DefaultAgeRestrictionBypass = Context(
            client = Client(
                clientName = "IOS",
                clientVersion = "19.28.1",
                platform = "MOBILE",
                userAgent = "com.google.ios.youtube/19.28.1 (iPhone14,3; U; CPU iOS 15_6 like Mac OS X) AppleWebKit/8613.3.9.0.1 (KHTML, like Gecko) Mobile/19G71"
            )
        )
    }
}
