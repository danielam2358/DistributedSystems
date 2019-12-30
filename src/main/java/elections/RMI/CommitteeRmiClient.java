package elections.RMI;


import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

class CommitteeRmiServer{

    private static CommitteeStateRmiInterface look_up;


    // TODO: after test rename main to startClient or something.
    public static void main(String[] args) throws RemoteException, NotBoundException {
//        if (System.getSecurityManager() == null) {
//            System.setSecurityManager(new SecurityManager());
//        }
//
        String name = "hello";
        Registry registry = LocateRegistry.getRegistry(1888);
        CommitteeStateRmiInterface lookup = (CommitteeStateRmiInterface) registry.lookup(name);
        lookup.startElection();

    }
}