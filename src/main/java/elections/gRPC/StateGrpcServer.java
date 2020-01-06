package elections.gRPC;

import elections.REST.VoterData;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import server.state.BallotGrpc;
import server.state.StateGrpcProto;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class StateGrpcServer {

    private Server server;
    private OnGrpcVoteCallback onGrpcVoteCallback;


    public interface OnGrpcVoteCallback {
        void callback(VoterData newVoter);
    }


    // BallotImpl
    class BallotImpl extends BallotGrpc.BallotImplBase {

        @Override
        public void vote(StateGrpcProto.VoteRequest req, StreamObserver<StateGrpcProto.VoteReply> responseObserver) {

            VoterData voterData = new VoterData(req.getId(), req.getName(), req.getState(), req.getVote());
            onGrpcVoteCallback.callback(voterData);

            StateGrpcProto.VoteReply reply = StateGrpcProto.VoteReply.newBuilder().build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }



    public void start(String grpcPort, OnGrpcVoteCallback onGrpcVoteCallback) throws IOException {

        this.onGrpcVoteCallback = onGrpcVoteCallback;
        int port = Integer.parseInt(grpcPort);

        server = ServerBuilder.forPort(port)
                .addService(new BallotImpl())
                .build()
                .start();

    }

    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

}
