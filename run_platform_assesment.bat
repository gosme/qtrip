@echo off
setlocal

set "FILE=test-results.xml"
set "SOURCE_XML=app\tmp\external_build\reports\tests\test\testng-results.xml"

if exist "%FILE%" del /f /q "%FILE%"

if not exist "%SOURCE_XML%" (
    echo Unable to locate xml path: %SOURCE_XML%
    exit /b 1
)

copy /y "%SOURCE_XML%" "%FILE%" >nul
python assessment\xmlAssesment.py assessment\final_assessment_instructions.json "%FILE%"
