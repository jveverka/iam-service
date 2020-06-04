package itx.iamservice.examples.resourceserver.services;

import itx.iamservice.examples.resourceserver.dto.ServerData;

public interface DataService {

    ServerData getData();

    void setData(ServerData serverData);

}
