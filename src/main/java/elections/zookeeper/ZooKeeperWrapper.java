package elections.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.util.List;

public class ZooKeeperWrapper {

    private final ZooKeeper zooKeeper;

    public ZooKeeperWrapper(String connectString, int sessionTimeout, Watcher watcher) throws IOException {
        this.zooKeeper = new ZooKeeper(connectString, sessionTimeout, watcher);
    }

    public void stop() throws InterruptedException {
        this.zooKeeper.close();
    }


    public String createNode(final String node, final boolean watch, final CreateMode createdMode) {

        String createdNode = node;

        try {

            // find out if the node already exist.
            final Stat nodeStat =  zooKeeper.exists(node, watch);

            // if the node not exist - create.
            if(nodeStat == null) {
                createdNode = zooKeeper.create(node, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, createdMode);
            }

        } catch (KeeperException | InterruptedException e) {
            throw new IllegalStateException(e);
        }

        return createdNode;
    }

    public void setAddress(String node, String address) throws KeeperException, InterruptedException {
        byte[] data = SerializationUtils.serialize(address);
        this.zooKeeper.setData(node, data, -1);
    }

    public String getAddress(String node) throws KeeperException, InterruptedException {
        byte[] data = this.zooKeeper.getData(node, false, null);
        return (String) SerializationUtils.deserialize(data);
    }


    public List<String> getChildren(final String node) {

        List<String> childNodes = null;

        try {
            childNodes = zooKeeper.getChildren(node, false);
        } catch (KeeperException | InterruptedException e) {
            throw new IllegalStateException(e);
        }

        return childNodes;
    }

    public void watchNode(final String node) {
        try {
            this.zooKeeper.exists(node, true);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void deleteNode(final String node) throws KeeperException, InterruptedException {
        zooKeeper.delete(node, -1);
    }

    public boolean isNodeExist(final String node) throws KeeperException, InterruptedException {
        return zooKeeper.exists(node, false) != null;
    }


}

