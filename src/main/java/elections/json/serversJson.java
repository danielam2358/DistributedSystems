package elections.json;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class serversJson {

    static JSONObject serversLists;

    static final String RMI_FIELD = "rmi_port";
    static final String GRPC_FIELD = "grpc_port";
    static final String REST_FIELD = "rest_port";


    static {
        JSONParser jsonParser = new JSONParser();

        File file = new File("src/main/java/elections/json/servers.json");

        try {
            FileReader reader = new FileReader(file.getAbsolutePath());

            Object obj = jsonParser.parse(reader);

            JSONArray serversListsArray = (JSONArray) obj;
            serversLists = (JSONObject) serversListsArray.get(0);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

//    public static boolean isCandidateValid(String state, String id){
//        JSONObject stateCandidatesList = (JSONObject) serversLists.get(state);
//        return stateCandidatesList.containsKey(id);
//    }
//
//    public static int numberOfCandidates(String state){
//        JSONObject stateCandidatesList = (JSONObject) serversLists.get(state);
//        return stateCandidatesList.size();
//    }

    private static JSONObject getRandomServer(String state) {
        JSONObject stateServersList = (JSONObject) serversLists.get(state);

        int stateServersListSize = stateServersList.size();

        Random random = new Random();
        int index = random.nextInt(stateServersListSize);

        return (JSONObject) stateServersList.get(String.valueOf(index));

    }

    public static List<String> getAllPorts(String state, String field){
        JSONObject stateServersList = (JSONObject) serversLists.get(state);
        List<String> rmiPortsLists = new ArrayList<>();

        stateServersList.forEach(
                (stateServerIndex, stateServer)  ->

                rmiPortsLists.add(
                        Integer.parseInt(stateServerIndex.toString()),
                        ((JSONObject)stateServer).get(field).toString()));

        return rmiPortsLists;
    }



    public static List<String> getAllRmiPorts(String state){
        return getAllPorts(state, RMI_FIELD);
    }

    public static List<String> getAllRestPorts(String state){
        return getAllPorts(state, REST_FIELD);
    }

    public static List<String> getAllGrpcPorts(String state){
        return getAllPorts(state, GRPC_FIELD);
    }

    public static String getRandomRestPort(String state){
        JSONObject stateServer = getRandomServer(state);

        return stateServer.get(REST_FIELD).toString();
    }

    public static String getRandomGrpcPort(String state){
        JSONObject stateServer = getRandomServer(state);

        return stateServer.get(GRPC_FIELD).toString();
    }


}
