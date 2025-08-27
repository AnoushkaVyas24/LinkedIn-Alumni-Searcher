**Alumni LinkedIn Searcher**

A Spring Boot backend application that allows users to search for alumni from a specific university and designation, fetch data from LinkedIn via PhantomBuster, and store results in a PostgreSQL database.

*Table of Contents*
1. Features
2. Requirements
3. Setup and Installation
4. Configuration
5. API Endpoints
6. Database
7. Usage
8. Technologies Used

*Features*
1. Search Alumni: Search for alumni by university, designation, and optionally passout year.
2. PhantomBuster Integration: Scrapes LinkedIn data automatically using PhantomBuster API.
3. Save Results: Persist alumni profiles into PostgreSQL.
4. Retrieve Saved Profiles: Fetch all saved alumni profiles.
5. RESTful API: Exposes endpoints for frontend or external clients.

*Requirements*
Java 17+
Maven or Gradle
PostgreSQL 12+
PhantomBuster account and agent configured

*Setup and Installation*
1. Clone the repository :
        git clone <repo-url>
        cd alumni-linkedin-searcher


2. Configure PostgreSQL
3. Create a database (e.g., alumni_db)

4. Update application.properties with your database credentials:
        spring.datasource.url=jdbc:postgresql://localhost:5432/alumni_db
        spring.datasource.username=postgres
        spring.datasource.password=<your_password>
        spring.jpa.hibernate.ddl-auto=update


5. Configure PhantomBuster API
        Add your PhantomBuster API key, agent ID, and session cookie in application.properties:
        phantombuster.api.key=<YOUR_API_KEY>
        phantombuster.agent.id=<YOUR_AGENT_ID>
        phantombuster.session.cookie=<YOUR_SESSION_COOKIE>
        phantombuster.api.url=https://api.phantombuster.com/api/v2/agents/launch


6. Build and Run
        mvn clean install
        mvn spring-boot:run


The backend will start on http://localhost:8080.

*Database*

Table: alumni_profiles

Columns:

Column Name	Type	Description
id	BIGINT PK	Auto-generated primary key
name	VARCHAR	Alumni name
role	VARCHAR	Current role/job title
university	VARCHAR	University name
location	VARCHAR	Current location
linkedin_headline	VARCHAR	LinkedIn headline
passout_year	INT	Graduation year

*Usage*
Start the Spring Boot application.

Use Postman or any REST client to test the endpoints.

Ensure PhantomBuster agent runs successfully to fetch real-time alumni data.

Fetched profiles are automatically saved into PostgreSQL and can be retrieved anytime.

*Technologies Used*
Java 17
Spring Boot 3
Spring Data JPA
PostgreSQL
PhantomBuster API
Maven
Jackson (JSON parsing)
