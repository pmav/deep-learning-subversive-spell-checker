package eu.pmav.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/spellchecker")
public class SpellcheckerEndpoint {

    @GET
    @Produces("text/plain")
    public Response doGet(@QueryParam("text") String text) {

        return Response.ok("Hello from WildFly Swarm! => " + text.toUpperCase()).build();

    }
}
