@REM @RD /S /Q target\java-runtime
@REM @RD /S /Q target\installer

jlink --no-header-files --no-man-pages --compress=2 --strip-debug --module-path "C:\\Program Files\\Java\\javafx-sdk-19\\lib" --add-modules "javafx.base,javafx.controls,javafx.fxml,javafx.graphics" --bind-services --output dist/java

@REM jpackage --dest target/installer --name SongGenerator --main-class org.se.Main --runtime-image target/java-runtime --icon icon.ico --main-jar SongGenerator.jar --input target/lib --type msi --resource-dir src/main/resources --win-console

@REM jpackage --name SongGenerator --input . --main-jar SongGenerator.jar --jlink-options --bind-services --module-path "C:\\Program Files\\Java\\javafx-sdk-19\\lib" --add-modules "javafx.base,javafx.controls,javafx.fxml,javafx.graphics" --icon icon.ico --type msi --dest target/installer --install-dir SongGenerator --win-console

@REM target\installer\SongGenerator-1.0.msi

@REM mkdir "\Program Files\SongGenerator\src\main"
@REM move /Y "\Program Files\SongGenerator\app\src\main\resources" "\Program Files\SongGenerator\src\main\resources"