package com.zmdev.fatesdk;

import com.zmdev.fatesdk.grpc_insterceptor.AccessTokenInterceptor;
import com.zmdev.fatesdk.pb.LoginCheckResOrBuilder;
import com.zmdev.fatesdk.pb.LoginCheckerGrpc;
import com.zmdev.fatesdk.pb.TicketID;
import io.grpc.*;

import java.util.concurrent.TimeUnit;

public class LoginChecker {
    private final ManagedChannel channel;
    private final LoginCheckerGrpc.LoginCheckerBlockingStub blockingStub;
    private long timeout;

    public LoginChecker(String host, int port, AccessTokenInterceptor accessTokenInterceptor) {
        this(host, port, 5000, accessTokenInterceptor);
    }

    public LoginChecker(String host, int port, long timeout, AccessTokenInterceptor accessTokenInterceptor) {
        this(ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .build(), timeout, accessTokenInterceptor);
    }

    LoginChecker(ManagedChannel channel, long timeout, AccessTokenInterceptor accessTokenInterceptor) {
        this.timeout = timeout;
        this.channel = channel;
        blockingStub = LoginCheckerGrpc.newBlockingStub(ClientInterceptors.intercept(channel, accessTokenInterceptor));
    }


    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(this.timeout, TimeUnit.MILLISECONDS);
    }

    public LoginCheckResOrBuilder Check(String ticketIdStr) {
        TicketID ticketId = TicketID.newBuilder().setId(ticketIdStr).build();
        return blockingStub.withDeadline(Deadline.after(timeout, TimeUnit.MILLISECONDS)).check(ticketId);
    }
}
