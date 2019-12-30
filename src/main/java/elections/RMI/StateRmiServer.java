package elections.RMI;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//TODO: finish

class StateRmiServer extends UnicastRemoteObject  implements CommitteeStateRmiInterface{

    public StateRmiServer() throws RemoteException { super(); }

    @Override
    public void startElection() throws RemoteException {
        System.out.println("startElection");
    }

    @Override
    public void stopElection() throws RemoteException {
        System.out.println("stopElection");
    }

    @Override
    public List<String> getElectionStatus() throws RemoteException {
        System.out.println("getElectionStatus");
        ArrayList<String> a = new ArrayList<String>();
        a.add("a");
        a.add("b");
        a.add("c");
        return a;
    }



    // TODO: after test rename main to startServer or something.
    public static void main(String[] args){
        try {
//            Registry rgsty = LocateRegistry.createRegistry(1888);

            String name = "StateRmiServer";
            StateRmiServer server = new StateRmiServer();
//            StateRmiServer stub = (StateRmiServer) UnicastRemoteObject.exportObject(server, 1888);
//            Registry registry = LocateRegistry.getRegistry();
//            registry.rebind(name, stub);
//            Naming.rebind(name, server);

            Registry rgsty = LocateRegistry.createRegistry(1888);
            rgsty.rebind("hello", server);
            System.out.println("Server ready");

        } catch (Exception e) {

            System.out.println("Server exception: " + e.toString());
            e.printStackTrace();

        }

    }
}