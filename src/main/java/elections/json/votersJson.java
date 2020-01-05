package elections.json;

import com.google.gson.JsonObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class votersJson {

    static JSONObject votersLists;

    static {
        JSONParser jsonParser = new JSONParser();

        File file = new File("src/main/java/elections/json/voters.json");

        try {
            FileReader reader = new FileReader(file.getAbsolutePath());

            Object obj = jsonParser.parse(reader);

            JSONArray votersListsArray = (JSONArray) obj;
            votersLists = (JSONObject) votersListsArray.get(0);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }


    }

    public static boolean isVoterValid(String state, String id){
        JSONObject stateVotersList = (JSONObject) votersLists.get(state);
        return stateVotersList.containsKey(id);
    }

}
