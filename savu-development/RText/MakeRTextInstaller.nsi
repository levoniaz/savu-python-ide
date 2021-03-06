; Script generated by the HM NIS Edit Script Wizard.

; HM NIS Edit Wizard helper defines
!define PRODUCT_NAME "Savu"
!define PRODUCT_VERSION "2.0.7"
!define PRODUCT_WEB_SITE "http://fifesoft.com/rtext/"
!define PRODUCT_DIR_REGKEY "Software\Microsoft\Windows\CurrentVersion\App Paths\Savu.exe"
!define PRODUCT_UNINST_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"
!define PRODUCT_UNINST_ROOT_KEY "HKLM"

; MUI 1.67 compatible ------
!include "MUI.nsh"

; MUI Settings
!define MUI_ABORTWARNING
!define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\modern-install.ico"
!define MUI_UNICON "${NSISDIR}\Contrib\Graphics\Icons\modern-uninstall.ico"

; Welcome page
!insertmacro MUI_PAGE_WELCOME
; License page
!insertmacro MUI_PAGE_LICENSE "extra\License.txt"
; Directory page
!insertmacro MUI_PAGE_DIRECTORY
; Instfiles page
!insertmacro MUI_PAGE_INSTFILES
; Finish page
!define MUI_FINISHPAGE_RUN "$INSTDIR\Savu.exe"
!define MUI_FINISHPAGE_SHOWREADME "$INSTDIR\Readme.txt"
!insertmacro MUI_PAGE_FINISH

; Uninstaller pages
!insertmacro MUI_UNPAGE_INSTFILES

; Language files
!insertmacro MUI_LANGUAGE "English"

; MUI end ------

Name "${PRODUCT_NAME} ${PRODUCT_VERSION}"
OutFile "savu_${PRODUCT_VERSION}_win32_setup.exe"
InstallDir "$PROGRAMFILES\Savu"
InstallDirRegKey HKLM "${PRODUCT_DIR_REGKEY}" ""
ShowInstDetails show
ShowUnInstDetails show

Section "MainSection" SEC01
  WriteRegStr HKLM "Software\Savu" "Location" "$INSTDIR"
  SetOutPath "$INSTDIR"
  SetOverwrite on
  File "Savu.exe"
  CreateDirectory "$SMPROGRAMS\Savu"
  CreateShortCut "$SMPROGRAMS\Savu\Savu.lnk" "$INSTDIR\Savu.exe"
  CreateShortCut "$DESKTOP\Savu.lnk" "$INSTDIR\Savu.exe"
  SetOverwrite on
  SetOutPath "$INSTDIR"
  File /r "dist\*"
SectionEnd

Section -AdditionalIcons
  WriteIniStr "$INSTDIR\${PRODUCT_NAME}.url" "InternetShortcut" "URL" "${PRODUCT_WEB_SITE}"
  CreateShortCut "$SMPROGRAMS\Savu\Website.lnk" "$INSTDIR\${PRODUCT_NAME}.url"
  CreateShortCut "$SMPROGRAMS\Savu\Uninstall.lnk" "$INSTDIR\uninst.exe"
SectionEnd

Section -Post
  WriteUninstaller "$INSTDIR\uninst.exe"
  WriteRegStr HKLM "${PRODUCT_DIR_REGKEY}" "" "$INSTDIR\Savu.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayName" "$(^Name)"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "UninstallString" "$INSTDIR\uninst.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayIcon" "$INSTDIR\Savu.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayVersion" "${PRODUCT_VERSION}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "URLInfoAbout" "${PRODUCT_WEB_SITE}"
SectionEnd


Function un.onUninstSuccess
  HideWindow
  MessageBox MB_ICONINFORMATION|MB_OK "$(^Name) was successfully removed from your computer."
FunctionEnd

Function un.onInit
  MessageBox MB_ICONQUESTION|MB_YESNO|MB_DEFBUTTON2 "Are you sure you want to completely remove $(^Name) and all of its components?" IDYES +2
  Abort
FunctionEnd

