package elections.REST;

import elections.REST.VoterController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
//@ComponentScan(basePackageClasses = VoterController.class)
public class StateRestServer {

    public static void main(String[] args) {
        SpringApplication.run(StateRestServer.class, args);
    }
}

