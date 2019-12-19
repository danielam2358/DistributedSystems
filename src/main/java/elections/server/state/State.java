package elections.server.state;

import elections.client.voter.VoterController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@ComponentScan(basePackageClasses = VoterController.class)
public class State {

    public static void main(String[] args) {
        SpringApplication.run(State.class, args);
    }
}

