package com.metoo.ws.core.api.service;

import com.metoo.ws.core.config.socket.NoticeWebsocketResp;

public interface WUserService {

    NoticeWebsocketResp selectObjById(Long id);
}
