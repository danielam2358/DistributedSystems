package elections;

import elections.REST.VoterData;
import elections.json.candidatesJson;
import elections.json.votersJson;
import org.apache.zookeeper.server.quorum.Vote;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

public class Voter {

    public static void Vote(String port, String id, String name, String state, String vote) throws URISyntaxException {


        RestTemplate restTemplate = new RestTemplate();

        final String baseUrl = "http://127.0.0.1:" + port + "/voters";
        URI uri = new URI(baseUrl);
        
        VoterData voteData = new VoterData(id, name, state, vote);

        HttpEntity<VoterData> request = new HttpEntity<>(voteData, new HttpHeaders());

        ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);

        //Verify request succeed
//        Assert.assertEquals(201, result.getStatusCodeValue());
    }

    public static void main(String[] args) throws URISyntaxException {

        String port = "8992";
        String id = "1000246700";
        String name = "Kasia Nicol";
        String state = "Kentucky";
        String vote = candidatesJson.getRandomCandidate(state);

        System.out.println(vote);

        Vote(port, id, name, state, vote);
    }
}
