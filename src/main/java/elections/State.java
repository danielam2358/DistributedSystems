package elections;

import elections.REST.StateRestServer;
import elections.REST.VoterData;
import elections.RMI.StateRmiServer;
import elections.gRPC.StateGrpcClient;
import elections.gRPC.StateGrpcServer;
import elections.json.candidatesJson;
import elections.json.serversJson;
import elections.json.votersJson;
import elections.zookeeper.StateZookeeper;

import org.json.simple.parser.ParseException;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.util.logging.Logger;


public class State {


        private static final Logger LOGGER = Logger.getLogger( State.class.getName() );

        Map<String, VoterData> votes = new HashMap<String, VoterData>();

        private static final String SERVER_ADDRESS = "127.0.0.1";
        private static final String ZK_SERVER_ADDRESS = SERVER_ADDRESS + ":2181";
        private static final int ZK_SESSION_TIMEOUT = 50000;

        private final String stateStr;

        private final String rmiPort;
        private final String restPort;
        private final String grpcPort;

        // onRestVote callback injection.
        private StateRestServer.OnRestVoteCallback onRestVote;

        // onGrpcVote callback injection
        private StateGrpcServer.OnGrpcVoteCallback onGrpcVote;

        // onLeaderElection callback injection.
        private StateZookeeper.OnLeaderElectionCallback onLeaderElection;

        // rmi state server callback injection.
        private StateRmiServer.OnStartElectionCallback onStartElection;
        private StateRmiServer.OnStopElectionCallback onStopElection;
        private StateRmiServer.OnReportElectionCallback onReportElectionCallback;



        // services objects.
        private StateRestServer stateRestServer;
        private StateRmiServer stateRmiServer;
        private StateZookeeper stateZookeeper;
        private StateGrpcServer stateGrpcServer;
        private StateGrpcClient stateGrpcClient;

        public State(String stateStr, String rmiPort, String restPort, String grpcPort) throws IOException, InterruptedException {

                this.stateStr = stateStr;

                this.rmiPort = rmiPort;
                this.restPort = restPort;
                this.grpcPort = grpcPort;

                startStateRmiServer();
        }



        private void startStateRmiServer() throws IOException, InterruptedException {

                onStartElection = () -> {

                        startStateGrpcServer();
                        startStateGrpcClient();
                        startStateZookeeper();
                        startStateRestServer();
                };

                onStopElection = () -> {
                        stateGrpcServer.stop();
                        stateGrpcClient.shutdown();
//                        stateZookeeper.stop();
                        stateRestServer.close();
                };

                onReportElectionCallback = () -> {
                        if(stateZookeeper.AmiLeader()){
                                return new ArrayList<>(votes.values());
                        }
                        return null;
                };

                // init RMI server
                this.stateRmiServer = new StateRmiServer(rmiPort, onStartElection, onStopElection, onReportElectionCallback);

                LOGGER.info(String.format("state %s: start rmi server on port %s", stateStr, rmiPort));
        }

        private void startStateGrpcServer() throws IOException, InterruptedException {

                onGrpcVote = this::manageStateVote;

                // init State gRPC server.
                this.stateGrpcServer = new StateGrpcServer();
                stateGrpcServer.start(grpcPort, onGrpcVote);

                LOGGER.info(String.format("state %s: start gRPC server listening on port %s", stateStr, grpcPort));

        }

        private void startStateGrpcClient() throws IOException {
                onLeaderElection = (leaderAddress) -> {
                        this.stateGrpcClient = new StateGrpcClient("127.0.0.1", leaderAddress);
                        LOGGER.info(String.format("state %s: start gRPC client on port %s", stateStr, leaderAddress));
                };

        }


        private void startStateZookeeper() throws IOException {

                // init State Zookeeper server
                this.stateZookeeper = new StateZookeeper(
                        this.stateStr,
                        grpcPort,
                        ZK_SERVER_ADDRESS,
                        ZK_SESSION_TIMEOUT,
                        onLeaderElection);
                stateZookeeper.run();
        }

        private void startStateRestServer(){
                // init Rest Server

                onRestVote = this::manageStateVote;

                this.stateRestServer = new StateRestServer();
                stateRestServer.start(restPort, onRestVote);

                LOGGER.info(String.format("state %s: start rest server on port %s", stateStr, restPort));
        }


        // get votes from rest server or from colleague state server.
        private void manageStateVote(VoterData voter){

//                LOGGER.info(String.format("manageStateVote"));
                LOGGER.info("manageStateVote");

                // voter is not valid
                if (!votersJson.isVoterValid(voter.getId(), voter.getState(), voter.getName())){
                        return;
                }

                // vote is not valid
                if (!candidatesJson.isCandidateValid(voter.getState(), voter.getVote())){
                        return;
                }

                // voter not from this server state
                if (!voter.getState().equals(stateStr)){
                        String port = serversJson.getRandomGrpcPort(voter.getState());
                        StateGrpcClient grpClient = new StateGrpcClient(SERVER_ADDRESS, port);
                        grpClient.vote(voter);
                }

                // server state is leader
                if (stateZookeeper.AmiLeader()){
                        votes.put(voter.getId(), voter);
                        spreadVotes();
                }

                // server state is not leader
                else {
                        this.stateGrpcClient.vote(voter);
                }
        }

        // update active replications in shard.
        private void spreadVotes(){
                //TODO

                List<String> colleagueGrpcStateServers = serversJson.getAllGrpcPorts(stateStr);
                System.out.println(colleagueGrpcStateServers);
                System.out.println(votes);
        }



        public static void main(String[] args) throws IOException, InterruptedException, ParseException {

                String stateStr = "Kentucky";

                List<String> rmiPorts = serversJson.getAllRmiPorts(stateStr);
                List<String> restPorts = serversJson.getAllRestPorts(stateStr);
                List<String> grpcPorts = serversJson.getAllGrpcPorts(stateStr);

                for(int i = 0; i< rmiPorts.size(); i++){
                        String rmiPort = rmiPorts.get(i);
                        String restPort = restPorts.get(i);
                        String grpcPort = grpcPorts.get(i);
                        State state = new State(stateStr, rmiPort, restPort, grpcPort);
                }

                LOGGER.info(String.format("start %s servers.", stateStr));

    }

}
