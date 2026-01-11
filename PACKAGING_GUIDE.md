# Photo Organizer — Packaging & Deployment Guide

This document explains how to:

- Build and run the application locally  
- Package it as a Spring Boot thin JAR  
- Create a custom Java runtime image with `jlink`  
- Build a Windows MSI installer using `jpackage`  
- Ensure the installed app launches correctly  

This guide reflects the final, working configuration.

---

## 🧱 1. Build & Run Locally

### Build the project

```powershell
mvn clean package
```

This produces:

```
target/photo-organizer-1.0.0.jar
```

### Run locally using your system JDK

```powershell
java -jar target/photo-organizer-1.0.0.jar
```

If the app launches normally, your local environment is correct.

---

## 📦 2. Spring Boot Thin JAR Structure

Your packaged JAR must contain:

```
BOOT-INF/classes/application.properties
org/springframework/boot/loader/launch/PropertiesLauncher.class
```

Verify:

```powershell
jar tf target/photo-organizer-1.0.0.jar | Select-String PropertiesLauncher
```

---

## 🏗️ 3. Prepare jpackage-input Folder

Your installer will package:

- The thin JAR
- All dependency JARs
- Your runtime image

Copy the JAR:

```powershell
Copy-Item -Force target\photo-organizer-1.0.0.jar jpackage-input\
```

Ensure the folder contains:

```
jpackage-input/
    photo-organizer-1.0.0.jar
    app/   ← dependency jars
```

---

## 🧩 4. Build the Runtime Image (jlink)

JavaFX requires the **JMODS** distribution, not the SDK.

Assuming:

```
$Env:JAVA_HOME = C:\Users\liu_m\.jdks\ms-21.0.9
JavaFX JMODS = C:\javafx-jmods-21.0.9
```

Run:

```powershell
jlink `
  --module-path "$Env:JAVA_HOME\jmods;C:\javafx-jmods-21.0.9" `
  --add-modules java.base,java.desktop,java.logging,java.xml,jdk.unsupported,javafx.controls,javafx.fxml,javafx.graphics,javafx.base `
  --output image
```

This produces:

```
image/
    bin/
    conf/
    legal/
    lib/
```

This folder is bundled inside the MSI.

---

## 📦 5. Build the MSI Installer (jpackage)

Older jpackage versions do **not** support `--class-path` or `--app-classpath`.  
Classpath must be passed through `--arguments`.

Use this final, working command:

```powershell
jpackage `
  --type msi `
  --name "Photo Organizer" `
  --input jpackage-input `
  --main-jar photo-organizer-1.0.0.jar `
  --main-class org.springframework.boot.loader.launch.PropertiesLauncher `
  --arguments "--class-path photo-organizer-1.0.0.jar;app/*" `
  --java-options "-Dloader.main=com.minghua.organizer.ui.PhotoOrganizerFxApp" `
  --runtime-image image `
  --win-shortcut `
  --win-menu `
  --win-menu-group "Photo Organizer" `
  --dest installer
```

This creates:

```
installer/Photo Organizer-1.0.0.msi
```

---

## 🖥️ 6. Install & Launch

Install the MSI normally.

Windows will create:

- Start Menu folder:  
  **Start → Photo Organizer → Photo Organizer**
- Searchable app entry
- Uninstaller entry in “Apps & Features”

---

## 🧪 7. Manual Debugging (Optional)

If you ever need to run the installed app manually:

```powershell
cd "C:\Program Files\Photo Organizer\app"
..\runtime\bin\java.exe -cp "photo-organizer-1.0.0.jar;app/*" org.springframework.boot.loader.launch.PropertiesLauncher
```

This prints full console output for debugging.

---

## 🎉 8. Summary

You now have a fully working:

- Spring Boot thin JAR
- JavaFX runtime image
- MSI installer
- Windows Start Menu integration
- Correct launcher classpath
- Fully portable distribution

This is a production‑grade packaging pipeline.
