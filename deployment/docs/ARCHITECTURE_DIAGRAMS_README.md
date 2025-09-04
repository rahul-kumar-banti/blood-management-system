# Blood Bank Management System - Architecture Diagrams

This directory contains comprehensive system architecture diagrams for the Blood Bank Management System, based on the current project implementation.

## Available Diagrams

### 1. SYSTEM_ARCHITECTURE_DIAGRAM.puml
- **Format**: PlantUML diagram
- **Description**: Comprehensive three-tier architecture diagram showing all components and their relationships
- **Usage**: Can be viewed in any PlantUML-compatible viewer or IDE plugin
- **Features**: 
  - Color-coded tiers (Presentation, Application, Data)
  - Detailed component breakdown
  - Relationship arrows showing data flow
  - Security and configuration components

### 2. SYSTEM_ARCHITECTURE_TEXT.txt
- **Format**: ASCII text diagram
- **Description**: Detailed text-based representation of the system architecture
- **Usage**: Can be viewed in any text editor
- **Features**:
  - Clear tier separation with box drawings
  - Component listings with descriptions
  - Configuration details
  - Technology stack information

### 3. COMPONENT_DIAGRAM.txt
- **Format**: Simplified ASCII component diagram
- **Description**: High-level component relationships and data flow
- **Usage**: Quick reference for understanding system structure
- **Features**:
  - Clean component hierarchy
  - Data flow arrows
  - Key component descriptions

## System Architecture Overview

The Blood Bank Management System follows a **three-tier architecture**:

### 🖥️ Presentation Tier (Client)
- **Java Swing Application** for Admin/Staff operations
- **Components**: Main client, dialogs, table models, client services
- **Purpose**: User interface and client-side business logic

### ⚙️ Application Tier (Backend)
- **Spring Boot Application** exposing REST endpoints
- **Components**: Controllers, Services, Repositories, Security, DTOs, Entities
- **Purpose**: Business logic, API endpoints, data processing

### 🗄️ Data Tier (Database)
- **PostgreSQL Database** with constraints, indexes, and audit fields
- **Components**: Database schema, tables, data integrity features
- **Purpose**: Persistent data storage and management

## Communication & Security

- **Protocol**: HTTPS REST API
- **Authentication**: JWT Bearer tokens
- **Port**: 8080
- **Context Path**: `/api`
- **Data Format**: JSON

## Key Technologies

- **Frontend**: Java Swing
- **Backend**: Spring Boot 3.2.0, Spring Security, Spring Data JPA
- **Database**: PostgreSQL
- **Authentication**: JWT (JSON Web Tokens)
- **Build Tool**: Maven
- **Java Version**: 17

## How to Use These Diagrams

### For Developers
1. Use `SYSTEM_ARCHITECTURE_DIAGRAM.puml` for detailed system understanding
2. Reference `COMPONENT_DIAGRAM.txt` for quick component relationships
3. Use `SYSTEM_ARCHITECTURE_TEXT.txt` for comprehensive documentation

### For Documentation
1. Include PlantUML diagram in technical documentation
2. Use text diagrams for README files and markdown documents
3. Reference component diagram for system overview presentations

### For Architecture Reviews
1. All diagrams show the current implementation
2. Use as baseline for future architecture discussions
3. Validate against actual code structure

## Viewing PlantUML Diagrams

### Online Viewers
- [PlantUML Online Server](http://www.plantuml.com/plantuml/uml/)
- [PlantText](https://www.planttext.com/)

### IDE Plugins
- **IntelliJ IDEA**: PlantUML integration plugin
- **VS Code**: PlantUML extension
- **Eclipse**: PlantUML plugin

### Command Line
```bash
# Install PlantUML
java -jar plantuml.jar SYSTEM_ARCHITECTURE_DIAGRAM.puml
```

## Maintenance

These diagrams should be updated when:
- New components are added to the system
- Architecture changes are made
- New technologies are introduced
- API endpoints are modified

## Current Implementation Status

✅ **Implemented Components**:
- All three tiers are fully implemented
- JWT authentication is working
- REST API endpoints are functional
- Database schema is established
- Client-server communication is operational

🔄 **Architecture Alignment**:
- Current implementation matches the documented architecture
- All described components exist in the codebase
- Configuration matches the documented settings
- Security implementation follows the documented approach

---

*Last Updated: Based on current project implementation*
*Architecture Status: Fully Implemented and Documented*
