@echo off
set javafx-path="C:\Program Files\Java\javafx-sdk-19"
set java-path="C:\Program Files\Java\jdk-19.0.1"
set "py-cmd=py"

set "jar-name=SongGenerator.jar"
set "robocopy-params=/E /njh /njs /ndl /nc /ns >NUL"


@REM Build clean dist directory
if exist dist (	@RD /S /Q dist )
mkdir dist

@REM Build Fat-Jar
echo Building JAR...
call mvn clean package >NUL
copy target\SongGenerator-1.0-SNAPSHOT-jar-with-dependencies.jar dist\%jar-name% >NUL

@REM Build Java Runtime
echo Building Runtime...
robocopy %javafx-path% dist\javafx %robocopy-params%
call jlink --no-header-files --no-man-pages --compress=2 --strip-debug --module-path dist\javafx\lib --add-modules "javafx.base,javafx.controls,javafx.fxml,javafx.graphics" --bind-services --output dist\java
robocopy dist\javafx\bin dist\java\bin %robocopy-params%
@RD /S /Q dist\javafx\bin

@REM Copy resources into the distributable
echo Building Resources...
cd dictionary-creation
call %py-cmd% forbiddenNouns.py
call %py-cmd% generateDict.py
cd ..
robocopy src\main\resources dist\src\main\resources %robocopy-params%
copy DistReadme.txt dist\README.txt >NUL

@REM Write executable into the distributable
echo Writing Batch-Script...
echo java\bin\java.exe --module-path javafx\lib --add-modules javafx.base,javafx.controls,javafx.fxml,javafx.graphics -jar %jar-name% > dist/SongGenerator.bat

echo Done