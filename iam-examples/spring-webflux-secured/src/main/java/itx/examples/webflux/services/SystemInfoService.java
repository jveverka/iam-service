package itx.examples.webflux.services;

import itx.examples.webflux.dto.SystemInfo;
import reactor.core.publisher.Mono;

public interface SystemInfoService {

    Mono<SystemInfo> getSystemInfo();

}
