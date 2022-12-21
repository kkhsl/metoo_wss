package com.metoo.ws.core.manager;

import com.metoo.ws.core.api.service.INetworkElementService;
import com.metoo.ws.core.api.service.IUserService;
import com.metoo.ws.core.config.socket.NoticeWebsocketResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/test")
@RestController
public class TestController {

    @Autowired
    private INetworkElementService networkElementService;
    @Autowired
    private IUserService userService;

//    @GetMapping
//    public Object get(){
//        String params = "{\"noticeType\":\"1\", \"userId\":\"1\", \"params\":{\"currentPage\":1,\"pageSize\":2}}";
//        NoticeWebsocketResp resp = this.networkElementService.getNeAvailable("/nspm/ne/testApi", params);
//        return resp;
//    }

    @GetMapping("/getUser")
    public Object getUser(){
        NoticeWebsocketResp resp = this.userService.selectObjById(1L);
        return resp;
    }


}
