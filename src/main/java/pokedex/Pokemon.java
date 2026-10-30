package pokedex;

import java.util.List;
import java.util.Map;

// this should make creating the frontend much easier
// this will contain the responses returned by backend
public class Pokemon {

    // private to keep the data encapsulated
    private final int id;
    private final String name;
    private final String imageURL;
    private final List<String> types;
    private final List<String> abilities;
    private final double height; // this is in meters
    private final double weight; // this is in kilograms

    // maps each stat's name to its base value, like "hp" to 35
    private final Map<String, Integer> stats;

    // initializes a Pokemon with the data extracted by PokemonService
    public Pokemon(int id, String name, String imageURL, List<String> types, List<String> abilities,
            double height, double weight, Map<String, Integer> stats) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;

        // this should copy collections so outside code doesn't change the stored data
        this.types = List.copyOf(types);
        this.abilities = List.copyOf(abilities);

        this.height = height;
        this.weight = weight;
        this.stats = Map.copyOf(stats);
    }

    // spring uses these getter to turn object into json
    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public List<String> getTypes() {
        return types;
    }

    public List<String> getAbilities() {
        return abilities;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public Map<String, Integer> getStats() {
        return stats;
    }
}
