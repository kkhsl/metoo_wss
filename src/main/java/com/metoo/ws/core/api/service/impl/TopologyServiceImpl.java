package com.metoo.ws.core.api.service.impl;

import com.metoo.ws.core.api.service.ITopologyService;
import com.metoo.ws.core.config.http.RestTemplateUtil;
import com.metoo.ws.core.config.socket.NoticeWebsocketResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopologyServiceImpl implements ITopologyService {


    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Override
    public NoticeWebsocketResp getMacDT(String params) {
        String url = "/websocket/api/zabbix/mac/dt";
        NoticeWebsocketResp result = restTemplateUtil.getObjByStr(url, params);
        return result;
    }
}
