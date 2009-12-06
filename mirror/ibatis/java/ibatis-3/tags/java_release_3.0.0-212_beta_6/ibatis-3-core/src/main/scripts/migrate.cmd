@ECHO off

set WORKING_DIR=%~dp0
set LIB=%WORKING_DIR%lib\
for /F %%a in ('dir %LIB% /a /b /-p /o') do set MIGRATOR_CP=%LIB%%%a

java -cp %MIGRATOR_CP% org.apache.ibatis.migration.Migrator %*
