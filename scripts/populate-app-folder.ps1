# Populate jpackage-input/app with ONLY the runtime dependency jars
# No duplicates, no test jars, no JavaFX jars.

Write-Host "Cleaning existing app folder..."
if (Test-Path "jpackage-input\app") {
    Remove-Item -Recurse -Force "jpackage-input\app"
}
New-Item -ItemType Directory -Path "jpackage-input\app" | Out-Null

Write-Host "Copying runtime dependencies from Maven..."

# Step 1: Rebuild dependencies cleanly
mvn dependency:copy-dependencies -DincludeScope=runtime

# Step 2: Copy only necessary jars
Get-ChildItem "target\dependency" -Filter "*.jar" |
    Where-Object {
        # Exclude JavaFX jars
        $_.Name -notmatch "^javafx" -and

        # Exclude test jars
        $_.Name -notmatch "test" -and

        # Exclude jmods or SDK leftovers
        $_.Name -notmatch "jmods" -and

        # Exclude duplicates (Spring Boot sometimes produces classifier jars)
        $_.Name -notmatch "sources" -and
        $_.Name -notmatch "javadoc"
    } |
    Copy-Item -Destination "jpackage-input\app" -Force

Write-Host "Dependency jars copied successfully."