package one.microproject.iamservice.examples.resourceserver.services;

import one.microproject.iamservice.examples.resourceserver.dto.ServerData;
import org.springframework.stereotype.Service;

@Service
public class DataServiceImpl implements DataService {

    private ServerData serverData;

    public DataServiceImpl() {
        this.serverData = new ServerData("default");
    }

    @Override
    public ServerData getData() {
        return this.serverData;
    }

    @Override
    public void setData(ServerData serverData) {
        this.serverData = serverData;
    }

}
