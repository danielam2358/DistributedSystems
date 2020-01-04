package elections.gRPC;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import server.state.BallotGrpc;
import server.state.StateGrpcProto;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StateGrpcClient {
    private static final Logger logger = Logger.getLogger(StateGrpcClient.class.getName());

    private ManagedChannel channel;
    private BallotGrpc.BallotBlockingStub blockingStub;

    /** Construct client connecting to HelloWorld server at {@code host:port}. */
    public StateGrpcClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .build());
    }

    /** Construct client for accessing HelloWorld server using the existing channel. */
    public StateGrpcClient(ManagedChannel channel) {
        this.channel = channel;
        blockingStub = BallotGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /** Say hello to server. */
    public void greet(String name) {
        logger.info("Will try to greet " + name + " ...");
        StateGrpcProto.VoteRequest request = StateGrpcProto.VoteRequest.newBuilder().setName(name).build();
        StateGrpcProto.VoteReply response;
        try {
            response = blockingStub.vote(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Greeting: " + response.getStatus());
    }

    // TODO: finish

    /**
     * Greet server. If provided, the first element of {@code args} is the name to use in the
     * greeting.
     */
    public static void main(String[] args) throws Exception {
        // Access a service running on the local machine on port 50051
        StateGrpcClient client = new StateGrpcClient("localhost", 50051);
        try {
            String user = "world";
            // Use the arg as the name to greet if provided
            if (args.length > 0) {
                user = args[0];
            }
            client.greet(user);
        } finally {
            client.shutdown();
        }
    }

}
