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
            StateGrpcProto.VoteReply reply = StateGrpcProto.VoteReply.newBuilder().build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }



    private void start(int port) throws IOException {

        server = ServerBuilder.forPort(port)
                .addService(new BallotImpl())
                .build()
                .start();

        logger.info("Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    StateGrpcServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final  StateGrpcServer server = new StateGrpcServer();
        int port = 50051;  // TODO: how should i find port?
        server.start(port);
        server.blockUntilShutdown();
    }

}
