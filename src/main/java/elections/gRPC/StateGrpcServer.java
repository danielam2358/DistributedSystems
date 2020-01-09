package elections.gRPC;

import elections.REST.VoterData;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import org.apache.zookeeper.KeeperException;
import server.state.BallotGrpc;
import server.state.StateGrpcProto;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class StateGrpcServer {

    private Server server;
    private OnGrpcVoteCallback onGrpcVoteCallback;
    private OnGrpcCommitVoteCallback onGrpcCommitVoteCallback;


    public interface OnGrpcVoteCallback {
        void callback(VoterData newVoter) throws KeeperException, InterruptedException;
    }

    public interface OnGrpcCommitVoteCallback {
        void callback(VoterData newVoter) throws KeeperException, InterruptedException;
    }


    // BallotImpl
    class BallotImpl extends BallotGrpc.BallotImplBase {

        @Override
        public void vote(StateGrpcProto.VoteRequest req, StreamObserver<StateGrpcProto.VoteReply> responseObserver) {

            // build voterData from request.
            VoterData voterData = new VoterData(req.getId(), req.getName(), req.getState(), req.getVote());

            // call onGrpcVoteCallback callback
            try {
                onGrpcVoteCallback.callback(voterData);
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }

            StateGrpcProto.VoteReply reply = StateGrpcProto.VoteReply.newBuilder().build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void commitVote(StateGrpcProto.VoteRequest req, StreamObserver<StateGrpcProto.VoteReply> responseObserver){

            // build voterData from request.
            VoterData voterData = new VoterData(req.getId(), req.getName(), req.getState(), req.getVote());

            // call onGrpcCommitVoteCallback callback
            try {
                onGrpcCommitVoteCallback.callback(voterData);
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }

            StateGrpcProto.VoteReply reply = StateGrpcProto
                    .VoteReply
                    .newBuilder()
                    .setStatus(true)
                    .build();

            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }



    public void start(String grpcPort, OnGrpcVoteCallback onGrpcVoteCallback, OnGrpcCommitVoteCallback onGrpcCommitVoteCallback) throws IOException {

        this.onGrpcVoteCallback = onGrpcVoteCallback;
        this.onGrpcCommitVoteCallback = onGrpcCommitVoteCallback;
        int port = Integer.parseInt(grpcPort);

        try {

            server = ServerBuilder.forPort(port)
                    .addService(new BallotImpl())
                    .build()
                    .start();
        }

        catch (Exception e){

        }

    }

    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

}
