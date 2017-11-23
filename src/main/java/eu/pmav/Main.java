package eu.pmav;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.jaxrs.JAXRSArchive;

public class Main {

    public static void main(String[] args) throws Exception {

        // Use this main method to run the App without the normal overload of the goal "wildfly-swarm:run".

        // Instantiate the container.
        Swarm swarm = new Swarm();

        // Create deployments.
        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class);
        deployment
                //.addAsResource(Main.class.getResource("/training-data-pt/data.txt"), "/training-data-pt/data.txt")
                .addPackages(true, Main.class.getPackage())
                .addAllDependencies();

        swarm.start();
        swarm.deploy(deployment);
    }
}