# Application de Réservation Restaurant 🍽️

Système de réservation moderne pour restaurant développé avec Spring Boot et Angular, mettant l'accent sur une architecture propre et des tests complets.

## 📋 Présentation du Projet

Cette application permet aux clients de faire des réservations anonymes dans un restaurant tout en fournissant au restaurateur des capacités complètes de gestion des réservations. Construit comme projet pour démontrer les pratiques modernes d'architecture logicielle et de développement.

### Contexte Métier
- **Capacité restaurant :** 20 tables (de 2 à 8 personnes)
- **Horaires d'ouverture :** 12h00 - 23h00  
- **Fenêtre de réservation :** Jusqu'à 30 jours à l'avance
- **Créneaux fixes :** Intervalles de 2h pour les services midi et soir
- **Clients anonymes :** Aucun compte requis (nom, téléphone, email uniquement)

## 🏗️ Architecture & Décisions Techniques

### Stack Technologique
- **Backend :** Spring Boot 3.x, Spring Data JPA, PostgreSQL
- **Frontend :** Angular 17+ (prévu)
- **Tests :** JUnit 5, AssertJ, tests d'intégration @DataJpaTest
- **Base de données :** PostgreSQL avec configuration application.yml
- **Style de code :** Google Java Style Guide (2 espaces)

### Patterns Architecturaux
- **Domain-Driven Design :** Organisé par domaines métier, pas par couches techniques
- **Repository Pattern :** Couche d'accès aux données propre avec JpaRepository
- **Test-Driven Development :** Tests d'intégration complets pour la couche données
- **Architecture Monolithique :** Monolithe modulaire pour focus apprentissage et simplicité

## 📦 Structure du Projet

```
src/main/java/com/fabien/restaurant_booking_api/
├── restaurant/domain/          # Entité et repository Restaurant
├── table/domain/              # Entité et repository DiningTable  
├── customer/domain/           # Entité et repository Customer
├── booking/domain/            # Entité et repository Booking
└── shared/                    # Utilitaires communs et configurations

src/test/java/                 # Tests d'intégration avec @DataJpaTest
├── restaurant/domain/RestaurantRepositoryTest.java
├── table/domain/DiningTableRepositoryTest.java
├── customer/domain/CustomerRepositoryTest.java
└── booking/domain/BookingRepositoryTest.java
```

## 🎯 Fonctionnalités Principales (MVP)

### Implémentées ✅
- [x] **Gestion Restaurant :** Stockage des informations de base du restaurant
- [x] **Gestion Tables :** Tables avec suivi de la capacité et du statut
- [x] **Gestion Clients :** Données clients anonymes (téléphone comme identifiant unique)
- [x] **Système de Réservation :** Réservations avec contraintes date/créneau
- [x] **Intégrité des Données :** Contrainte unique empêchant les doubles réservations
- [x] **Tests Complets :** Couverture complète par tests d'intégration

### Prévues 🚧
- [ ] Contrôleurs API REST avec validation
- [ ] Pattern DTO pour design API propre
- [ ] Gestion des exceptions métier
- [ ] Interface frontend Angular
- [ ] Logique d'annulation de réservation

## 🗄️ Modèle de Données

### Entités Principales

**Restaurant**
- Informations de base (nom, adresse, téléphone)
- Relation un-vers-plusieurs avec les tables

**DiningTable** 
- Gestion de la capacité et du statut
- Relation clé étrangère vers restaurant

**Customer**
- Données client anonymes
- Numéro de téléphone comme identifiant métier unique
- Email optionnel pour les confirmations

**Booking**
- Lie client, table, date et créneau horaire
- Contrainte unique sur (table_id, date, time_slot_type)
- Suivi du statut (CONFIRMED, CANCELLED, COMPLETED)

**TimeSlotType** (Enum)
```java
LUNCH_12H14H, LUNCH_14H16H, DINNER_19H21H, DINNER_21H23H
```

### Schéma Base de Données
- **Tables :** restaurants, dining_tables, customers, bookings
- **Contraintes :** Clés étrangères, contraintes uniques pour règles métier
- **Base de données :** PostgreSQL avec auto-DDL Hibernate

## 🚀 Démarrage Rapide

### Prérequis
- Java 17+
- Maven 3.6+
- PostgreSQL 12+
- IDE avec support Spring Boot (IntelliJ IDEA recommandé)

### Installation

1. **Cloner le dépôt**
   ```bash
   git clone https://github.com/votre-username/restaurant-booking-app.git
   cd restaurant-booking-app
   ```

2. **Configuration Base de Données PostgreSQL**
   ```sql
   CREATE DATABASE restaurant_booking;
   ```

3. **Configurer la Connexion Base de Données**
   Mettre à jour `src/main/resources/application.yml` :
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/restaurant_booking
       username: votre_nom_utilisateur
       password: votre_mot_de_passe
   ```

4. **Lancer l'Application**
   ```bash
   mvn spring-boot:run
   ```

5. **Exécuter les Tests**
   ```bash
   mvn test
   ```

## 🧪 Stratégie de Tests

### Tests d'Intégration
- **@DataJpaTest** pour tester la couche repository
- **Base PostgreSQL réelle** (pas H2 en mémoire)
- **Convention BDD :** `method_should_behavior_when_context()`
- **Assertions AssertJ** pour de meilleurs messages d'erreur
- **TestEntityManager** pour setup propre des données de test

### Couverture de Tests
- ✅ Persistance et récupération des entités
- ✅ Relations JPA (ManyToOne, OneToMany)
- ✅ Validation des contraintes base de données
- ✅ Cas limites (non trouvé, données dupliquées)

## 🔧 Pratiques de Développement

### Qualité du Code
- **Google Java Style Guide** avec formatage automatisé
- **Save Actions** configurées pour auto-formatage et optimisation imports
- **Nommage full anglais** pour compatibilité internationale
- **Commits conventionnels** avec messages clairs et descriptifs

### Principes de Design
- Adhérence aux **principes SOLID**
- Patterns **Clean Architecture**
- Structure packages **Domain-driven design**
- **Séparation des responsabilités** entre couches

## 📈 Feuille de Route

### Phase 2 : API & Validation
- Contrôleurs REST avec codes de statut HTTP appropriés
- Implémentation du pattern DTO
- Validation Bean et application des règles métier
- Gestion personnalisée des exceptions

### Phase 3 : Frontend & UX
- Frontend Angular avec formulaires réactifs
- Vérification de disponibilité en temps réel
- Design responsive mobile
- Flux de réservation convivial

### Phase 4 : Fonctionnalités Avancées
- Gestion du surbooking (capacité +10%)
- Confirmations et rappels par email
- Support multi-tenant pour chaînes de restaurants
- Dashboard analytique pour propriétaires de restaurants

## 🤝 Contribution

Les retours et suggestions sont les bienvenus ! N'hésitez pas à :
- Ouvrir des issues pour bugs ou demandes de fonctionnalités
- Soumettre des pull requests avec des améliorations
- Partager des insights architecturaux ou bonnes pratiques

## 🎓 Objectifs d'Apprentissage

Ce projet démontre la maîtrise de :
- **Écosystème Spring Boot** et développement Java moderne
- **JPA/Hibernate** avec relations complexes et contraintes
- **Design et intégration PostgreSQL**
- **Développement piloté par les tests** avec couverture complète
- **Architecture propre** et principes domain-driven design
- **Pratiques de développement professionnel** et standards qualité code

---

**Construit avec ❤️ comme projet portfolio démontrant les pratiques modernes de développement Java.**
