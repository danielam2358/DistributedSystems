package elections.REST;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;


@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
//@ComponentScan(basePackageClasses = VoterController.class)
public class StateRestServer {

    private ConfigurableApplicationContext context;

    public interface OnRestVoteCallback {
        void callback(VoterData newVoter);
    }

    @Component
    public static class Handler {

        private OnRestVoteCallback onRestVoteCallback;

        void init(OnRestVoteCallback onRestVoteCallback){
            this.onRestVoteCallback = onRestVoteCallback;
        }

        void onVote(VoterData newVoter){
            this.onRestVoteCallback.callback(newVoter);
        }

    }

   public void start(String restPort, OnRestVoteCallback callback){
        HashMap<String, Object> props = new HashMap<>();
        props.put("server.port", Integer.parseInt(restPort));

        this.context = new SpringApplicationBuilder()
                .sources(StateRestServer.class)
                .properties(props)
                .run("");

       Handler handler = context.getBean(Handler.class);

       handler.init(callback);
    }

    public void close(){
        context.close();
    }

}

