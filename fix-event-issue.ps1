# Fix all event classes across the whole project
# Adds @NoArgsConstructor and @AllArgsConstructor (Lombok) + required imports

# Find all Java files in the project (assuming Gradle layout)
$javaFiles = Get-ChildItem -Recurse -Path . -Filter *.java | 
    Where-Object { $_.FullName -match 'src\\main\\java' }

foreach ($file in $javaFiles) {
    $content = Get-Content $file.FullName -Raw
    # Only process files that extend DomainEvent (or IntegrationEvent)
    if ($content -match 'extends\s+(Integration|Domain)Event\b') {
        # Skip if it's the DomainEvent base class itself (the abstract one)
        if ($file.Name -eq 'DomainEvent.java' -or $file.Name -eq 'IntegrationEvent.java') { continue }

        # Skip if already has @NoArgsConstructor
        if ($content -match '@NoArgsConstructor') { continue }

        Write-Host "Processing $($file.FullName)"

        # 1️⃣ Add imports (after the package line) if missing
        if ($content -notmatch 'import\s+lombok\.NoArgsConstructor;') {
            $content = $content -replace '(package\s+[^\n;]+[;\n])', "`$1`r`nimport lombok.NoArgsConstructor;`r`nimport lombok.AllArgsConstructor;"
        } elseif ($content -notmatch 'import\s+lombok\.AllArgsConstructor;') {
            # If NoArgsConstructor import exists but AllArgsConstructor missing, add it
            $content = $content -replace 'import lombok.NoArgsConstructor;', "import lombok.NoArgsConstructor;`r`nimport lombok.AllArgsConstructor;"
        }

        # 2️⃣ Add annotations just before the class declaration
        $content = $content -replace '(public class \w+)', "@NoArgsConstructor`r`n@AllArgsConstructor`r`n$1"

        # Save the file back
        $content | Set-Content -Path $file.FullName -NoNewline
        Write-Host " Fixed"
    }
}