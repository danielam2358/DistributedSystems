package elections.server.state;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.logging.Logger;

public class StateGrpcServer {
    private static final Logger logger = Logger.getLogger(StateGrpcServer.class.getName());
    private Server server;

}
