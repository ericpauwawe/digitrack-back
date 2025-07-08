 # Digitrack-back

## Cloner le projet

Clonez le dépôt git sur votre machine avec la commande suivante :
```sh
git clone https://github.com/ericpauwawe/digitrack-back.git
```

## Présentation

Digitrack-back est une API backend développée en Java avec Spring Boot, destinée à la reconnaissance et la classification des matériaux présents dans des flux de déchets. Elle permet d’identifier le matériau dominant dans un lot, de distinguer les mélanges, et de stocker les résultats dans une base de données PostgreSQL.

## Simulation des flux Recomat

Pour les besoins du développement et des tests, les flux de données issus de Recomat (représentant la répartition 
des matériaux détectés) sont simulés et reçus via une API REST dédiée. Cela permet de tester l’algorithme de 
reconnaissance sans dépendre d’un flux matériel réel ("envoyé via SQS).

## Architecture du projet

- **Spring Boot** : Framework principal pour l’API REST.
- **PostgreSQL** : Base de données relationnelle pour stocker les matériaux et leurs propriétés.
- **Flyway** : Gestion des migrations de schéma de base de données.
- **Maven** : Gestionnaire de dépendances et de build.
- **Docker** : Conteneurisation de l’application et de la base de données.

## Conception de la base de données

La base de données est conçue pour stocker les matériaux et leur statut (déchet ou non). Elle est initialisée et migrée automatiquement grâce à Flyway.

- **Table principale : MATERIAL**
  - `id` (SERIAL, clé primaire)
  - `label` (VARCHAR, nom du matériau, unique)
  - `is_waste` (BOOLEAN, indique si le matériau est considéré comme un déchet)

Exemple de création (voir `src/main/resources/db/migration/V1__create_material.sql`) :
```sql
CREATE TABLE MATERIAL (
    id SERIAL PRIMARY KEY,
    label VARCHAR(50) NOT NULL UNIQUE,
    is_waste BOOLEAN NOT NULL DEFAULT FALSE
);

INSERT INTO MATERIAL (label, is_waste) VALUES
        ('CARTON', FALSE),
        ('SABLE', TRUE),
        ('TERRE', TRUE);
```

- Les migrations sont versionnées et appliquées automatiquement au démarrage de l’application.
- Cette structure permet d’ajouter facilement de nouveaux matériaux ou de modifier leur statut.

### Structure des dossiers principaux

- `src/main/java/com/altaroad/digitrack/`
  - `controller/` : Contrôleurs REST (expose les endpoints de l’API)
  - `service/` : Logique métier (notamment la reconnaissance des matériaux)
  - `repository/` : Accès aux données (JPA)
  - `model/` : Entités de la base de données
  - `dto/` : Objets de transfert de données
- `src/main/resources/db/migration/` : Scripts de migration Flyway (création des tables, etc.)
- `docker-compose.yml` : Orchestration des conteneurs applicatif et base de données

## Algorithme de reconnaissance des matériaux

L’algorithme principal se trouve dans la classe `MaterialService`. Il fonctionne ainsi :

1. **Entrée** : Une répartition des matériaux sous forme de map (`{label: pourcentage}`).
2. **Recherche du matériau dominant** :
   - Si la répartition est vide ou nulle, le résultat est "DECHETS EN MELANGE".
   - Sinon, on cherche le matériau avec le pourcentage le plus élevé.
3. **Vérification** :
   - Si ce matériau n’existe pas en base, est marqué comme déchet, ou si son pourcentage est inférieur au seuil (par défaut 85%), le résultat est "DECHETS EN MELANGE".
   - Si un seul matériau est présent à 100%, on retourne son label.
   - Sinon, on retourne "<label du matériau dominant>+DECHETS" pour signaler un mélange.

Ce seuil de dominance est configurable via la propriété `material.seuil-dominant`.

## Exécution locale avec Docker

### Prérequis
- [Docker](https://www.docker.com/) installé
- [Maven](https://maven.apache.org/) pour builder le jar (optionnel si vous ne modifiez pas le code)

### Étapes

1. **Builder le projet** (si nécessaire) :
   ```sh
   mvn clean package
   ```
   Le jar sera généré dans le dossier `target/` avec un nom dépendant de la version définie dans le fichier `pom.xml` (par exemple : `digitrack-back-1.0.0-SNAPSHOT.jar`). Ce nom est templatisé et changera automatiquement si la version Maven évolue.

2. **Lancer les conteneurs Docker** :
   ```sh
   docker-compose up --build
   ```
   Cela démarre :
   - Une base PostgreSQL (port 5432)
   - L’application Spring Boot (port 8080)

3. **Accéder à l’API** :
   L’API sera disponible sur [http://localhost:8080](http://localhost:8080)

### Variables importantes
- Les paramètres de connexion à la base sont définis dans `docker-compose.yml` et peuvent être adaptés si besoin.
- Les scripts de migration Flyway sont automatiquement appliqués au démarrage.

## Exemple d’appel API

Pour reconnaître un matériau dominant, faites un POST sur l’endpoint approprié (voir la documentation OpenAPI générée automatiquement sur [http://localhost:8080/api/swagger-ui/index.html](http://localhost:8080/api/swagger-ui/index.html)).

---

Pour toute question ou contribution, vous pouvez me contacter à l'adresse suivante : [eric.pauwawe@adibone-ts.com](mailto:eric.pauwawe@adibone-ts.com)
