package elections;

import elections.REST.StateRestServer;
import elections.RMI.StateRmiServer;
import elections.gRPC.StateGrpcClient;
import elections.gRPC.StateGrpcServer;
import elections.zookeeper.StateZookeeper;

import java.io.IOException;
import java.rmi.RemoteException;

public class State {

        private static final String ZK_SERVER_ADDRESS = "127.0.0.1:2181";
        private static final int ZK_SESSION_TIMEOUT = 50000;

        private final String stateStr;

        private final String zkPort;
        private final String rmiPort;
        private final String restPort;
        private final String grpcPort;

        private StateRestServer stateRestServer;

        // onVote callback injection.
        private StateRestServer.OnVoteCallback onVote;

        // onLeaderElection callback injection.
        private StateZookeeper.OnLeaderElectionCallback onLeaderElection;

        // services objects.
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


                startStateGrpcServer();

                startStateGrpcClient();

                startStateZookeeper();

                startStateRmiServer();

                startStateRestServer();

        }



        private void startStateGrpcServer() throws IOException, InterruptedException {
                // init State gRPC server.
                this.stateGrpcServer = new StateGrpcServer();
                stateGrpcServer.start(grpcPort);
                stateGrpcServer.blockUntilShutdown();
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

        private void startStateRmiServer() throws RemoteException {
                // init RMI server
                this.stateRmiServer = new StateRmiServer(rmiPort);
        }

        private void startStateRestServer(){
                // init Rest Server
                // TODO
                onVote = (voter) -> {
                        System.out.println("hello" + voter.getName());
                };
                this.stateRestServer = new StateRestServer();
                stateRestServer.start(restPort, onVote);
        }


        public static void main(String[] args) throws IOException, InterruptedException {
                String stateStr = "NY";
                String zkPort = "2181";
                String rmiPort = "8991";
                String restPort = "8992";
                String grpcPort = "8993";
                State state = new State(stateStr, zkPort, rmiPort, restPort, grpcPort);
    }

}
