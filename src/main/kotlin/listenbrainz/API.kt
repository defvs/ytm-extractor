package listenbrainz

import helpers.InstantAsUnixSecondsSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class ListenBrainzListens(
    val listenType: String = "import",
    val payload: List<ListenBrainzPayload>,
)

@Serializable
data class ListenBrainzPayload(
    @Serializable(with = InstantAsUnixSecondsSerializer::class) val listenedAt: Instant,
    val trackMetadata: ListenBrainzTrackMetadata,
)

@Serializable
data class ListenBrainzTrackMetadata(
    val artistName: String,
    val trackName: String,
    val additionalInfo: ListenBrainzAdditionalInfo,
)

@Serializable
data class ListenBrainzAdditionalInfo(
    val originUrl: String,
    val musicService: String = "music.youtube.com",
    val submissionClient: String = "defvs/YTM-Extractor",
)

