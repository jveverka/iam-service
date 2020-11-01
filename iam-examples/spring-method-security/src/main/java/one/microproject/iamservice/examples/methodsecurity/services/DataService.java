package one.microproject.iamservice.examples.methodsecurity.services;

import one.microproject.iamservice.examples.methodsecurity.dto.ServerData;

public interface DataService {

    ServerData getData();

    ServerData setData(ServerData serverData);

}
