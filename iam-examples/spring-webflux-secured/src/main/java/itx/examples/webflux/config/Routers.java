package itx.examples.webflux.config;

import itx.examples.webflux.dto.CreateUserData;
import itx.examples.webflux.dto.SystemInfo;
import itx.examples.webflux.dto.UserData;
import itx.examples.webflux.services.SystemInfoService;
import itx.examples.webflux.services.UserService;
import one.microproject.iamservice.client.IAMClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;


@Configuration
public class Routers {

    @Bean
    public RouterFunction<ServerResponse> route(UserService userService, SystemInfoService systemInfoService,
                                                IAMClient iamClient) {
        return RouterFunctions
                //Secured endpoints
                .route(GET("/secured/users/{id}").and(accept(APPLICATION_JSON)), request -> {
                    Mono<UserData> userDataMono = userService.getEmployee(request.pathVariable("id"));
                    return ServerResponse.ok().body(userDataMono, UserData.class);
                })
                .andRoute(GET("/secured/users").and(accept(APPLICATION_JSON)), request -> {
                    Flux<UserData> userDataFlux = userService.getAll();
                    return ServerResponse.ok().body(userDataFlux, UserData.class);
                })
                .andRoute(POST("/secured/users").and(accept(APPLICATION_JSON)), request -> {
                    Mono<CreateUserData> monoBody = request.bodyToMono(CreateUserData.class);
                    Mono<UserData> userDataMono = monoBody.flatMap(userService::create);
                    return ServerResponse.ok().body(userDataMono, UserData.class);
                })
                .andRoute(DELETE("/secured/users/{id}").and(accept(APPLICATION_JSON)), request -> {
                    Mono<UserData> userDataMono = userService.delete(request.pathVariable("id"));
                    return ServerResponse.ok().body(userDataMono, UserData.class);
                })
                .andRoute(PUT("/secured/users").and(accept(APPLICATION_JSON)), request -> {
                    Mono<UserData> monoBody = request.bodyToMono(UserData.class);
                    Mono<UserData> userDataMono = monoBody.flatMap(userService::update);
                    return ServerResponse.ok().body(userDataMono, UserData.class);
                })
                .andRoute(PUT("/secured/create/{n}").and(accept(APPLICATION_JSON)), request -> {
                    userService.createUsersBulk(Integer.parseInt(request.pathVariable("n")));
                    return ServerResponse.ok().build();
                })
                .filter(new IAMHandlerFilterFunction(iamClient))
                //Un-Secured endpoints
                .andRoute(GET("/services/public/info").and(accept(APPLICATION_JSON)), request -> {
                    Mono<SystemInfo> systemInfoMono = systemInfoService.getSystemInfo();
                    return ServerResponse.ok().body(systemInfoMono, SystemInfo.class);
                });
    }

}
