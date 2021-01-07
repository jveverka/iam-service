package itx.examples.webflux.config;

import one.microproject.iamservice.client.IAMClient;
import one.microproject.iamservice.core.model.JWToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static one.microproject.iamservice.client.JWTUtils.AUTHORIZATION;

public class IAMHandlerFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private static final Logger LOG = LoggerFactory.getLogger(IAMHandlerFilterFunction.class);

    private final IAMClient iamClient;

    public IAMHandlerFilterFunction(IAMClient iamClient) {
        this.iamClient = iamClient;
    }

    @Override
    public Mono<ServerResponse> filter(ServerRequest serverRequest, HandlerFunction<ServerResponse> handlerFunction) {
        Optional<String> authorization = serverRequest.headers().header(AUTHORIZATION).stream().findFirst();
        if (authorization.isPresent()) {
            String[] tokens = authorization.get().split(" ");
            if (iamClient.validate(new JWToken(tokens[1])).isPresent()) {
                LOG.info("filter: OK authorization={}", authorization.get());
                return handlerFunction.handle(serverRequest);
            }
        }
        LOG.info("filter: UNAUTHORIZED");
        return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
    }

}
