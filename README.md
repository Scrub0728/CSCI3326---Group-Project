# Pokédex

A simple web application that searches for Pokémon by name or Pokédex number.
The backend uses Java, Spring Boot, and PokéAPI.

## Requirements

- Java 17 or newer
- Maven
- Internet connection to retrieve Pokémon data

Check your installations (install if needed):

```bash
java -version
mvn -v
```
## Run the backend

Open a terminal in the project folder containing `pom.xml` (should be root folder), then run:

```bash
mvn spring-boot:run
```

Wait until the terminal displays `Started PokedexApplication`.

The server runs at http://localhost:8080.
Keep the terminal open while using it. Press Ctrl+C to stop it.

## Progress

See [PROGRESS.md](PROGRESS.md) for our dated work log.

## Data source

Pokémon data comes from [PokéAPI](https://pokeapi.co/).
