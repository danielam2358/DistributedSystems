package elections.REST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class VoterController {

    private HashMap<String, VoterData> voters = new HashMap<>();



    private StateRestServer.Handler handler;

    VoterController(StateRestServer.Handler bean) {
        this.handler = bean;
    }


    @GetMapping("/voters")
    List<VoterData> all() {
        return new ArrayList<>(voters.values());
    }


    @PostMapping("/voters")
    VoterData newVoter(@RequestBody VoterData newVoter) {
        voters.put(newVoter.getId(), newVoter);
        try {
            handler.onVote(newVoter);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return newVoter;
    }

//    @PutMapping("/voters/{id}")
//    VoterData replaceVote(@RequestBody VoterData newVoter, @PathVariable String id) {
//        return voters.get(id).setVote(newVoter.getVote());
//    }

}
