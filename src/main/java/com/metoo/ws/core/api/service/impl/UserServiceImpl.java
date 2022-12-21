package com.metoo.ws.core.api.service.impl;

import com.metoo.ws.core.api.service.IUserService;
import com.metoo.ws.core.config.http.RestTemplateUtil;
import com.metoo.ws.core.config.socket.NoticeWebsocketResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Override
    public NoticeWebsocketResp selectObjById(Long id) {
        String url = "/websocket/api/user";
        StringBuffer sb = new StringBuffer(url);
        sb.append("?userId=" + id);
        NoticeWebsocketResp result = restTemplateUtil.get(sb.toString());
        return result;
    }
}
