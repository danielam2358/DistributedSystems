package elections;

import elections.REST.StateRestServer;
import elections.REST.VoterData;
import elections.RMI.StateRmiServer;
import elections.zookeeper.StateZookeeper;

import java.io.IOException;
import java.rmi.RemoteException;

public class State {

        private String zkPort;
        private String rmiPort;
        private String restPort;
        private String grpcPort;

        // onVote callback injection.
        private StateRestServer.OnVoteCallback callback;

        public State(String zkPort, String rmiPort, String restPort, String grpcPort) throws IOException {

                this.zkPort = zkPort;
                this.rmiPort = rmiPort;
                this.restPort = restPort;
                this.grpcPort = grpcPort;

                callback = (voter) -> {};

                startStateRmiServer();
                startStateZookeeper();
                startStateRestServer();

        }



        private void startStateRmiServer() throws RemoteException {
                // init RMI server
                StateRmiServer stateRmiServer = new StateRmiServer(rmiPort);
        }

        private void startStateZookeeper() throws IOException {
                // init State Zookeeper server
                StateZookeeper s = new StateZookeeper("127.0.0.1:" + zkPort, 50000);
                s.run();
        }

        private void startStateRestServer(){
                // init Rest Server
//                StateRestServer stateRestServer = new StateRestServer()
        }

}
