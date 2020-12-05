package itx.examples.webflux.services;

import itx.examples.webflux.dto.SystemInfo;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class SystemInfoServiceImpl implements SystemInfoService {

    @Override
    public Mono<SystemInfo> getSystemInfo() {
        return Mono.just(new SystemInfo("webflux-demo", "1.0.0"));
    }

}
