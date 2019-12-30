package elections.server;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

public class ZooKeeperWrapper {

    private final ZooKeeper zooKeeper;

    public ZooKeeperWrapper(String connectString, int sessionTimeout, Watcher watcher) throws IOException {
        this.zooKeeper = new ZooKeeper(connectString, sessionTimeout, watcher);
    }


    public String createNode(final String node, final boolean watch, final CreateMode createdMode) {

        String createdNode = node;

        try {

            // find out if the node already exist.
            final Stat nodeStat =  zooKeeper.exists(node, false);

            // if the node not exist - create.
            if(nodeStat == null) {
                createdNode = zooKeeper.create(node, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, createdMode);
            }

        } catch (KeeperException | InterruptedException e) {
            throw new IllegalStateException(e);
        }

        return createdNode;
    }

    public List<String> getChildren(final String node) {

        List<String> childNodes = null;

        try {
            childNodes = zooKeeper.getChildren(node, false); // TODO: should watcher be true?
        } catch (KeeperException | InterruptedException e) {
            throw new IllegalStateException(e);
        }

        return childNodes;
    }
}