Section Uninstall
  ; Don't just Delete /r $INSTDIR, preserve files they added themselves
  SetOutPath "$INSTDIR"
  Delete "$INSTDIR\Savu.exe"
  Delete "$INSTDIR\RText.url"
  Delete "$INSTDIR\uninst.exe"
  Delete /REBOOTOK "$INSTDIR\autocomplete.jar"
  Delete /REBOOTOK "$INSTDIR\AutoComplete.License.txt"
  Delete /REBOOTOK "$INSTDIR\english_dic.zip"
  Delete /REBOOTOK "$INSTDIR\ExtraFileChooserFilters.xml"
  Delete /REBOOTOK "$INSTDIR\fife.common.jar"
  Delete /REBOOTOK "$INSTDIR\Jazzy.LICENSE.txt"
  Delete /REBOOTOK "$INSTDIR\js-14*.jar" ; Shouldn't be there, but was put by older releases
  Delete /REBOOTOK "$INSTDIR\License.txt"
  Delete /REBOOTOK "$INSTDIR\localizations.xml"
  Delete /REBOOTOK "$INSTDIR\README.jazzy.txt"
  Delete /REBOOTOK "$INSTDIR\README.spellchecker.txt"
  Delete /REBOOTOK "$INSTDIR\Readme.txt"
  Delete /REBOOTOK "$INSTDIR\readme.unix"
  Delete /REBOOTOK "$INSTDIR\Rhino.build-date" ; Shouldn't be there, but was put by older releases
  Delete /REBOOTOK "$INSTDIR\Rhino.LICENSE.txt" ; Shouldn't be there, but was put by older releases
  Delete /REBOOTOK "$INSTDIR\RSTALanguageSupport.License.txt" ; Shouldn't be there, but was put by older releases
  Delete /REBOOTOK "$INSTDIR\RSTAUI.*.txt" ; License and readme
  Delete /REBOOTOK "$INSTDIR\rstaui.jar"
  Delete /REBOOTOK "$INSTDIR\rsta_spellchecker.jar"
  Delete /REBOOTOK "$INSTDIR\rsyntaxtextarea.jar"
  Delete /REBOOTOK "$INSTDIR\RSyntaxTextArea.License.txt"
  Delete /REBOOTOK "$INSTDIR\RText.jar"
  Delete /REBOOTOK "$INSTDIR\SpellChecker.License.txt"
  Delete /REBOOTOK "$INSTDIR\Win32FileIOExtras.dll"
  Delete /REBOOTOK "$INSTDIR\x64FileIOExtras.dll"
  Delete /REBOOTOK "$INSTDIR\doc\HelpDialogContents.xml"
  Delete /REBOOTOK "$INSTDIR\doc\en\*"
  Delete /REBOOTOK "$INSTDIR\exampleMacros\*"
  Delete /REBOOTOK "$INSTDIR\icongroups\BlueSphereIcons.jar"
  Delete /REBOOTOK "$INSTDIR\icongroups\EclipseIcons.jar"
  Delete /REBOOTOK "$INSTDIR\icongroups\ExtraIcons.xml"
  Delete /REBOOTOK "$INSTDIR\icongroups\JavaIcons.jar"
  Delete /REBOOTOK "$INSTDIR\icongroups\XPIcons.jar"
  Delete /REBOOTOK "$INSTDIR\lnfs\lookandfeels.xml"
  Delete /REBOOTOK "$INSTDIR\lnfs\OfficeLnFs.jar"
  Delete /REBOOTOK "$INSTDIR\lnfs\OfficeLnFs.License.txt"
  Delete /REBOOTOK "$INSTDIR\lnfs\substance\*jar"
  Delete /REBOOTOK "$INSTDIR\lnfs\substance.jar" ; Possibly there from old releases
  Delete /REBOOTOK "$INSTDIR\lnfs\trident.jar" ; Possibly there from old releases
  Delete /REBOOTOK "plugins\Console.jar"
  Delete /REBOOTOK "plugins\FileSystemTree.jar"
  Delete /REBOOTOK "plugins\groovy-all-1.8.8.jar"
  Delete /REBOOTOK "plugins\groovy.LICENSE.txt"
  Delete /REBOOTOK "plugins\HeapIndicator.jar"
  Delete /REBOOTOK "plugins\js-14*.jar" ; Old and new versions
  Delete /REBOOTOK "plugins\jsonbeans.jar"
  Delete /REBOOTOK "plugins\jsonbeans.LICENSE.txt"
  Delete /REBOOTOK "plugins\jtidy-r938.jar"
  Delete /REBOOTOK "plugins\jtidy-r938.LICENSE.txt"
  Delete /REBOOTOK "plugins\language_support.jar"
  Delete /REBOOTOK "plugins\MacroSupport.jar"
  Delete /REBOOTOK "plugins\ProjectSupport.jar"
  Delete /REBOOTOK "plugins\Rhino.build-date"
  Delete /REBOOTOK "plugins\Rhino.LICENSE.txt"
  Delete /REBOOTOK "plugins\RSTALanguageSupport.License.txt"
  Delete /REBOOTOK "plugins\RTextLanguageSupport.jar"
  Delete /REBOOTOK "plugins\SourceBrowser.jar"
  Delete /REBOOTOK "plugins\TaskList.jar"
  Delete /REBOOTOK "plugins\tidy.jar"
  Delete /REBOOTOK "plugins\ToolSupport.jar"

  Delete "$SMPROGRAMS\Savu\Uninstall.lnk"
  Delete "$SMPROGRAMS\Savu\Website.lnk"
  Delete "$DESKTOP\Savu.lnk"
  Delete "$SMPROGRAMS\Savu\Savu.lnk"

  ; Folders will be removed if they are completely empty (user didn't add anything)
  RMDir "$SMPROGRAMS\Savu"
  RMDir "$INSTDIR\doc\en"
  RMDir "$INSTDIR\doc"
  RMDir "$INSTDIR\exampleMacros"
  RMDir "$INSTDIR\icongroups"
  RMDir "$INSTDIR\lnfs\substance"
  RMDir "$INSTDIR\lnfs"
  RMDir "$INSTDIR\plugins"
  SetOutPath "$TEMP" ; So we can remove the root dir if it's empty
  RMDir "$INSTDIR"

  DeleteRegKey ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}"
  DeleteRegKey HKLM "${PRODUCT_DIR_REGKEY}"
  SetAutoClose true
SectionEnd