## Movie DB test app

This is a solution for take home test.

You can always get latest version of the app from [here](https://github.com/yaroslavyadrov/movieDb/releases/latest)

## Build instructions:
1. Get API Read access token from [TMDB](https://www.themoviedb.org/settings/api).
2. You will need to create 'secrets.defaults.properties' file in the root of the project with the following content:
```
AUTH_TOKEN="<your eyJhb... token>"
```
3. Use `./gradlew assembleDebug` to build the apk.
4. The result will appear in `app/build/outputs/apk/debug/app-debug.apk`.
