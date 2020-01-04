package elections.gRPC;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import server.state.BallotGrpc;
import server.state.StateGrpcProto;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class StateGrpcServer {

    private static final Logger logger = Logger.getLogger(StateGrpcServer.class.getName());
    private Server server;


    // BallotImpl
    static class BallotImpl extends BallotGrpc.BallotImplBase {

        @Override
        public void vote(StateGrpcProto.VoteRequest req, StreamObserver<StateGrpcProto.VoteReply> responseObserver) {
//            req.ge
            StateGrpcProto.VoteReply reply = StateGrpcProto.VoteReply.newBuilder().build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }



    public void start(String grpcPort) throws IOException {

        int port = Integer.parseInt(grpcPort);

        server = ServerBuilder.forPort(port)
                .addService(new BallotImpl())
                .build()
                .start();

        logger.info("Server started, listening on " + port);

    }

    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

}
