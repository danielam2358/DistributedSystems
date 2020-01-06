package elections;


import elections.REST.VoterData;
import elections.RMI.CommitteeStateRmiInterface;
import elections.json.serversJson;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class Committee {

    private static CommitteeStateRmiInterface look_up;

    private static HashMap<String, List<CommitteeStateRmiInterface>> lookupMap = new HashMap<>();

    private static CommitteeStateRmiInterface serverLookupInit(String port){
        String name = "StateRmiServer";
        int intPort = Integer.parseInt(port);

        try {
            Registry registry = LocateRegistry.getRegistry(intPort);
            return (CommitteeStateRmiInterface) registry.lookup(name);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private static void stateLookupInit(String state){

        List<String> stateRmiPorts = serversJson.getAllRmiPorts(state);

        List<CommitteeStateRmiInterface> stateLookup = new ArrayList<>();

        stateRmiPorts.forEach( port -> {
            stateLookup.add(serverLookupInit(port));
        });

        lookupMap.put(state, stateLookup);
    }

    private static void stateStartElection(String state){
        lookupMap.get(state).forEach( lookUp -> {
            try {
                lookUp.startElection();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private static void stateStopElection(String state){
        lookupMap.get(state).forEach( lookUp -> {
            try {
                lookUp.stopElection();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private static void stateReportElection(String state){
        lookupMap.get(state).forEach( lookUp -> {
            try {
                // TODO
                List<VoterData> status = lookUp.getElectionStatus();
                System.out.println(status);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }



    private static void init(){
        Config.statesString.forEach(Committee::stateLookupInit);
    }


    public static void start(){
        Config.statesString.forEach(Committee::stateStartElection);
    }

    public static void stop(){
        Config.statesString.forEach(Committee::stateStopElection);
    }

    public static void report(){
        Config.statesString.forEach(Committee::stateReportElection);
    }



    public static void main(String[] args) throws IOException, NotBoundException, InterruptedException {

        init();

        Scanner scan = new Scanner(System.in);
        boolean serversUp = false;
        String cmd = "";

        System.out.println("commands: start, stop, report, quit");

        while (!cmd.equals("quit")){
            cmd = scan.next();

            switch (cmd){

                case "start": {
                        serversUp = true;
                        start();
                }
                    break;

                case "stop": {
                        serversUp = false;
                        stop();
                }
                    break;

                case "report": {
                    report();
                }
                    break;

                case "quit":
                    break;

                default:
                    System.out.println("commands: start, stop, report, quit");
            }
        }

        if (serversUp){
            stop();
        }

    }
}
