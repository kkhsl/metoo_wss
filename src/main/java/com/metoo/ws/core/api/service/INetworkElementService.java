package com.metoo.ws.core.api.service;

import com.alibaba.fastjson.JSONObject;
import com.metoo.ws.core.config.socket.NoticeWebsocketResp;

import java.util.Map;

public interface INetworkElementService {

    NoticeWebsocketResp getNeAvailable(String params);

    NoticeWebsocketResp getSnmpSatus(String params);

    NoticeWebsocketResp interfaceEvent(String params);

    NoticeWebsocketResp getNeInterfaceDT(String params);


}
