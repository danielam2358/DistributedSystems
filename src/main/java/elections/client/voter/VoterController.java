package elections.client.voter;




import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.*;


@RestController
public class VoterController {
    private HashMap<String, VoterData> voters = new HashMap<>();

    VoterController() {
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
