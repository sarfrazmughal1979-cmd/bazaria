Get-ChildItem -Filter "*.java" -Recurse | ForEach-Object {
    $content = [System.IO.File]::ReadAllText($_.FullName);
    $Utf8NoBom = New-Object System.Text.UTF8Encoding($false);
    [System.IO.File]::WriteAllText($_.FullName, $content, $Utf8NoBom);
    Write-Host "Fixed: $($_.Name)" -ForegroundColor Green
}
