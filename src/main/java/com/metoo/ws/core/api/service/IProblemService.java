package com.metoo.ws.core.api.service;

import com.metoo.ws.core.config.socket.NoticeWebsocketResp;

public interface IProblemService {

    NoticeWebsocketResp getProblem(String params);
}
