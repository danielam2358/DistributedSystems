package elections.server.state;

import elections.client.voter.VoterController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.rsocket.RSocketProperties;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication

public class State {

    public static void main(String[] args) {
        new SpringApplicationBuilder(RSocketProperties.Server.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}

