# BAAAHS Simulator 2

## Running from source

* Open as a gradle project with [IntelliJ](https://www.jetbrains.com/idea/download/)
* Open `src/jsMain/resources/index.html` using "Open in Browser -> Chrome" from IntelliJ context menu

## Auto build

```sh
brew install fswatch
fswatch src --batch-marker=BOOM --exclude=___jb | grep --line-buffered BOOM | xargs -n1 -I{} ./gradlew -i jsJar
```
