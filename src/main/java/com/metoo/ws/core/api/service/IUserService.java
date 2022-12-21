package com.metoo.ws.core.api.service;

import com.metoo.ws.core.config.socket.NoticeWebsocketResp;

public interface IUserService {

    NoticeWebsocketResp selectObjById(Long id);
}
