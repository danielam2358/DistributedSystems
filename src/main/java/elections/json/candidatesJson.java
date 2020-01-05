package elections.json;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class candidatesJson {

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
}
