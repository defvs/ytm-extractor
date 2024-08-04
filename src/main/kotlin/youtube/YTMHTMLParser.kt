package youtube

import helpers.parseDateTime
import listenbrainz.ListenBrainzAdditionalInfo
import listenbrainz.ListenBrainzPayload
import listenbrainz.ListenBrainzTrackMetadata
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.time.Instant

object YTMHTMLParser {
    data class YouTubeMusicData(
        val title: String,
        val videoHref: String,
        val channel: String,
        val channelHref: String,
        val dateTime: Instant,
    ) {
        fun toListenBrainz() = ListenBrainzPayload(
            listenedAt = dateTime,
            trackMetadata = ListenBrainzTrackMetadata(
                artistName = channel,
                trackName = title,
                additionalInfo = ListenBrainzAdditionalInfo(
                    originUrl = videoHref,
                )
            )
        )
    }

    fun parse(htmlContent: String): List<YouTubeMusicData> {
        val document: Document = Jsoup.parse(htmlContent)
        val youtubeMusicDataList = mutableListOf<YouTubeMusicData>()

        val elements = document.select("div.outer-cell.mdl-cell.mdl-cell--12-col.mdl-shadow--2dp")
        for (element in elements) {
            val titleElement = element.selectFirst("p.mdl-typography--title")
            if (titleElement != null && titleElement.text().contains("YouTube Music")) {
                val contentCells = element.select("div.content-cell.mdl-cell--6-col.mdl-typography--body-1")
                val topElement =
                    if (contentCells.size == 2) contentCells[0]
                    else if (contentCells.size == 6) contentCells[0].parent()!!
                    else continue

                val videoElement = runCatching { topElement.child(0) }.getOrNull() ?: continue
                val channelElement = runCatching { topElement.child(2) }.getOrNull() ?: continue
                val dateTime = runCatching {
                    parseDateTime(
                        topElement.ownText().trim().removePrefix("Watched ").trim()
                    )
                }.getOrNull() ?: continue

                val channelName = channelElement.text().trim().removeSuffix("- Topic")
                val youtubeMusicData = YouTubeMusicData(
                    title = videoElement.text().trim(),
                    videoHref = videoElement.attr("href").trim(),
                    channel = channelName.trim(),
                    channelHref = channelElement.attr("href").trim(),
                    dateTime = dateTime
                )
                youtubeMusicDataList.add(youtubeMusicData)
            }
        }
        return youtubeMusicDataList
    }
}