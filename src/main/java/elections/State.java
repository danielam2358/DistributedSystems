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

import org.apache.zookeeper.KeeperException;
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
        private StateGrpcServer.OnGrpcCommitVoteCallback onGrpcCommitVote;

        // onLeaderElection callback injection.
        private StateZookeeper.OnLeaderElectionCallback onLeaderElection;

        // rmi state server callback injection.
        private StateRmiServer.OnStartElectionCallback onStartElection;
        private StateRmiServer.OnStopElectionCallback onStopElection;
        private StateRmiServer.OnReportElectionCallback onReportElectionCallback;
        private StateRmiServer.OnTerminateElectionCallback onTerminateElectionCallback;



        // services objects.
        private StateRestServer stateRestServer;
        private StateRmiServer stateRmiServer;
        private StateZookeeper stateZookeeper;
        private StateGrpcServer stateGrpcServer;
        private StateGrpcClient stateGrpcClient;
        private List<StateGrpcClient> stateGrpcClientList;

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
                        if(stateZookeeper.AmiLeader()){
                                stateGrpcClientList.forEach(StateGrpcClient -> {
                                        try {
                                                StateGrpcClient.shutdown();
                                        } catch (InterruptedException e) {
                                                e.printStackTrace();
                                        }
                                });
                        }
                        else {
                                stateGrpcClient.shutdown();
                        }
                        stateZookeeper.stop();
                        stateRestServer.close();
                };

                onReportElectionCallback = () -> {
                        if(stateZookeeper.AmiLeader()){
                                return new ArrayList<>(votes.values());
                        }
                        return null;
                };

                onTerminateElectionCallback = () -> {
                        System.exit(0);
                };

                // init RMI server
                this.stateRmiServer = new StateRmiServer(rmiPort,
                        onStartElection,
                        onStopElection,
                        onReportElectionCallback,
                        onTerminateElectionCallback
                        );

                LOGGER.info(String.format("state %s: start rmi server on port %s", stateStr, rmiPort));
        }

        private void startStateGrpcServer() throws IOException, InterruptedException {

                // when leader get vote request
                onGrpcVote = this::manageStateVote;

                // when server (not leader) get commit vote request
                onGrpcCommitVote = this::getVoteCommit;

                // init State gRPC server.
                this.stateGrpcServer = new StateGrpcServer();
                stateGrpcServer.start(grpcPort, onGrpcVote, onGrpcCommitVote);

                LOGGER.info(String.format("state %s: start gRPC server listening on port %s", stateStr, grpcPort));

        }

        private void startStateGrpcClient() throws IOException {
                onLeaderElection = (leaderAddress) -> {

                        // im the leader!
                        if(grpcPort.equals(leaderAddress)) {
                                this.stateGrpcClient = null;

                                List<String> grpcPorts = serversJson.getAllGrpcPorts(stateStr);

                                stateGrpcClientList = new ArrayList<>();

                                grpcPorts.forEach( (grpcPort) -> {
                                        if(!grpcPort.equals(this.grpcPort)){
                                                stateGrpcClientList.add(new StateGrpcClient("127.0.0.1", grpcPort));
                                        }
                                });

                                LOGGER.info(String.format("state %s: start gRPC client on port %s", stateStr, leaderAddress));
                        }

                        // im not the leader!
                        else {
                                this.stateGrpcClient = new StateGrpcClient("127.0.0.1", leaderAddress);
                                LOGGER.info(String.format("state %s: start gRPC client on port %s", stateStr, leaderAddress));
                                stateGrpcClientList = null;
                        }

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
        private void manageStateVote(VoterData voterData) throws KeeperException, InterruptedException {

                LOGGER.info("manageStateVote");

                // voter is not valid
                if (!votersJson.isVoterValid(voterData.getId(), voterData.getState(), voterData.getName())){
                        return;
                }

                // vote is not valid
                if (!candidatesJson.isCandidateValid(voterData.getState(), voterData.getVote())){
                        return;
                }

                // voter not from this server state
                if (!voterData.getState().equals(stateStr)){
                        String port = serversJson.getRandomGrpcPort(voterData.getState());
                        StateGrpcClient grpClient = new StateGrpcClient(SERVER_ADDRESS, port);
                        grpClient.vote(voterData);
                }

                // server state is leader
                if (stateZookeeper.AmiLeader()){
                        spreadVote(voterData);
                }

                // server state is not leader
                else {
                        this.stateGrpcClient.vote(voterData);
                }
        }

        // update active replications in shard.
        private void spreadVote(VoterData voterData) throws KeeperException, InterruptedException {

                String createdNode = stateZookeeper.createCommitVoteNode(voterData);

                stateGrpcClientList.forEach(stateGrpcClient -> {
                        try {
                                stateGrpcClient.commitVote(voterData);
                        } catch (InterruptedException e) {
                                e.printStackTrace();
                        }
                });

                stateZookeeper.deleteCommitVoteNode(createdNode);

                votes.put(voterData.getId(), voterData);
        }

        // (not leader) get vote and wait for confirm from leader that vote can be count.
        private void getVoteCommit(VoterData voterData) throws KeeperException, InterruptedException {

                stateZookeeper.waitForCommitVoteNode(voterData);
                votes.put(voterData.getId(), voterData);
        }



        public static void main(String[] args) throws IOException, InterruptedException, ParseException {

                String stateStr = args[0];
                String rmiPort = args[1];
                String restPort = args[2];
                String grpcPort = args[3];

                new State(stateStr, rmiPort, restPort, grpcPort);
                LOGGER.info(String.format("start %s servers.", stateStr));

    }

}
