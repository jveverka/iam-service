package itx.iamservice.examples.methodsecurity.services;

import itx.iamservice.examples.methodsecurity.dto.ServerData;
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
    public ServerData setData(ServerData serverData) {
        this.serverData = serverData;
        return this.serverData;
    }

}
