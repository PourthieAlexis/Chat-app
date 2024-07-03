# Chat Application

## Description

Cette application de chat permet aux utilisateurs de s'inscrire, de se connecter, de discuter en privé avec d'autres utilisateurs, ainsi que de participer à des discussions publiques. L'application est construite avec Spring Boot pour le backend et Angular pour le frontend.

## Features

- Inscription et connexion des utilisateurs
- Discussions privées entre deux utilisateurs
- Discussions publiques
- Authentification JWT pour sécuriser les endpoints
- Gestion des erreurs et validation des formulaires

## Prerequisites

- Java 11+
- Node.js 14+
- Angular CLI

## Installation

### Backend

1. Clonez le repository

   ```sh
   git clone https://github.com/PourthieAlexis/chat-app.git
   cd chat-app-backend
   ```

2. Configurez votre base de données dans `src/main/resources/application.properties`

   ```properties
    spring.application.name=chat-app
    spring.datasource.url=jdbc:h2:mem:chatdb
    spring.datasource.driverClassName=org.h2.Driver
    spring.datasource.username=votre-username
    spring.datasource.password=votre-password
    spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
   ```

3. Construisez et lancez l'application Spring Boot
   ```sh
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

### Frontend

1. Ce mettre dans le dossier

   ```sh
   cd chat-app-frontend
   ```

2. Installez les dépendances

   ```sh
   npm install
   ```

3. Lancez l'application Angular
   ```sh
   ng serve
   ```

## Utilisation

1. Ouvrez votre navigateur et allez à `http://localhost:4200`
2. Inscrivez-vous avec un nouveau compte
3. Connectez-vous avec vos informations d'identification
4. Ajoutez des contacts et commencez à discuter en privé ou dans le chat publique

## Structure du Projet

### Backend

- `src/main/java/com/example/chat_app`
  - `controller`: Contient les contrôleurs
  - `dto`: Contient les dto
  - `model`: Contient les entités JPA
  - `repository`: Contient les interfaces de repository Spring Data JPA
  - `service`: Contient les services métier
  - `exception`: Contient les exceptions personnalisées

### Frontend

- `src/app`
  - `auth`: Contient les composants d'authentification (login et register)
  - `back-button`: Composant de bouton de retour
  - `chat`: Contient les composants de chat (publique et privé)
  - `chat-list`: Composant de liste de chats
