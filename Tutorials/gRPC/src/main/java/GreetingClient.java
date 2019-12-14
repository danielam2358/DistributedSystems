import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import protos.GreeterGrpc;
import protos.Hello;

public class GreetingClient {
    private GreeterGrpc.GreeterBlockingStub stub;
    private ManagedChannel channel;

    public GreetingClient(String host, int port) {
        channel = ManagedChannelBuilder
                .forAddress(host, port)
                .intercept(new Decorator(0))
                .usePlaintext()
                .build();
        stub = GreeterGrpc.newBlockingStub(channel);
    }

    public void shutdown() {
        channel.shutdown();
    }

    public void sayHello(String from, String msg) {
        Hello.HelloRequest req = Hello.HelloRequest.newBuilder()
                .setFrom(from)
                .setMsg(msg)
                .build();
        Hello.HelloReply rep = stub.sayHello(req);
        System.out.println(String.format("got replay from %d", rep.getServerID()));
    }

    public void sayHelloAgain(String from, String msg) {
        Hello.HelloRequest req = Hello.HelloRequest.newBuilder()
                .setFrom(from)
                .setMsg(msg)
                .build();
        Hello.HelloReply rep = stub.sayHelloAgain(req);
        System.out.println(String.format("got replay from %d", rep.getServerID()));
    }
}
