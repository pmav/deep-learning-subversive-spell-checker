package eu.pmav.api;

import eu.pmav.Provider;
import eu.pmav.predictor.Predictor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/spellchecker")
public class SpellcheckerEndpoint {

    @GET
    @Produces("text/plain")
    public Response doGet(@QueryParam("input") String input) {

        Predictor predictor = Provider.getInstance().getPredictor();
        String output = predictor.predict(input);

        return Response.ok(output).build();
    }
}
