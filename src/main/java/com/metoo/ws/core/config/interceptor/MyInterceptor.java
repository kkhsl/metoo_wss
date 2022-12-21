package com.metoo.ws.core.config.interceptor;

import com.metoo.ws.core.api.service.IUserService;
import com.metoo.ws.core.config.socket.NoticeWebsocketResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MyInterceptor implements HandlerInterceptor {


    @Autowired
    private static IUserService userService;

    /**
     * handler 对应@RequestMapping对应的controller对象
     */
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        //这里我们是没办法拿到方法参数的，parameters是空的，但是可以拿到controller中注入的bean
//
////        HandlerMethod handlerMethod = (HandlerMethod)handler;
////        MethodParameter[] parameters = handlerMethod.getMethodParameters();
//        String id = request.getParameter("userId");
//        NoticeWebsocketResp resp = this.userService.selectObjById(1L);
//        if(resp.getNoticeStatus() == 1){
//            return true;//放行
//        }
//        return false;
//    }
}
