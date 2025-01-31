package elections.zookeeper;

import elections.REST.VoterData;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class StateZookeeper implements Runnable{

    private OnLeaderElectionCallback onLeaderElectionCallback;

    public interface OnLeaderElectionCallback {
        void updateLeaderAddress(String leaderAddress);
    }

    private static final String ROOT_NODE = "/election";
    private final static String LEADER_ELECTION_NODE = "/leader_election";
    private final static String LEADER_ELECTION_NODE_SUFFIX = "/state_number_";
    private final static String COMMIT_VOTE_NODE = "/votes";
    private final static String COMMIT_VOTE_NODE_SUFFIX = "/vote_";

    private final String stateNode;
    private final String leaderElectionNode;
    private final String commitVotesNode;

    private ZooKeeperWrapper zooKeeperWrapper;

    // hold state of leadership
    private boolean amiLeader = false;

    // hold current path to server node in leader elections
    private String createdServerNode = null;

    // hold current path to leader node in leader elections
    private String createdLeaderNodePath;

    // hold the address of state server
    private String myAddress;
    private String leaderAddress;


    // instantiate StateZookeeper class with uniq connectString
    public StateZookeeper(String stateStr, String address, String connectString, int sessionTimeout,
                          OnLeaderElectionCallback onLeaderElectionCallback) throws IOException {
        stateNode = ROOT_NODE + "/" + stateStr;
        leaderElectionNode = stateNode + LEADER_ELECTION_NODE + LEADER_ELECTION_NODE_SUFFIX;
        commitVotesNode = stateNode + COMMIT_VOTE_NODE + COMMIT_VOTE_NODE_SUFFIX;
        this.myAddress = address;
        this.onLeaderElectionCallback = onLeaderElectionCallback;
        this.zooKeeperWrapper = new ZooKeeperWrapper(connectString, sessionTimeout, new NodeDeleteWatcher());
    }

    public void stop() throws InterruptedException {
        this.zooKeeperWrapper.stop();
    }

    public boolean AmiLeader(){
        return this.amiLeader;
    }

    private void createRootsNode(){
        this.zooKeeperWrapper.createNode(ROOT_NODE, false, CreateMode.PERSISTENT);
        this.zooKeeperWrapper.createNode(stateNode, false, CreateMode.PERSISTENT);
        this.zooKeeperWrapper.createNode(stateNode + LEADER_ELECTION_NODE, false, CreateMode.PERSISTENT);
        this.zooKeeperWrapper.createNode(stateNode + COMMIT_VOTE_NODE, false, CreateMode.PERSISTENT);
    }

    private void createServerNode(){
        if(this.createdServerNode == null){
            final String node = leaderElectionNode;
            final boolean watch = false;
            final CreateMode createdMode = CreateMode.EPHEMERAL_SEQUENTIAL;
            this.createdServerNode = this.zooKeeperWrapper.createNode(node, watch, createdMode);
        }

    }

     public String createCommitVoteNode(VoterData voterData) {
        final String node = commitVotesNode + voterData.getId() + voterData.getVote();
        final boolean watch = false;
        final CreateMode createdMode = CreateMode.EPHEMERAL;
         return this.zooKeeperWrapper.createNode(node, watch, createdMode);
    }

    public void deleteCommitVoteNode(String node) throws KeeperException, InterruptedException {
        this.zooKeeperWrapper.deleteNode(node);
    }



    public boolean waitForCommitVoteNode(VoterData voterData) throws KeeperException, InterruptedException {
        final String node = commitVotesNode + voterData.getId() + voterData.getVote();

        while (true) {
                if (!this.zooKeeperWrapper.isNodeExist(node)) {
                    return true;
            }
        }
    }


    private void startLeaderElection(){
        createServerNode();
        try {
            this.zooKeeperWrapper.setAddress(createdServerNode, myAddress);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }

        // get all LEADER_ELECTION_NODE child nodes
        final List<String> childNodePaths = this.zooKeeperWrapper.getChildren(stateNode + LEADER_ELECTION_NODE );
        System.out.println(childNodePaths);

        // sort by name
        Collections.sort(childNodePaths);

        // update createdLeaderNode
        createdLeaderNodePath = stateNode + LEADER_ELECTION_NODE  + "/" + childNodePaths.get(0);

        // am i a leader?
        this.amiLeader = createdLeaderNodePath.equals(createdServerNode);

        // watch on leader node
        this.zooKeeperWrapper.watchNode(createdLeaderNodePath);

        try {
            leaderAddress = this.zooKeeperWrapper.getAddress(createdLeaderNodePath);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }

        onLeaderElectionCallback.updateLeaderAddress(leaderAddress);
    }

    // this class implement Watcher of delete node event: this means that a state server failed.
    public class NodeDeleteWatcher implements Watcher {

        @Override
        public void process(WatchedEvent event) {
            final Event.EventType eventType = event.getType();
            if(Event.EventType.NodeDeleted.equals(eventType)) {
                if(event.getPath().equalsIgnoreCase(createdLeaderNodePath)) {
                    startLeaderElection();
                }
            }
        }
    }

    @Override
    public void run() {
        createRootsNode();
        startLeaderElection();
    }

}

