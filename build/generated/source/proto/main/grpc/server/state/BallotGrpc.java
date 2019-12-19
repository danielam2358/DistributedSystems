package server.state;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 * <pre>
 * The ballot service definition
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.26.0)",
    comments = "Source: StateGrpcProto.proto")
public final class BallotGrpc {

  private BallotGrpc() {}

  public static final String SERVICE_NAME = "server.state.Ballot";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<server.state.StateGrpcProto.VoteRequest,
      server.state.StateGrpcProto.VoteReply> getVoteMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Vote",
      requestType = server.state.StateGrpcProto.VoteRequest.class,
      responseType = server.state.StateGrpcProto.VoteReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<server.state.StateGrpcProto.VoteRequest,
      server.state.StateGrpcProto.VoteReply> getVoteMethod() {
    io.grpc.MethodDescriptor<server.state.StateGrpcProto.VoteRequest, server.state.StateGrpcProto.VoteReply> getVoteMethod;
    if ((getVoteMethod = BallotGrpc.getVoteMethod) == null) {
      synchronized (BallotGrpc.class) {
        if ((getVoteMethod = BallotGrpc.getVoteMethod) == null) {
          BallotGrpc.getVoteMethod = getVoteMethod =
              io.grpc.MethodDescriptor.<server.state.StateGrpcProto.VoteRequest, server.state.StateGrpcProto.VoteReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Vote"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  server.state.StateGrpcProto.VoteRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  server.state.StateGrpcProto.VoteReply.getDefaultInstance()))
              .setSchemaDescriptor(new BallotMethodDescriptorSupplier("Vote"))
              .build();
        }
      }
    }
    return getVoteMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static BallotStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BallotStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BallotStub>() {
        @java.lang.Override
        public BallotStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BallotStub(channel, callOptions);
        }
      };
    return BallotStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static BallotBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BallotBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BallotBlockingStub>() {
        @java.lang.Override
        public BallotBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BallotBlockingStub(channel, callOptions);
        }
      };
    return BallotBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static BallotFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BallotFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BallotFutureStub>() {
        @java.lang.Override
        public BallotFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BallotFutureStub(channel, callOptions);
        }
      };
    return BallotFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * The ballot service definition
   * </pre>
   */
  public static abstract class BallotImplBase implements io.grpc.BindableService {

    /**
     */
    public void vote(server.state.StateGrpcProto.VoteRequest request,
        io.grpc.stub.StreamObserver<server.state.StateGrpcProto.VoteReply> responseObserver) {
      asyncUnimplementedUnaryCall(getVoteMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getVoteMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                server.state.StateGrpcProto.VoteRequest,
                server.state.StateGrpcProto.VoteReply>(
                  this, METHODID_VOTE)))
          .build();
    }
  }

  /**
   * <pre>
   * The ballot service definition
   * </pre>
   */
  public static final class BallotStub extends io.grpc.stub.AbstractAsyncStub<BallotStub> {
    private BallotStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BallotStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BallotStub(channel, callOptions);
    }

    /**
     */
    public void vote(server.state.StateGrpcProto.VoteRequest request,
        io.grpc.stub.StreamObserver<server.state.StateGrpcProto.VoteReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getVoteMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * The ballot service definition
   * </pre>
   */
  public static final class BallotBlockingStub extends io.grpc.stub.AbstractBlockingStub<BallotBlockingStub> {
    private BallotBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BallotBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BallotBlockingStub(channel, callOptions);
    }

    /**
     */
    public server.state.StateGrpcProto.VoteReply vote(server.state.StateGrpcProto.VoteRequest request) {
      return blockingUnaryCall(
          getChannel(), getVoteMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * The ballot service definition
   * </pre>
   */
  public static final class BallotFutureStub extends io.grpc.stub.AbstractFutureStub<BallotFutureStub> {
    private BallotFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BallotFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BallotFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<server.state.StateGrpcProto.VoteReply> vote(
        server.state.StateGrpcProto.VoteRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getVoteMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_VOTE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final BallotImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(BallotImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_VOTE:
          serviceImpl.vote((server.state.StateGrpcProto.VoteRequest) request,
              (io.grpc.stub.StreamObserver<server.state.StateGrpcProto.VoteReply>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class BallotBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    BallotBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return server.state.StateGrpcProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Ballot");
    }
  }

  private static final class BallotFileDescriptorSupplier
      extends BallotBaseDescriptorSupplier {
    BallotFileDescriptorSupplier() {}
  }

  private static final class BallotMethodDescriptorSupplier
      extends BallotBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    BallotMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (BallotGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new BallotFileDescriptorSupplier())
              .addMethod(getVoteMethod())
              .build();
        }
      }
    }
    return result;
  }
}
