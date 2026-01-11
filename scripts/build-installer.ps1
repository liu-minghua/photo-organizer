# Build the MSI installer for Photo Organizer

Write-Host "Cleaning old installer..."
if (Test-Path "installer") {
    Remove-Item -Recurse -Force installer
}
New-Item -ItemType Directory -Path installer | Out-Null

Write-Host "Copying latest JAR into jpackage-input..."
Copy-Item -Force target\photo-organizer-1.0.0.jar jpackage-input\

Write-Host "Building MSI installer with jpackage..."

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

Write-Host "Installer built successfully."