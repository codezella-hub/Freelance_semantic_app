# Script pour ajouter des evenements
$baseUrl = "http://localhost:8089/api/events"

Write-Host "Adding events..." -ForegroundColor Cyan

# Event 1
$event1 = @{
    eventTitle = "Formation AWS Solutions Architect"
    eventDescription = "Formation complete sur architecture AWS avec certification officielle"
    eventCategory = "Formation"
    eventDate = "2024-11-15T09:00:00Z"
    eventType = "Premium"
} | ConvertTo-Json

Invoke-RestMethod -Uri $baseUrl -Method Post -Body $event1 -ContentType "application/json"
Write-Host "Event 1 created" -ForegroundColor Green

# Event 2
$event2 = @{
    eventTitle = "Atelier DevOps avec Docker"
    eventDescription = "Atelier pratique sur Docker et conteneurisation"
    eventCategory = "Atelier"
    eventDate = "2024-11-20T14:00:00Z"
    eventType = "Public"
} | ConvertTo-Json

Invoke-RestMethod -Uri $baseUrl -Method Post -Body $event2 -ContentType "application/json"
Write-Host "Event 2 created" -ForegroundColor Green

# Event 3
$event3 = @{
    eventTitle = "Conference Kubernetes Avance"
    eventDescription = "Conference sur techniques avancees de Kubernetes"
    eventCategory = "Conference"
    eventDate = "2024-12-01T10:00:00Z"
    eventType = "Premium"
} | ConvertTo-Json

Invoke-RestMethod -Uri $baseUrl -Method Post -Body $event3 -ContentType "application/json"
Write-Host "Event 3 created" -ForegroundColor Green

# Event 4
$event4 = @{
    eventTitle = "Workshop React & TypeScript"
    eventDescription = "Workshop intensif sur React 18 et TypeScript"
    eventCategory = "Workshop"
    eventDate = "2024-12-05T09:00:00Z"
    eventType = "Public"
} | ConvertTo-Json

Invoke-RestMethod -Uri $baseUrl -Method Post -Body $event4 -ContentType "application/json"
Write-Host "Event 4 created" -ForegroundColor Green

# Event 5
$event5 = @{
    eventTitle = "Seminaire Securite Cloud"
    eventDescription = "Seminaire sur meilleures pratiques de securite cloud"
    eventCategory = "Seminaire"
    eventDate = "2024-12-10T13:00:00Z"
    eventType = "Premium"
} | ConvertTo-Json

Invoke-RestMethod -Uri $baseUrl -Method Post -Body $event5 -ContentType "application/json"
Write-Host "Event 5 created" -ForegroundColor Green

# Event 6
$event6 = @{
    eventTitle = "Formation Python pour Data Science"
    eventDescription = "Formation complete Python avec focus sur analyse de donnees"
    eventCategory = "Formation"
    eventDate = "2024-12-15T09:00:00Z"
    eventType = "Premium"
} | ConvertTo-Json

Invoke-RestMethod -Uri $baseUrl -Method Post -Body $event6 -ContentType "application/json"
Write-Host "Event 6 created" -ForegroundColor Green

# Event 7
$event7 = @{
    eventTitle = "Atelier Machine Learning"
    eventDescription = "Atelier pratique sur algorithmes de Machine Learning"
    eventCategory = "Atelier"
    eventDate = "2024-12-20T14:00:00Z"
    eventType = "Public"
} | ConvertTo-Json

Invoke-RestMethod -Uri $baseUrl -Method Post -Body $event7 -ContentType "application/json"
Write-Host "Event 7 created" -ForegroundColor Green

# Event 8
$event8 = @{
    eventTitle = "Conference Architecture Microservices"
    eventDescription = "Conference sur conception et deploiement microservices"
    eventCategory = "Conference"
    eventDate = "2025-01-10T10:00:00Z"
    eventType = "Premium"
} | ConvertTo-Json

Invoke-RestMethod -Uri $baseUrl -Method Post -Body $event8 -ContentType "application/json"
Write-Host "Event 8 created" -ForegroundColor Green

# Event 9
$event9 = @{
    eventTitle = "Workshop Angular 19"
    eventDescription = "Workshop sur nouveautes Angular 19 et standalone components"
    eventCategory = "Workshop"
    eventDate = "2025-01-15T09:00:00Z"
    eventType = "Public"
} | ConvertTo-Json

Invoke-RestMethod -Uri $baseUrl -Method Post -Body $event9 -ContentType "application/json"
Write-Host "Event 9 created" -ForegroundColor Green

# Event 10
$event10 = @{
    eventTitle = "Formation Cybersecurite"
    eventDescription = "Formation complete sur cybersecurite et tests de penetration"
    eventCategory = "Formation"
    eventDate = "2025-01-20T09:00:00Z"
    eventType = "Premium"
} | ConvertTo-Json

Invoke-RestMethod -Uri $baseUrl -Method Post -Body $event10 -ContentType "application/json"
Write-Host "Event 10 created" -ForegroundColor Green

# Event 11
$event11 = @{
    eventTitle = "Atelier CI/CD avec Jenkins"
    eventDescription = "Atelier pratique sur integration et deploiement continus"
    eventCategory = "Atelier"
    eventDate = "2025-01-25T14:00:00Z"
    eventType = "Public"
} | ConvertTo-Json

Invoke-RestMethod -Uri $baseUrl -Method Post -Body $event11 -ContentType "application/json"
Write-Host "Event 11 created" -ForegroundColor Green

# Event 12
$event12 = @{
    eventTitle = "Conference Intelligence Artificielle"
    eventDescription = "Conference sur dernieres avancees en IA et Deep Learning"
    eventCategory = "Conference"
    eventDate = "2025-02-01T10:00:00Z"
    eventType = "Premium"
} | ConvertTo-Json

Invoke-RestMethod -Uri $baseUrl -Method Post -Body $event12 -ContentType "application/json"
Write-Host "Event 12 created" -ForegroundColor Green

# Event 13
$event13 = @{
    eventTitle = "Workshop Vue.js 3"
    eventDescription = "Workshop sur Vue.js 3 avec Composition API et TypeScript"
    eventCategory = "Workshop"
    eventDate = "2025-02-05T09:00:00Z"
    eventType = "Public"
} | ConvertTo-Json

Invoke-RestMethod -Uri $baseUrl -Method Post -Body $event13 -ContentType "application/json"
Write-Host "Event 13 created" -ForegroundColor Green

# Event 14
$event14 = @{
    eventTitle = "Formation Blockchain et Smart Contracts"
    eventDescription = "Formation sur blockchain Ethereum et developpement smart contracts"
    eventCategory = "Formation"
    eventDate = "2025-02-10T09:00:00Z"
    eventType = "Premium"
} | ConvertTo-Json

Invoke-RestMethod -Uri $baseUrl -Method Post -Body $event14 -ContentType "application/json"
Write-Host "Event 14 created" -ForegroundColor Green

# Event 15
$event15 = @{
    eventTitle = "Atelier GraphQL"
    eventDescription = "Atelier pratique sur GraphQL avec Apollo Server et Client"
    eventCategory = "Atelier"
    eventDate = "2025-02-15T14:00:00Z"
    eventType = "Public"
} | ConvertTo-Json

Invoke-RestMethod -Uri $baseUrl -Method Post -Body $event15 -ContentType "application/json"
Write-Host "Event 15 created" -ForegroundColor Green

Write-Host "`nAll 15 events created successfully!" -ForegroundColor Green

