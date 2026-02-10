# -----------------------------
# Build portable Windows exe (NO WiX REQUIRED)
# -----------------------------

# Paths
$projectRoot = Get-Location
$targetDir = Join-Path $projectRoot "target"
$outputDir = Join-Path $projectRoot "output"
$extraFilesDir = Join-Path $projectRoot "extra-files"
$shadedJar = "ApaveImageShrinker-1.0-SNAPSHOT.jar"
$mainClass = "shrinker.Main"
$appName = "ApaveImageShrinker"
$appVersion = "1.0"

# -----------------------------
# Step 1: Ensure extra-files folder exists
# -----------------------------
if (-Not (Test-Path $extraFilesDir)) {
    Write-Host "Creating extra-files folder..."
    New-Item -ItemType Directory -Path $extraFilesDir
}

# Check for config.txt
$configFile = Join-Path $extraFilesDir "config.txt"
if (-Not (Test-Path $configFile)) {
    Write-Host "config.txt not found in extra-files. Please place it there."
    exit
}

# Ensure output directory exists
if (-Not (Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir | Out-Null
}

# -----------------------------
# Step 2: Run jpackage (NO EXE INSTALLER MODE)
# -----------------------------
Write-Host "Running jpackage..."

jpackage `
    --type app-image `
    --input $targetDir `
    --main-jar $shadedJar `
    --main-class $mainClass `
    --name $appName `
    --app-version $appVersion `
    --dest $outputDir `
    --resource-dir $extraFilesDir

# -----------------------------
# Step 3: Done
# -----------------------------
Write-Host "Portable application created at: $outputDir\$appName"
Write-Host "Run $outputDir\$appName\$appName.exe to start the program."
