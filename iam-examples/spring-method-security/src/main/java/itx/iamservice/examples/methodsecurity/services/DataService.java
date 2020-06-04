package itx.iamservice.examples.methodsecurity.services;

import itx.iamservice.examples.methodsecurity.dto.ServerData;

public interface DataService {

    ServerData getData();

    void setData(ServerData serverData);

}
