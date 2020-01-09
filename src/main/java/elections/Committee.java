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
    private static ArrayList<Process> processPoolStates;
    private static ArrayList<Process> processPoolVotes = new ArrayList<>();


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

    private static void startVotes() throws IOException {
        String cmd = "java -jar ./out/artifacts/voter_jar/ELECTIONS.jar";
        processPoolVotes.add(Runtime.getRuntime().exec(cmd));
    }

    private static void stopVotes() throws IOException {
        processPoolVotes.forEach(Process::destroy);
    }

    private static void startServers(){
        processPoolStates = new ArrayList<>();

        Config.statesString.forEach( stateStr -> {

            System.out.println(String.format("start %s servers.", stateStr));

            List<String> rmiPorts = serversJson.getAllRmiPorts(stateStr);
            List<String> restPorts = serversJson.getAllRestPorts(stateStr);
            List<String> grpcPorts = serversJson.getAllGrpcPorts(stateStr);

            for(int i = 0; i< rmiPorts.size(); i++) {
                String rmiPort = rmiPorts.get(i);
                String restPort = restPorts.get(i);
                String grpcPort = grpcPorts.get(i);

                String baseCmd = "java -jar ./out/artifacts/state_jar/ELECTIONS.jar";
                String cmd = baseCmd + String.format(" %s %s %s %s", stateStr, rmiPort, restPort, grpcPort);

                try {
                    processPoolStates.add(Runtime.getRuntime().exec(cmd));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    private static void init(){
        startServers();
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

        String helpMessage = "commands: init, start, stop, report, quit, down, startV, stopV";

        System.out.println(helpMessage);

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

                case "startV": {
                        startVotes();
                }

                case "stopV": {
                        stopVotes();
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
                    System.out.println(helpMessage);
            }
        }

        report();

        if (serversUp){
            stop();
        }

        processPoolStates.forEach(Process::destroy);

    }
}
