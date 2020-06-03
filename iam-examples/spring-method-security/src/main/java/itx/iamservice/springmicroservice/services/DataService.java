package itx.iamservice.springmicroservice.services;

import itx.iamservice.springmicroservice.dto.ServerData;

public interface DataService {

    ServerData getData();

    void setData(ServerData serverData);

}
