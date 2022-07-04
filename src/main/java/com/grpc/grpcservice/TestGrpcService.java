package com.grpc.grpcservice;

import com.grpc.demo.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Arrays;
import java.util.List;

@GrpcService
public class TestGrpcService extends HelloServiceGrpc.HelloServiceImplBase{

    @Override
    public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        String greeting = new StringBuilder().append("Hello, ")
                .append(request.getFirstName())
                .append(" ")
                .append(request.getLastName())
                .toString();

        HelloResponse response = HelloResponse.newBuilder()
                .setGreeting(greeting)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<SumRequest> helloClientStreaming(StreamObserver<SumResponse> responseObserver) {
        return new StreamObserver<SumRequest>() {
            int sum=0;

            @Override
            public void onNext(SumRequest sumRequest) {
                sum += sumRequest.getNumber();
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(SumResponse.newBuilder().setSum(sum).build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void helloServerStreaming(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        List<String> messages = Arrays.asList("how are you? ", "hope you're doing well", "can you do me a favor?");
        StringBuilder greeting = new StringBuilder().append("Hello, ")
                .append(request.getFirstName())
                .append(" ")
                .append(request.getLastName());

        HelloResponse response = HelloResponse.newBuilder()
                .setGreeting(greeting.toString())
                .build();
        responseObserver.onNext(response);

        for(String message: messages){
            HelloResponse conversation = HelloResponse.newBuilder()
                    .setGreeting(message)
                    .build();
            responseObserver.onNext(conversation);
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<DuplexRequest> helloDuplex(StreamObserver<DuplexResponse> responseObserver) {

        return new StreamObserver<DuplexRequest>() {

            @Override
            public void onNext(DuplexRequest duplexRequest) {
                if(duplexRequest.getItemType().equalsIgnoreCase("Fruit")){
                    DuplexResponse response = DuplexResponse.newBuilder()
                            .setItemName("orange")
                            .setItemPrice(100)
                            .setIsAvailable(true)
                            .build();
                    responseObserver.onNext(response);
                    response = DuplexResponse.newBuilder()
                            .setItemName("apple")
                            .setItemPrice(150)
                            .setIsAvailable(true)
                            .build();
                    responseObserver.onNext(response);
                    response = DuplexResponse.newBuilder()
                            .setItemName("Kiwi")
                            .setItemPrice(250)
                            .setIsAvailable(false)
                            .build();
                    responseObserver.onNext(response);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
