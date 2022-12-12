@echo off

if not exist dist mkdir dist
if exist SongGenerator.jar copy SongGenerator.jar dist\SongGenerator.jar >NUL
if not exist dist\SongGenerator.jar echo "Missing jar-file. Make sure you have the jar-file either in the current directory or in the dist directory."

@REM Build Java Runtime
if exist dist\java @RD /S /Q dist\java
jlink --no-header-files --no-man-pages --compress=2 --strip-debug --module-path "C:\\Program Files\\Java\\javafx-sdk-19\\lib" --add-modules "javafx.base,javafx.controls,javafx.fxml,javafx.graphics" --bind-services --output dist\java

@REM Copy resources into the distributable
if exist dist\src @RD /S /Q dist\src
robocopy src\main\resources dist\src\main\resources /E /njh /njs /ndl /nc /ns >NUL

@REM Write executable into the distributable
echo java\bin\java.exe --add-modules "javafx.base,javafx.controls,javafx.fxml,javafx.graphics" -jar SongGenerator.jar > dist/SongGenerator.bat
