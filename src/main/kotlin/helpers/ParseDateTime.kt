package helpers

import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun parseDateTime(dateTimeString: String, dateTimeFormatter: DateTimeFormatter): Instant {
    return ZonedDateTime.parse(dateTimeString, dateTimeFormatter).toInstant()
}