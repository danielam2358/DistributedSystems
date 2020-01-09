package elections;

import elections.REST.VoterData;
import elections.json.candidatesJson;
import elections.json.serversJson;
import elections.json.votersJson;
import org.apache.zookeeper.server.quorum.Vote;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Voter {

    public static void Vote(String port, VoterData voteData) throws URISyntaxException {


        RestTemplate restTemplate = new RestTemplate();

        final String baseUrl = "http://127.0.0.1:" + port + "/voters";
        URI uri = new URI(baseUrl);

        HttpEntity<VoterData> request = new HttpEntity<>(voteData, new HttpHeaders());

        ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);

        //Verify request succeed
//        Assert.assertEquals(201, result.getStatusCodeValue());
    }

    public static void main(String[] args) throws URISyntaxException {

        while (true){

            try {
                VoterData voterData = votersJson.getRandomVoter();
                String port = serversJson.getRandomRestPort(voterData.getState());
                Vote(port, voterData);
                System.out.println(voterData);
                System.out.println(port);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}
