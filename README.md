# YouTube Music to ListenBrainz

Submit your YTM listens from your Google Takeout.

> ### Note:
> 
> This is not fool-proof, some listens are under Various Artists or Release and as such will not be properly matched. However, from my experience, 95-99% of listens get properly matched and a couple are left to do by hand.

> ### Disclaimer:
> 
> * I'm not responsible for messed up histories, unmatched listens, thermonuclear war, or you getting fired because your taste in music is bad.
> * YOU are choosing to use this software, and if you point the finger at me for messing up your ListenBrainz history, I will laugh at you.

## Usage

### 1. Getting the Takeout data

Go through this link, and request your YouTube History: https://takeout.google.com/settings/takeout

You should find the necessary file under `Takeout/YouTube and YouTube Music/history/watch-history.html`

### 2. Getting your ListenBrainz Token

You will need to get your ListenBrainz token through: https://listenbrainz.org/settings/

### 3a. Submitting everything

```shell
java -jar ytm-extractor.jar -i "path/to/watch-history.html" -t "listenbrainz token"
```

### 3b. Submitting after/before (with Unix epoch, in seconds.)

```shell
java -jar ytm-extractor.jar -i "path/to/watch-history.html" -t "listenbrainz token" --before 1722791449 --after 1722532249
```

## Building

```shell
./gradlew shadowJar
```

## License

This software is under the GPLv3 license.