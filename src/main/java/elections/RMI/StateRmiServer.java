package elections.RMI;

import elections.REST.VoterData;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import java.util.List;


public class StateRmiServer extends UnicastRemoteObject  implements CommitteeStateRmiInterface{

    public interface OnStartElectionCallback {
        void callback() throws IOException, InterruptedException;
    }

    public interface OnStopElectionCallback {
        void callback() throws InterruptedException;
    }

    public interface OnReportElectionCallback {

        List<VoterData> callback();
    }

    public interface OnTerminateElectionCallback {
        void callback() throws InterruptedException;
    }

    private String port;

    private OnStartElectionCallback onStartElection;
    private OnStopElectionCallback onStopElection;
    private OnReportElectionCallback onReportElectionCallback;
    private OnTerminateElectionCallback onTerminateElectionCallback;


    public StateRmiServer(
            String rmiPort,
            OnStartElectionCallback onStartElection,
            OnStopElectionCallback onStopElection,
            OnReportElectionCallback onReportElectionCallback,
            OnTerminateElectionCallback onTerminateElectionCallback)
            throws RemoteException {
        super();

        this.port = rmiPort;
        this.onStartElection = onStartElection;
        this.onStopElection = onStopElection;
        this.onReportElectionCallback = onReportElectionCallback;
        this.onTerminateElectionCallback = onTerminateElectionCallback;

        String name = "StateRmiServer";
        Registry registry = LocateRegistry.createRegistry(Integer.parseInt(rmiPort));
        registry.rebind(name, this);
    }

    @Override
    public void startElection() throws IOException, InterruptedException {
        System.out.println("Start Election port " + port);
        onStartElection.callback();
    }

    @Override
    public void stopElection() throws RemoteException, InterruptedException {
        System.out.println("Stop Election port " + port);
        onStopElection.callback();
    }

    @Override
    public List<VoterData> getElectionStatus() throws RemoteException {
        System.out.println("Report Election port " + port);
        return onReportElectionCallback.callback();
    }

    @Override
    public void terminateElection() throws RemoteException, InterruptedException {
        System.out.println("Terminate Election port " + port);
        onReportElectionCallback.callback();
    }
}