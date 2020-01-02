package elections.REST;

import elections.State;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;


@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
//@ComponentScan(basePackageClasses = VoterController.class)
public class StateRestServer {

    public interface OnVoteCallback {
        void callback(VoterData newVoter);
    }

    @Component
    public static class Handler {

        private OnVoteCallback onVoteCallback;

        void init(OnVoteCallback onVoteCallback){
            this.onVoteCallback = onVoteCallback;
        }

        void onVote(VoterData newVoter){
            this.onVoteCallback.callback(newVoter);
        }

    }

   static void start(String restPort, OnVoteCallback callback){
        HashMap<String, Object> props = new HashMap<>();
        props.put("server.port", Integer.parseInt(restPort));

        ConfigurableApplicationContext context = new SpringApplicationBuilder()
                .sources(StateRestServer.class)
                .properties(props)
                .run("");

       Handler handler = context.getBean(Handler.class);

       handler.init(callback);
    }

    public static void main(String[] args) {
        start("9999" , (voter)->{});
    }
}

