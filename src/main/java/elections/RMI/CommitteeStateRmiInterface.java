package elections.RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface CommitteeStateRmiInterface extends Remote{

    public void startElection() throws RemoteException;

    public void stopElection() throws RemoteException;

    // TODO: List<String>? decide.
    public List<String> getElectionStatus() throws RemoteException;
}