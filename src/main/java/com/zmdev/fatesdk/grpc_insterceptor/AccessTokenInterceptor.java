package com.zmdev.fatesdk.grpc_insterceptor;


import com.zmdev.fatesdk.AccessToken;
import io.grpc.*;

public class AccessTokenInterceptor implements ClientInterceptor {
    private AccessToken accessToken;
    private Metadata.Key<String> token;

    public AccessTokenInterceptor(String accessTokenKey, AccessToken accessToken) {
        this.accessToken = accessToken;
        token = Metadata.Key.of(accessTokenKey, Metadata.ASCII_STRING_MARSHALLER);
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                headers.put(token, accessToken.getToken());
                super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                    @Override
                    public void onHeaders(Metadata headers) {
                        super.onHeaders(headers);
                    }
                }, headers);
            }
        };
    }
}
