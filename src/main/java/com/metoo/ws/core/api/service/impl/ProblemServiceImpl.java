package com.metoo.ws.core.api.service.impl;

import com.metoo.ws.core.api.service.IProblemService;
import com.metoo.ws.core.config.http.RestTemplateUtil;
import com.metoo.ws.core.config.socket.NoticeWebsocketResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProblemServiceImpl implements IProblemService {

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Override
    public NoticeWebsocketResp getProblem(String params) {
        String url = "/websocket/api/problem";
        NoticeWebsocketResp result = restTemplateUtil.getObjByStr(url, params);
        return result;
    }

    @Override
    public NoticeWebsocketResp getProblemCpu(String params) {
        String url = "/websocket/api/problem/interface/event";
        NoticeWebsocketResp result = restTemplateUtil.getObjByStr(url, params);
        return result;
    }

    @Override
    public NoticeWebsocketResp getProblemLimit(String params) {
        String url = "/websocket/api/problem/all";
        NoticeWebsocketResp result = restTemplateUtil.getObjByStr(url, params);
        return result;
    }
}
