# Script pour afficher un resume des donnees
$baseUrl = "http://localhost:8089/api"

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "   RESUME DES DONNEES FUSEKI /rassil" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Get events
Write-Host "EVENEMENTS:" -ForegroundColor Yellow
$eventsResponse = Invoke-RestMethod -Uri "$baseUrl/events" -Method Get
$events = $eventsResponse.events
Write-Host "Total: $($eventsResponse.count) evenements`n" -ForegroundColor Green

$events | ForEach-Object {
    Write-Host "  - $($_.eventTitle)" -ForegroundColor White
    Write-Host "    Categorie: $($_.eventCategory) | Type: $($_.eventType) | Date: $($_.eventDate)" -ForegroundColor Gray
}

Write-Host "`n----------------------------------------`n" -ForegroundColor Cyan

# Get certifications
Write-Host "CERTIFICATIONS:" -ForegroundColor Yellow
$certsResponse = Invoke-RestMethod -Uri "$baseUrl/certifications" -Method Get
$certs = $certsResponse.certifications
Write-Host "Total: $($certsResponse.count) certifications`n" -ForegroundColor Green

$certs | ForEach-Object {
    Write-Host "  - $($_.certificationName)" -ForegroundColor White
    Write-Host "    Emetteur: $($_.issuedBy) | Type: $($_.certificationType)" -ForegroundColor Gray
    Write-Host "    Emission: $($_.issueDate) | Expiration: $($_.expirationDate)" -ForegroundColor Gray
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "STATISTIQUES:" -ForegroundColor Yellow
Write-Host "  Total Evenements: $($eventsResponse.count)" -ForegroundColor White
Write-Host "  Total Certifications: $($certsResponse.count)" -ForegroundColor White
Write-Host "`n  Categories d'evenements:" -ForegroundColor White
$events | Group-Object eventCategory | ForEach-Object {
    Write-Host "    - $($_.Name): $($_.Count)" -ForegroundColor Gray
}
Write-Host "`n  Types de certifications:" -ForegroundColor White
$certs | Group-Object certificationType | ForEach-Object {
    Write-Host "    - $($_.Name): $($_.Count)" -ForegroundColor Gray
}
Write-Host "`n========================================`n" -ForegroundColor Cyan

Write-Host "Acces aux interfaces:" -ForegroundColor Yellow
Write-Host "  Frontend Angular: http://localhost:4200" -ForegroundColor White
Write-Host "  API Events: http://localhost:8089/api/events" -ForegroundColor White
Write-Host "  API Certifications: http://localhost:8089/api/certifications" -ForegroundColor White
Write-Host "  Fuseki Dataset: http://localhost:3030/rassil" -ForegroundColor White
Write-Host ""

