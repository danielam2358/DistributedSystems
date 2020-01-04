package elections;

import elections.REST.StateRestServer;
import elections.REST.VoterData;
import elections.RMI.StateRmiServer;
import elections.gRPC.StateGrpcClient;
import elections.gRPC.StateGrpcServer;
import elections.zookeeper.StateZookeeper;

import java.io.IOException;
import java.rmi.RemoteException;
import org.json.*;

public class State {

        private final String stateStr;

        private final String zkPort;
        private final String rmiPort;
        private final String restPort;
        private final String grpcPort;

        private StateRestServer stateRestServer;

        // onVote callback injection.
        private StateRestServer.OnVoteCallback callback;

        // services objects.
        private StateRmiServer stateRmiServer;
        private StateZookeeper stateZookeeper;
        private StateGrpcServer stateGrpcServer;
        private StateGrpcClient stateGrpcClient;

        public State(String stateStr, String zkPort, String rmiPort, String restPort, String grpcPort) throws IOException {

                this.stateStr = stateStr;

                this.zkPort = zkPort;
                this.rmiPort = rmiPort;
                this.restPort = restPort;
                this.grpcPort = grpcPort;

                callback = (voter) -> {
                        System.out.println("hello" + voter.getName());
                };

                startStateRestServer();
                startStateRmiServer();
                startStateZookeeper();

        }



        private void startStateRestServer(){
                // init Rest Server
                this.stateRestServer = new StateRestServer();
                stateRestServer.start(restPort, callback);
        }

        private void startStateRmiServer() throws RemoteException {
                // init RMI server
                this.stateRmiServer = new StateRmiServer(rmiPort);
        }

        private void startStateZookeeper() throws IOException {

                String stateStr = this.stateStr;
                String address = "BAnana";
                String zkServerAddress = "127.0.0.1:2181";
                int sessionTimeout = 50000;

                // init State Zookeeper server
                this.stateZookeeper = new StateZookeeper(stateStr, address, zkServerAddress, sessionTimeout);
                stateZookeeper.run();
        }

        private void startStateGrpcServer() throws IOException {
                // init State gRPC server.
                this.stateGrpcServer = new StateGrpcServer();
        }

        private void startState() throws IOException {
                // init State gRPC client.
                //TODO
//                this.stateGrpcClient = new StateGrpcClient();
        }


        public static void main(String[] args) throws IOException {
                String stateStr = "NY";
                String zkPort = "2181";
                String rmiPort = "8991";
                String restPort = "8992";
                String grpcPort = "8993";
                State state = new State(stateStr, zkPort, rmiPort, restPort, grpcPort);
    }

}
