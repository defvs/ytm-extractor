package helpers

import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun parseDateTime(dateTimeString: String): Instant {
    val cleanedDateTimeString =
        dateTimeString.replace('\u00A0', ' ') // Replace non-breaking spaces with regular spaces
            .replace('\u202F', ' ') // Replace non-breaking spaces with regular spaces
    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy, h:mm:ss a z", Locale.US)
    return ZonedDateTime.parse(cleanedDateTimeString, formatter).toInstant()
}