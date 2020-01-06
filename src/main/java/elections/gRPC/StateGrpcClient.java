package elections.gRPC;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import elections.REST.VoterData;
import server.state.BallotGrpc;
import server.state.StateGrpcProto;

import java.util.concurrent.TimeUnit;

public class StateGrpcClient {

    private ManagedChannel channel;
    private BallotGrpc.BallotBlockingStub blockingStub;

    public StateGrpcClient(String host, String port) {
        this(ManagedChannelBuilder.forAddress(host, Integer.parseInt(port))
                .usePlaintext()
                .build());
    }

    public StateGrpcClient(ManagedChannel channel) {
        this.channel = channel;
        blockingStub = BallotGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void vote(VoterData voterData) {

        // build vote request.
        StateGrpcProto.VoteRequest request = StateGrpcProto.VoteRequest.newBuilder()
                .setId(voterData.getId())
                .setName(voterData.getName())
                .setState(voterData.getState())
                .setVote(voterData.getVote())
                .build();

        // vote
        blockingStub.vote(request);
    }


//    public static void main(String[] args) throws Exception {
//        // Access a service running on the local machine on port 50051
//        StateGrpcClient client = new StateGrpcClient("localhost", 50051);
//        try {
//            String user = "world";
//            // Use the arg as the name to greet if provided
//            if (args.length > 0) {
//                user = args[0];
//            }
//            client.greet(user);
//        } finally {
//            client.shutdown();
//        }
//    }

}
