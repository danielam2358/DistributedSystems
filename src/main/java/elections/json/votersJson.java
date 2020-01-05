package elections.json;

import elections.REST.VoterData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class votersJson {

    static JSONObject votersList;
    static String STATE_FIELD = "state";
    static String NAME_FIELD = "name";


    private static int voterListSize;

    static {
        JSONParser jsonParser = new JSONParser();

        File file = new File("src/main/java/elections/json/voters.json");

        try {
            FileReader reader = new FileReader(file.getAbsolutePath());

            Object obj = jsonParser.parse(reader);

            JSONArray votersListsArray = (JSONArray) obj;
            votersList = (JSONObject) votersListsArray.get(0);
            voterListSize = votersList.size();

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }


    }

    public static boolean isVoterValid(String id, String state, String name){
        if (!votersList.containsKey(id)){
            return false;
        }
        JSONObject voter = (JSONObject) votersList.get(id);
        return voter.get(STATE_FIELD).toString().equals(state) && voter.get(NAME_FIELD).toString().equals(name);
    }

    public static VoterData getRandomVoter(){
        Random random = new Random();
        int index = random.nextInt(voterListSize);

        String id = votersList.keySet().toArray()[index].toString();

        JSONObject voter = (JSONObject) votersList.get(id);

        String name = voter.get(NAME_FIELD).toString();
        String state = voter.get(STATE_FIELD).toString();
        String vote = candidatesJson.getRandomCandidate(state);

        return new VoterData(id, name, state, vote);
    }
}
