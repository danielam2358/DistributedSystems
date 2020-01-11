package elections.RMI;

import elections.REST.VoterData;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface CommitteeStateRmiInterface extends Remote{

    public void startElection() throws IOException, InterruptedException;

    public void stopElection() throws RemoteException, InterruptedException;

    public List<VoterData> getElectionStatus() throws RemoteException;

    public void terminateElection() throws RemoteException, InterruptedException;
}