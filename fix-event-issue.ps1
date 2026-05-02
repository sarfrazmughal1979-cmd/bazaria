# repair-events-final-script.ps1
# Adds @NoArgsConstructor to event classes, removes 'final' from fields

$javaFiles = Get-ChildItem -Recurse -Path . -Filter *.java |
    Where-Object { $_.FullName -match 'src\\main\\java' }

foreach ($file in $javaFiles) {
    $content = Get-Content $file.FullName -Raw

    # --- Only process files that extend DomainEvent / IntegrationEvent ---
    if ($content -notmatch 'extends\s+(Domain|Integration)Event') { continue }

    $className = [System.IO.Path]::GetFileNameWithoutExtension($file.Name)
    $modified = $false

    # ------------------------------------------------------------
    # 1. Add @NoArgsConstructor if missing AND no explicit no-arg constructor exists
    # ------------------------------------------------------------
    $hasNoArgsConstructorAnnotation = $content -match '@NoArgsConstructor'
    $hasExplicitNoArgConstructor = $content -match "public\s+$className\s*\(\s*\)\s*\{"

    if (-not $hasNoArgsConstructorAnnotation -and -not $hasExplicitNoArgConstructor) {
        Write-Host "Adding @NoArgsConstructor to $($file.Name)"

        # Add import if missing
        if ($content -notmatch 'import\s+lombok\.NoArgsConstructor;') {
            $content = $content -replace '(package\s+[^\n;]+[;\n])',
                                        "`${1}`r`nimport lombok.NoArgsConstructor;"
        }

        # Insert annotation just before "public class ClassName"
        $content = $content -replace '(public\s+class\s+\w+)',
                                    "@NoArgsConstructor`r`n`$1"
        $modified = $true
    }

    # ------------------------------------------------------------
    # 2. Remove 'final' from fields ONLY IF the class already has (or will have) a no-arg constructor
    # ------------------------------------------------------------
    # After step 1, we know @NoArgsConstructor will be present. We remove 'final' in all cases.
    if ($content -match '\bfinal\b') {
        Write-Host "Removing 'final' from fields in $($file.Name)"

        # Remove 'final' that appears between an access modifier and the type
        $content = $content -replace '(\bprivate\b|\bprotected\b|\bpublic\b)(\s+)(final)(\s+)', '$1$2$4'

        # Also handle fields without access modifier (rare)
        $content = $content -replace '(\s)(final)(\s+)(\w+\s+\w+)', '$1$3$4'

        $modified = $true
    }

    if ($modified) {
        $content | Set-Content -Path $file.FullName -NoNewline
        Write-Host "  Done"
    }
}