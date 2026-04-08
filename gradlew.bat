@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  Gradle startup script for Windows
@rem
@rem ##########################################################################

@set DIRNAME=%~dp0
@if "%DIRNAME%"=="" set DIRNAME=.
@set APP_BASE_NAME=%~n0
@set APP_HOME=%DIRNAME%

@for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m" "--enable-native-access=ALL-UNNAMED"

@set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

@set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% neq 0 goto execute

:execute
%JAVA_EXE% %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
