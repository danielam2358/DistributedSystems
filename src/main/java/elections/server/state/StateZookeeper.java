package elections.server.state;




import elections.server.ZooKeeperWrapper;
import jdk.nashorn.internal.objects.annotations.Property;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class StateZookeeper implements Runnable{


    private static final String ROOT_NODE = "/election";
    private static final String STATE_NODE = ROOT_NODE + "/NY"; // TODO: how to pass?
    private static final String LEADER_ELECTION_NODE_SUFFIX = "/state_number_";
    private static final String LEADER_ELECTION_NODE = STATE_NODE + LEADER_ELECTION_NODE_SUFFIX;

    private ZooKeeperWrapper zooKeeperWrapper;

    // hold state of leadership
    private boolean amiLeader = false;

    // hold current path to server node in leader elections
    private String createdServerNode;

    // hold current path to leader node in leader elections
    private String createdLeaderNodePath;


    // instantiate StateZookeeper class with uniq connectString
    public StateZookeeper(String connectString, int sessionTimeout) throws IOException {
        this.zooKeeperWrapper = new ZooKeeperWrapper(connectString, sessionTimeout, new NodeDeleteWatcher());
    }

    public boolean AmiLeader(){
        return this.amiLeader;
    }

    private void createRootNode(){
        final String node = ROOT_NODE;
        final boolean watch = false;
        final CreateMode createdMode = CreateMode.PERSISTENT;
        this.zooKeeperWrapper.createNode(node, watch, createdMode);
    }

    private void createStateNode(){
        final String node = STATE_NODE;
        final boolean watch = false;
        final CreateMode createdMode = CreateMode.PERSISTENT;
        this.zooKeeperWrapper.createNode(node, watch, createdMode);
    }

    private void createServerNode(){
        final String node = LEADER_ELECTION_NODE;
        final boolean watch = true;
        final CreateMode createdMode = CreateMode.EPHEMERAL_SEQUENTIAL;
        this.createdServerNode = this.zooKeeperWrapper.createNode(node, watch, createdMode).replace(STATE_NODE + "/", "");
    }

    private void startLeaderElection(){

        // get all LEADER_ELECTION_NODE child nodes
        final List<String> childNodePaths = this.zooKeeperWrapper.getChildren(STATE_NODE);
        System.out.println(childNodePaths);

        // sort by name
        Collections.sort(childNodePaths);

        // find out what is th index of my server in the nodes.
        int index = childNodePaths.indexOf(createdServerNode);

        // update createdLeaderNode
        createdLeaderNodePath = STATE_NODE + "/" + childNodePaths.get(0);

        // am i a leader?
        this.amiLeader = index == 0;

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
        createRootNode();
        createStateNode();
        createServerNode();
        startLeaderElection();
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        StateZookeeper s = new StateZookeeper("127.0.0.1:2181", 5000);
        s.run();
//        System.out.println("RESULT:" + s.amiLeader);
    }

}

