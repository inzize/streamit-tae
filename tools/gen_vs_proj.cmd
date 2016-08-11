@echo off

set CMAKE=%COREVA_STREAMIT%\tools\cmake-2.8.11.2-win32-x86\bin\cmake

:: set name of current program
for %%* in (.) do set PROG=%%~n*

:: create files in build_vs
mkdir build_vs & pushd build_vs 

:: generate VS project
xcopy /Y /Q "%COREVA_STREAMIT%\tools\CMakeLists.txt" . > nul
if defined VS120COMNTOOLS (
  %CMAKE% -G"Visual Studio 12"
) else (
  if defined VS110COMNTOOLS (
    %CMAKE% -G"Visual Studio 11"
  ) else (
    if defined VS100COMNTOOLS (
	  %CMAKE% -G"Visual Studio 10"
    )
  )
)

:: launch VS
if defined VS120COMNTOOLS (
  "%VS120COMNTOOLS%/vsvars32.bat"
) else (
  if defined VS110COMNTOOLS (
    "%VS110COMNTOOLS%/vsvars32.bat"
  ) else (
    if defined VS100COMNTOOLS (
	  "%VS100COMNTOOLS%/vsvars32.bat"
    )
  )
)

devenv "%PROG%.sln"

popd