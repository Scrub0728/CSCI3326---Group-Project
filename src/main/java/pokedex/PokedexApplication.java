package pokedex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// this configures spring boot and automatically finds our controller and service
@SpringBootApplication
public class PokedexApplication {

    // entry point which starts the app and its web server
    public static void main(String[] args) {
        SpringApplication.run(PokedexApplication.class, args);
    }
}
