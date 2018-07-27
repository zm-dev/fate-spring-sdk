package com.zmdev.fatesdk;

import com.zmdev.fatesdk.grpc_insterceptor.AccessTokenInterceptor;
import com.zmdev.fatesdk.pb.*;
import io.grpc.ClientInterceptors;
import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

public class UserService {
    private final ManagedChannel channel;
    private final UserServiceGrpc.UserServiceBlockingStub blockingStub;
    private long timeout;

    public UserService(String host, int port, AccessTokenInterceptor accessTokenInterceptor) {
        this(host, port, 5000, accessTokenInterceptor);
    }

    public UserService(String host, int port, long timeout, AccessTokenInterceptor accessTokenInterceptor) {

        this(ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .build(), timeout, accessTokenInterceptor);
    }

    UserService(ManagedChannel channel, long timeout, AccessTokenInterceptor accessTokenInterceptor) {
        this.timeout = timeout;
        this.channel = channel;
        blockingStub = UserServiceGrpc.newBlockingStub(ClientInterceptors.intercept(channel, accessTokenInterceptor));
    }


    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(this.timeout, TimeUnit.MILLISECONDS);
    }

    public void updatePassword(long userId, String newPassword) {
        UpdatePasswordMsg updatePasswordMsg = UpdatePasswordMsg.newBuilder().setUserId(userId).setNewPassword(newPassword).build();
        blockingStub.withDeadline(Deadline.after(timeout, TimeUnit.MILLISECONDS)).updatePassword(updatePasswordMsg);
    }

    public long register(String account, CertificateType certificateType, String password) {
        if (account == null || certificateType == null || password == null) {
            return 0;
        }
        User user = User.newBuilder().setAccount(account).setCertificateType(certificateType).setPassword(password).build();
        return blockingStub.withDeadline(Deadline.after(timeout, TimeUnit.MILLISECONDS)).register(user).getId();
    }

}
