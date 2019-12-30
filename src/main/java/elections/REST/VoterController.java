package elections.REST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class VoterController {
    private HashMap<String, VoterData> voters = new HashMap<>();

    VoterController() {

        // TODO: delete
        VoterData voter_0 = new VoterData("123", "jhon", "WDC", "1");
        VoterData voter_1 = new VoterData("321", "jhon", "WDC", "1");
        voters.put(voter_0.getId(), voter_0);
        voters.put(voter_1.getId(), voter_1);
    }


    @GetMapping("/voters")
    List<VoterData> all() {
        return new ArrayList<>(voters.values());
    }


    @PostMapping("/voters")
    VoterData newVoter(@RequestBody VoterData newVoter) {
        if(voters.containsKey(newVoter.getId())){
            return voters.get(newVoter.getId());
        }
        voters.put(newVoter.getId(), newVoter);
        return newVoter;
    }

    @PutMapping("/voters/{id}")
    VoterData replaceVote(@RequestBody VoterData newVoter, @PathVariable String id) {
        return voters.get(id).setVote(newVoter.getVote());
    }

}
