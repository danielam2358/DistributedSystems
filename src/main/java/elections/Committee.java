package elections;


import elections.REST.VoterData;
import elections.RMI.CommitteeStateRmiInterface;
import elections.json.candidatesJson;
import elections.json.serversJson;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Collections.max;

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
        AtomicBoolean reported = new AtomicBoolean(false);

        lookupMap.get(state).forEach( lookUp -> {
            List<VoterData> status = null;
            try {
                status = lookUp.getElectionStatus();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (status != null && !reported.get()){
                reported.set(true);

                HashMap<String, Integer> votes = new HashMap<>();

                HashMap<String, String> candidates = candidatesJson.getCandidates(state);

                candidates.keySet().forEach( candidateId -> {
                    votes.put(candidateId, 0);
                });

                status.forEach(voterData -> {
                    String candidateId = voterData.getVote();
                    Integer currentVote = votes.get(candidateId);
                    votes.put(candidateId, currentVote + 1);
                });

                votes.forEach( (candidateId, numberOfVotes) -> {
                    System.out.println(
                            String.format("state %s\t candidate %s\t votes %d",
                                    state,
                                    candidates.get(candidateId),
                                    votes.get(candidateId)
                                    )
                    );
                });

                String WinnerCandidateId = max(votes.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
                System.out.println(
                        String.format("\nstate %s\t candidate %s\t is winner with %d votes\n",
                        state,
                        candidates.get(WinnerCandidateId),
                        votes.get(WinnerCandidateId)
                        )
                );
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

    public static void downServer() throws RemoteException, InterruptedException {
        Config.statesString.forEach( state -> {
            CommitteeStateRmiInterface lookup = lookupMap.get(state).remove(0);
            try {
                lookup.stopElection();
            } catch (RemoteException | InterruptedException e) {
                e.printStackTrace();
            }
        });

    }



    public static void main(String[] args) throws IOException, NotBoundException, InterruptedException {

        init();

        Scanner scan = new Scanner(System.in);
        boolean serversUp = false;
        String cmd = "";

        System.out.println("commands: init, start, stop, report, quit, down");

        while (!cmd.equals("quit")){
            cmd = scan.next();

            switch (cmd){

                case "init": {
                    init();
                }
                break;

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

                case "down":{
                    downServer();
                }
                    break;

                case "quit":
                    break;

                default:
                    System.out.println("commands: start, stop, report, quit");
            }
        }

        report();

        if (serversUp){
            stop();
        }

    }
}
