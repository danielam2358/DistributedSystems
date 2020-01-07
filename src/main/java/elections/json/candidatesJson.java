package elections.json;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public class candidatesJson {

    static String NAME_FIELD = "name";
    static JSONObject candidatesLists;

    static {
        JSONParser jsonParser = new JSONParser();

        File file = new File("src/main/java/elections/json/candidates.json");

        try {
            FileReader reader = new FileReader(file.getAbsolutePath());

            Object obj = jsonParser.parse(reader);

            JSONArray candidatesListsArray = (JSONArray) obj;
            candidatesLists = (JSONObject) candidatesListsArray.get(0);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public static boolean isCandidateValid(String state, String id){
        JSONObject stateCandidatesList = (JSONObject) candidatesLists.get(state);
        return stateCandidatesList.containsKey(id);
    }

    public static int numberOfCandidates(String state){
        JSONObject stateCandidatesList = (JSONObject) candidatesLists.get(state);
        return stateCandidatesList.size();
    }

    public static HashMap<String, String> getCandidates(String state){
        JSONObject stateCandidatesList = (JSONObject) candidatesLists.get(state);

        HashMap<String, String> candidates = new HashMap<>();

        stateCandidatesList.forEach( (id, name) -> {
            candidates.put(id.toString(), ((JSONObject)name).get(NAME_FIELD).toString());
        } );

        return candidates;
    }

    public static String getRandomCandidate(String state) {
        JSONObject stateCandidatesList = (JSONObject) candidatesLists.get(state);

        int stateCandidatesListSize = stateCandidatesList.size();

        Random random = new Random();
        int index = random.nextInt(stateCandidatesListSize);

        return stateCandidatesList.keySet().toArray()[index].toString();

    }


}
