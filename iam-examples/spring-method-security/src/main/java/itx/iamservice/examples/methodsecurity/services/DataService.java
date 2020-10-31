package itx.iamservice.examples.methodsecurity.services;

import itx.iamservice.examples.methodsecurity.dto.ServerData;

public interface DataService {

    ServerData getData();

    ServerData setData(ServerData serverData);

}
