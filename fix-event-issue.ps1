# remove-final-from-event-fields.ps1
# Removes 'final' from field declarations in event classes that have @NoArgsConstructor

$javaFiles = Get-ChildItem -Recurse -Path . -Filter *.java |
             Where-Object { $_.FullName -match 'src\\main\\java' }

foreach ($file in $javaFiles) {
    $content = Get-Content $file.FullName -Raw

    # Only classes extending DomainEvent/IntegrationEvent
    if ($content -notmatch 'extends\s+(Domain|Integration)Event') { continue }

    # Only if the class has @NoArgsConstructor (we already added it)
    if ($content -notmatch '@NoArgsConstructor') { continue }

    # Check if there are any 'final' fields
    if ($content -match '\bfinal\b') {
        Write-Host "Removing 'final' from fields in $($file.Name)"

        # Remove 'final' that appears before field type (and after access modifier)
        # Pattern: (private|protected|public)(\s+)(final)(\s+) -> $1$2$4
        $content = $content -replace '(\bprivate\b|\bprotected\b|\bpublic\b)(\s+)(final)(\s+)', '$1$2$4'

        # Also handle fields declared without an explicit access modifier (rare but possible)
        $content = $content -replace '(\s)(final)(\s+)(\w+\s+\w+)', '$1$3$4'

        # Save
        $content | Set-Content -Path $file.FullName -NoNewline
        Write-Host "  Done"
    }
}