package pokedex;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// this handles requests to api/pokemon and returns the Pokemon's data as json
@RestController
@RequestMapping("/api/pokemon")

public class PokemonController {
    private final PokemonService service;

    // spring auto provides the service through this constructor
    public PokemonController(PokemonService service) {
        this.service = service;
    }

    // reads the search query and asks the service to find the specific Pokenon
    // ex: GET /api/pokemon?query=pikachu
    @GetMapping
    public Pokemon searchPokemon(@RequestParam(defaultValue = "") String query) {
        return service.getPokemon(query);
    }
}
