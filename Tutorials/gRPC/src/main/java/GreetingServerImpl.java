import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import protos.GreeterGrpc;
import protos.Hello;
import java.io.IOException;
public class GreetingServerImpl extends GreeterGrpc.GreeterImplBase {
    int id;
    private Server greetingServer;
    public GreetingServerImpl(int id, int port) {
        this.id = id;
        try {
            greetingServer = ServerBuilder.forPort(port)
                    .addService(this)
                    .intercept(new Interceptor())
                    .build()
                    .start();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    void shutdown() {
        greetingServer.shutdown();
    }

    @Override
    public void sayHello(Hello.HelloRequest request, StreamObserver<Hello.HelloReply> responseObserver) {
        System.out.println(request.getFrom() + " says " + request.getMsg());
        Hello.HelloReply rep = Hello.HelloReply
                .newBuilder()
                .setServerID(id)
                .build();
        responseObserver.onNext(rep);
    }

    @Override
    public void sayHelloAgain(Hello.HelloRequest request, StreamObserver<Hello.HelloReply> responseObserver) {
        System.out.println(request.getFrom() + " says again" + request.getMsg());
        Hello.HelloReply rep = Hello.HelloReply
                .newBuilder()
                .setServerID(id)
                .build();
        responseObserver.onNext(rep);
    }
}
