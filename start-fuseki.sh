#!/bin/bash

# Script to start Fuseki server with rassil dataset

echo "========================================"
echo "Starting Apache Jena Fuseki Server"
echo "Dataset: rassil"
echo "Port: 3030"
echo "========================================"
echo ""

# Check if Docker is available
if command -v docker &> /dev/null; then
    echo "Docker detected. Starting Fuseki in Docker..."
    echo ""
    
    # Check if container already exists
    if docker ps -a | grep -q fuseki; then
        echo "Fuseki container already exists. Starting it..."
        docker start fuseki
    else
        echo "Creating new Fuseki container..."
        docker run -d --name fuseki -p 3030:3030 stain/jena-fuseki
    fi
    
    echo ""
    echo "Fuseki is starting..."
    echo "Please wait a few seconds for the server to be ready."
    sleep 5
    echo ""
    echo "Fuseki Web Interface: http://localhost:3030"
    echo ""
    echo "IMPORTANT: You need to create a dataset named 'rassil' manually:"
    echo "1. Open http://localhost:3030 in your browser"
    echo "2. Click 'Manage datasets'"
    echo "3. Click 'Add new dataset'"
    echo "4. Enter 'rassil' as the dataset name"
    echo "5. Select 'Persistent (TDB2)' and click 'Create dataset'"
    echo ""
    echo "After creating the dataset, start your Spring Boot application."
    echo ""
else
    echo "Docker not found. Please install Docker or download Fuseki manually."
    echo ""
    echo "Download Fuseki from: https://jena.apache.org/download/"
    echo ""
    echo "After downloading, extract and run:"
    echo "  ./fuseki-server --update --mem /rassil"
    echo ""
fi

