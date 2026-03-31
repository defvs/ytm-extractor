package youtube

import helpers.parseDateTime
import listenbrainz.ListenBrainzAdditionalInfo
import listenbrainz.ListenBrainzPayload
import listenbrainz.ListenBrainzTrackMetadata
import mapNotNull
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import sanitize
import java.time.Instant
import java.time.format.DateTimeFormatter
import kotlin.streams.toList

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

    fun parse(htmlContent: String, dateTimeFormatter: DateTimeFormatter): List<YouTubeMusicData> {
        val document: Document = Jsoup.parse(htmlContent)
        
        return document.select("div.outer-cell.mdl-cell.mdl-cell--12-col.mdl-shadow--2dp")
            .parallelStream()
            .filter { element ->
                val titleElement = element.selectFirst("p.mdl-typography--title")
                titleElement?.text()?.contains("YouTube Music") ?: false
            }.mapNotNull { element ->
                val contentCells = element.select("div.content-cell.mdl-cell--6-col.mdl-typography--body-1")
                val topElement = when (contentCells.size) {
                    2 -> contentCells[0]
                    6 -> contentCells[0].parent()!!
                    else -> return@mapNotNull null
                }

                val videoElement = runCatching { topElement.child(0) }.onFailure {
                    System.err.println("Failed to parse video element: $it")
                }.getOrNull() ?: return@mapNotNull null
                
                val channelElement = runCatching { topElement.child(2) }.onFailure {
                    System.err.println("Failed to parse channel element: $it")
                }.getOrNull() ?: return@mapNotNull null
                
                val dateTime = runCatching {
                    val sanitizedDateTimeString = topElement.wholeOwnText().sanitize()
                        .split("\n")
                        .last { it.isNotBlank() } // Allow removing "Watched " prefix, support any language
                        .trim()
                    parseDateTime(sanitizedDateTimeString, dateTimeFormatter)
                }.onFailure {
                    System.err.println("Failed to parse date time: $it")
                }.getOrNull() ?: return@mapNotNull null

                val channelName = channelElement.text().sanitize().removeSuffix("- Topic")
                if (channelName.isBlank()) {
                    System.err.println("Channel name is blank for title ${videoElement.text()}")
                    return@mapNotNull null
                }

                return@mapNotNull YouTubeMusicData(
                    title = videoElement.text().sanitize(),
                    videoHref = videoElement.attr("href").trim(),
                    channel = channelName.sanitize(),
                    channelHref = channelElement.attr("href").trim(),
                    dateTime = dateTime
                )
            }.toList()
    }
}