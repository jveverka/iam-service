package one.microproject.iamservice.examples.resourceserver.services;

import one.microproject.iamservice.examples.resourceserver.dto.ServerData;

public interface DataService {

    ServerData getData();

    void setData(ServerData serverData);

}
