# Build a clean Java runtime image for Photo Organizer

Write-Host "Cleaning old runtime image..."
if (Test-Path "image") {
    Remove-Item -Recurse -Force image
}

Write-Host "Building runtime image with jlink..."

jlink `
  --module-path "$Env:JAVA_HOME\jmods;C:\javafx-jmods-21.0.9" `
  --add-modules java.base,java.desktop,java.logging,java.xml,jdk.unsupported,javafx.controls,javafx.fxml,javafx.graphics,javafx.base `
  --output image

Write-Host "Runtime image built successfully."