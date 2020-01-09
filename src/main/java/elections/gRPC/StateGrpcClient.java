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
    private BallotGrpc.BallotFutureStub futureStub;

    public StateGrpcClient(String host, String port) {
        this(ManagedChannelBuilder.forAddress(host, Integer.parseInt(port))
                .usePlaintext()
                .build());
    }

    public StateGrpcClient(ManagedChannel channel) {
        this.channel = channel;
        blockingStub = BallotGrpc.newBlockingStub(channel);
        futureStub = BallotGrpc.newFutureStub(channel);
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

    public void commitVote(VoterData voterData) throws InterruptedException {

        // build vote request.
        StateGrpcProto.VoteRequest request = StateGrpcProto.VoteRequest.newBuilder()
                .setId(voterData.getId())
                .setName(voterData.getName())
                .setState(voterData.getState())
                .setVote(voterData.getVote())
                .build();

        // commit vote
        futureStub.commitVote(request);
    }
}
