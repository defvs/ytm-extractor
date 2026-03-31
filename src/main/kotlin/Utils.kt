fun String.sanitize(): String = this
    .replace('\u00A0', ' ') // Replace non-breaking spaces with regular spaces
    .replace('\u202F', ' ') // Replace non-breaking spaces with regular spaces
    .replace('‬', ' ')
    .trim()