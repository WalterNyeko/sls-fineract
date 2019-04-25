@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  fineract-provider startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

@rem Add default JVM options here. You can also use JAVA_OPTS and FINERACT_PROVIDER_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windowz variants

if not "%OS%" == "Windows_NT" goto win9xME_args
if "%@eval[2+2]" == "4" goto 4NT_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*
goto execute

:4NT_args
@rem Get arguments from the 4NT Shell from JP Software
set CMD_LINE_ARGS=%$

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\fineract-provider.jar;%APP_HOME%\lib\spring-boot-starter-web-1.1.6.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-data-jpa-1.1.6.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-security-1.1.6.RELEASE.jar;%APP_HOME%\lib\spring-context-support-4.0.7.RELEASE.jar;%APP_HOME%\lib\spring-security-oauth2-2.0.4.RELEASE.jar;%APP_HOME%\lib\openjpa-all-2.4.1.jar;%APP_HOME%\lib\jersey-core-1.17.jar;%APP_HOME%\lib\jersey-servlet-1.17.jar;%APP_HOME%\lib\jersey-server-1.17.jar;%APP_HOME%\lib\jersey-json-1.17.jar;%APP_HOME%\lib\jersey-spring-1.17.jar;%APP_HOME%\lib\jersey-multipart-1.17.jar;%APP_HOME%\lib\retrofit-1.6.1.jar;%APP_HOME%\lib\okhttp-2.0.0.jar;%APP_HOME%\lib\okhttp-urlconnection-2.0.0.jar;%APP_HOME%\lib\gson-2.2.4.jar;%APP_HOME%\lib\guava-15.0.jar;%APP_HOME%\lib\commons-email-1.3.3.jar;%APP_HOME%\lib\commons-lang3-3.3.2.jar;%APP_HOME%\lib\drizzle-jdbc-1.3.jar;%APP_HOME%\lib\pentaho-reporting-engine-classic-core-5.4.0.4-149.jar;%APP_HOME%\lib\pentaho-reporting-engine-classic-extensions-5.4.0.4-149.jar;%APP_HOME%\lib\pentaho-reporting-engine-classic-extensions-scripting-5.4.0.4-149.jar;%APP_HOME%\lib\pentaho-reporting-engine-wizard-core-5.4.0.4-149.jar;%APP_HOME%\lib\pentaho-reporting-engine-wizard-xul-5.4.0.4-149.jar;%APP_HOME%\lib\libbase-5.4.0.4-149.jar;%APP_HOME%\lib\libdocbundle-5.4.0.4-149.jar;%APP_HOME%\lib\libfonts-5.4.0.4-149.jar;%APP_HOME%\lib\libformat-5.4.0.4-149.jar;%APP_HOME%\lib\libformula-5.4.0.4-149.jar;%APP_HOME%\lib\libloader-5.4.0.4-149.jar;%APP_HOME%\lib\librepository-5.4.0.4-149.jar;%APP_HOME%\lib\libserializer-5.4.0.4-149.jar;%APP_HOME%\lib\libsparkline-5.4.0.4-149.jar;%APP_HOME%\lib\libxml-5.4.0.4-149.jar;%APP_HOME%\lib\poi-3.15.jar;%APP_HOME%\lib\poi-ooxml-3.15.jar;%APP_HOME%\lib\poi-ooxml-schemas-3.15.jar;%APP_HOME%\lib\dom4j-2.1.0.jar;%APP_HOME%\lib\itext-2.1.7.jar;%APP_HOME%\lib\ical4j-1.0.4.jar;%APP_HOME%\lib\flyway-core-2.1.1.jar;%APP_HOME%\lib\quartz-2.1.7.jar;%APP_HOME%\lib\aws-java-sdk-s3-1.11.80.jar;%APP_HOME%\lib\ehcache-2.7.2.jar;%APP_HOME%\lib\compiler-0.8.12.jar;%APP_HOME%\lib\json-path-0.9.1.jar;%APP_HOME%\lib\tika-core-1.9.jar;%APP_HOME%\lib\spring-jms-4.0.7.RELEASE.jar;%APP_HOME%\lib\activemq-broker-5.9.1.jar;%APP_HOME%\lib\spring-boot-starter-tomcat-1.1.6.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-1.1.6.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-aop-1.1.6.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-jdbc-1.1.6.RELEASE.jar;%APP_HOME%\lib\spring-orm-4.0.7.RELEASE.jar;%APP_HOME%\lib\spring-data-jpa-1.6.4.RELEASE.jar;%APP_HOME%\lib\spring-aspects-4.0.7.RELEASE.jar;%APP_HOME%\lib\spring-security-config-3.2.5.RELEASE.jar;%APP_HOME%\lib\spring-security-web-3.2.5.RELEASE.jar;%APP_HOME%\lib\spring-security-core-3.2.5.RELEASE.jar;%APP_HOME%\lib\jackson-mapper-asl-1.9.13.jar;%APP_HOME%\lib\asm-3.1.jar;%APP_HOME%\lib\jettison-1.1.jar;%APP_HOME%\lib\jackson-jaxrs-1.9.2.jar;%APP_HOME%\lib\jackson-xc-1.9.2.jar;%APP_HOME%\lib\mimepull-1.6.jar;%APP_HOME%\lib\okio-1.0.0.jar;%APP_HOME%\lib\mail-1.4.5.jar;%APP_HOME%\lib\commons-collections4-4.1.jar;%APP_HOME%\lib\curvesapi-1.04.jar;%APP_HOME%\lib\xmlbeans-2.6.0.jar;%APP_HOME%\lib\jaxen-1.1.6.jar;%APP_HOME%\lib\commons-lang-2.6.jar;%APP_HOME%\lib\backport-util-concurrent-3.1.jar;%APP_HOME%\lib\aws-java-sdk-kms-1.11.80.jar;%APP_HOME%\lib\aws-java-sdk-core-1.11.80.jar;%APP_HOME%\lib\jmespath-java-1.11.80.jar;%APP_HOME%\lib\json-smart-1.2.jar;%APP_HOME%\lib\spring-tx-4.0.7.RELEASE.jar;%APP_HOME%\lib\activemq-client-5.9.1.jar;%APP_HOME%\lib\activemq-openwire-legacy-5.9.1.jar;%APP_HOME%\lib\tomcat-embed-core-7.0.55.jar;%APP_HOME%\lib\tomcat-embed-el-7.0.55.jar;%APP_HOME%\lib\tomcat-embed-logging-juli-7.0.55.jar;%APP_HOME%\lib\spring-boot-1.1.6.RELEASE.jar;%APP_HOME%\lib\spring-boot-autoconfigure-1.1.6.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-logging-1.1.6.RELEASE.jar;%APP_HOME%\lib\snakeyaml-1.13.jar;%APP_HOME%\lib\aspectjrt-1.8.2.jar;%APP_HOME%\lib\aspectjweaver-1.8.2.jar;%APP_HOME%\lib\spring-jdbc-4.0.7.RELEASE.jar;%APP_HOME%\lib\tomcat-jdbc-7.0.55.jar;%APP_HOME%\lib\spring-data-commons-1.8.4.RELEASE.jar;%APP_HOME%\lib\aopalliance-1.0.jar;%APP_HOME%\lib\ion-java-1.0.1.jar;%APP_HOME%\lib\jackson-dataformat-cbor-2.6.6.jar;%APP_HOME%\lib\geronimo-jms_1.1_spec-1.1.1.jar;%APP_HOME%\lib\hawtbuf-1.9.jar;%APP_HOME%\lib\geronimo-j2ee-management_1.1_spec-1.0.1.jar;%APP_HOME%\lib\log4j-over-slf4j-1.7.7.jar;%APP_HOME%\lib\logback-classic-1.1.2.jar;%APP_HOME%\lib\tomcat-juli-7.0.55.jar;%APP_HOME%\lib\logback-core-1.1.2.jar;%APP_HOME%\lib\spring-beans-4.0.8.RELEASE.jar;%APP_HOME%\lib\spring-core-4.0.8.RELEASE.jar;%APP_HOME%\lib\spring-context-4.0.8.RELEASE.jar;%APP_HOME%\lib\spring-webmvc-4.0.8.RELEASE.jar;%APP_HOME%\lib\spring-web-4.0.8.RELEASE.jar;%APP_HOME%\lib\spring-aop-4.0.8.RELEASE.jar;%APP_HOME%\lib\commons-codec-1.10.jar;%APP_HOME%\lib\slf4j-api-1.7.7.jar;%APP_HOME%\lib\spring-expression-4.0.8.RELEASE.jar;%APP_HOME%\lib\httpclient-4.5.2.jar;%APP_HOME%\lib\jackson-databind-2.6.6.jar;%APP_HOME%\lib\joda-time-2.8.1.jar;%APP_HOME%\lib\jackson-core-2.6.6.jar;%APP_HOME%\lib\jackson-core-asl-1.9.13.jar;%APP_HOME%\lib\httpcore-4.4.4.jar;%APP_HOME%\lib\jackson-annotations-2.6.0.jar

@rem Execute fineract-provider
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %FINERACT_PROVIDER_OPTS%  -classpath "%CLASSPATH%" org.apache.fineract.ServerWithMariaDB4jApplication %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable FINERACT_PROVIDER_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%FINERACT_PROVIDER_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
