package pokedex;

// from jackson; this reads pokeapi's json response
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// from spring; registers the service and creates http error responses as well
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

// from java; sends requests to pokeapi and also handles any input/output errors
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// sets connection and request timeouts
import java.time.Duration;

// collections; stores types, abilities, stats, and cached pokemon
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// makes the lowercasing consistent
import java.util.Locale;

// handles Pokemon searches and retrieves data from pokeapi
@Service
public class PokemonService {

    private static final String API_URL = "https://pokeapi.co/api/v2/pokemon/";
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final ObjectMapper mapper;

    // stores successful searches in memory to avoid repeated api requests
    private final Map<String, Pokemon> cache = new ConcurrentHashMap<>();

    // spring provides the ObjectMapper used to read json responses
    public PokemonService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    // find a Pokemon by name or number and uses the cached data when available
    public Pokemon getPokemon(String query) {

        // remove any extra spaces and lowercase the query for consistency
        String search = query.trim().toLowerCase(Locale.ROOT);

        // reject empty, long, or incorrectly formatted searches
        if (search.length() > 100 || !search.matches("[a-z0-9]+(-[a-z0-9]+)*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Enter a Pokemon name or Pokedex number, such as pikachu or 25.");
        }

        // returned cached data if the Pokemon has already been retrieved
        Pokemon cached = cache.get(search);
        if (cached != null) {
            return cached;
        }

        // build a GET request that waits up to 10 secs for pokeapi
        HttpRequest request = HttpRequest.newBuilder(URI.create(API_URL + search))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        try {
            // send the request and read the response body as a string
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // if the Pokemon doesn't exist, return 404 error
            if (response.statusCode() == 404) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pokemon not found.");
            }

            // report any other unsuccessful responses as an api failure
            if (response.statusCode() != 200) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "PokeAPI is unavailable. Try again later.");
            }

            // convert the json response into our pokemon model
            Pokemon pokemon = parseResponse(response.body());

            // save the result under the search query, pokemon name, and id
            cache.put(search, pokemon);
            cache.put(pokemon.getName(), pokemon);
            cache.put(String.valueOf(pokemon.getID()), pokemon);
            return pokemon;

        } catch (InterruptedException exception) {
            // preserve the interruption signal and report the the search stopped
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "Search interrupted.");

        } catch (IOException exception) {
            // handle connection failures, timeouts or bad api data
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Could not retrieve Pokemon data. Try again later.");
        }
    }

    // extracts the needed fields from pokeapi's jason and build the actual Pokemon
    // object
    private Pokemon parseResponse(String json) throws IOException {

        // read the json as a tree so we can access the fields by name
        JsonNode data = mapper.readTree(json);
        if (data == null || !data.hasNonNull("id") || !data.hasNonNull("name")) {
            throw new IOException("Invalid Pokemon response");
        }

        // extract each type's name
        List<String> types = new ArrayList<>();
        for (JsonNode entry : data.path("types")) {
            types.add(entry.path("type").path("name").asText());
        }

        // extract each ability's name
        List<String> abilities = new ArrayList<>();
        for (JsonNode entry : data.path("abilities")) {
            abilities.add(entry.path("ability").path("name").asText());
        }

        // match each statistic with its base value
        Map<String, Integer> stats = new LinkedHashMap<>();
        for (JsonNode entry : data.path("stats")) {
            stats.put(
                    entry.path("stat").path("name").asText(),
                    entry.path("base_stat").asInt());
        }

        // we prefer official artwork, but this will use the default sprite if it is
        // unavailable
        String imageUrl = data.path("sprites").path("other")
                .path("official-artwork").path("front_default").asText(null);

        if (imageUrl == null) {
            imageUrl = data.path("sprites").path("front_default").asText(null);
        }

        // Build the model, converting height to meters and weight to kilograms.
        return new Pokemon(
                data.path("id").asInt(),
                data.path("name").asText(),
                imageUrl,
                types,
                abilities,
                data.path("height").asDouble() / 10.0,
                data.path("weight").asDouble() / 10.0,
                stats);
    }
}
