package eu.pmav.deeplearningsubversivespellchecker;

import eu.pmav.Provider;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

public class Http {

    private static final String BASE_URI = "http://localhost:8080/";

    public static void main(String[] args) throws IOException {

        // Init provider.
        Provider.getInstance();

        final ResourceConfig resourceConfig = new ResourceConfig().packages("eu.pmav");
        GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), resourceConfig);

        System.out.println(String.format("Jersey app started with WADL available at %sapplication.wadl\nHit enter to stop it...", BASE_URI));
    }
}
