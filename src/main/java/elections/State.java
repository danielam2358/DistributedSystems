package elections;

import elections.REST.StateRestServer;
import elections.REST.VoterData;
import elections.RMI.StateRmiServer;
import elections.gRPC.StateGrpcClient;
import elections.gRPC.StateGrpcServer;
import elections.json.candidatesJson;
import elections.json.votersJson;
import elections.zookeeper.StateZookeeper;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.*;

public class State {

        Map<String, VoterData> votes = new HashMap<String, VoterData>();

        private static final String ZK_SERVER_ADDRESS = "127.0.0.1:2181";
        private static final int ZK_SESSION_TIMEOUT = 50000;

        private final String stateStr;

        private final String zkPort;
        private final String rmiPort;
        private final String restPort;
        private final String grpcPort;

        // onVote callback injection.
        private StateRestServer.OnVoteCallback onVote;

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

        public State(String stateStr, String zkPort, String rmiPort, String restPort, String grpcPort) throws IOException, InterruptedException {

                this.stateStr = stateStr;

                this.zkPort = zkPort;
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
                        stateZookeeper.stop();
                        stateRestServer.close();
                };

                onReportElectionCallback = () -> {
                        if(stateZookeeper.AmiLeader()){
                                return (List<VoterData>)  votes.values();
                        }
                        return null;
                };

                // init RMI server
                this.stateRmiServer = new StateRmiServer(rmiPort, onStartElection, onStopElection, onReportElectionCallback);
        }

        private void startStateGrpcServer() throws IOException, InterruptedException {
                // init State gRPC server.
                this.stateGrpcServer = new StateGrpcServer();
                stateGrpcServer.start(grpcPort);

        }

        private void startStateGrpcClient() throws IOException {
                onLeaderElection = (leaderAddress) -> {
                        int port = Integer.parseInt(leaderAddress);
                        this.stateGrpcClient = new StateGrpcClient("127.0.0.1", port);
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

                onVote = (voter) -> {

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
                                //TODO
                        }

                        // server state is leader
                        if (stateZookeeper.AmiLeader()){
                                votes.put(voter.getId(), voter);
                        }

                        // server state is not leader
                        else {
//                                this.stateGrpcClient. // TODO
                        }
                };
                this.stateRestServer = new StateRestServer();
                stateRestServer.start(restPort, onVote);
        }


        public static void main(String[] args) throws IOException, InterruptedException, ParseException {
//                String stateStr = "NY";
//                String zkPort = "2181";
//                String rmiPort = "8991";
//                String restPort = "8992";
//                String grpcPort = "8993";
//                State state = new State(stateStr, zkPort, rmiPort, restPort, grpcPort);

    }

}
