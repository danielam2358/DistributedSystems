package elections;


import elections.RMI.CommitteeStateRmiInterface;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Committee {

    private static CommitteeStateRmiInterface look_up;

    public static void main(String[] args) throws IOException, NotBoundException, InterruptedException {


        String name = "StateRmiServer";

        // TODO
        int port = 8991;

        Registry registry = LocateRegistry.getRegistry(port);

        CommitteeStateRmiInterface lookup = (CommitteeStateRmiInterface) registry.lookup(name);

//        lookup.startElection();
//        System.out.println(lookup.getElectionStatus());
        lookup.stopElection();

    }
}
