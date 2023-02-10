package com.metoo.ws.core.config.socket;

/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.metoo.ws.core.api.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 * 只读属性 readyState 表示连接状态，可以是以下值：
 *
 * 0 - 表示连接尚未建立。
 *
 * 1 - 表示连接已建立，可以进行通信。
 *
 * 2 - 表示连接正在进行关闭。
 *
 * 3 - 表示连接已经关闭或者连接不能打开。
 *
 * @OnOpen 表示有浏览器链接过来的时候被调用
 * @OnClose 表示浏览器发出关闭请求的时候被调用
 * @OnMessage 表示浏览器发消息的时候被调用
 * @OnError 表示报错了
 */
@ServerEndpoint("/notice/nmap/{userId}")
@Component
@Slf4j
public class NoticeEndpoint {

    //记录连接的客户端
    public static Map<String, Session> clients = new ConcurrentHashMap<>();

    // 记录连接的客户端的参数，定时发送消息
    public static Map<String, Map<String, String>> clientsParams = new ConcurrentHashMap<>();

    // 定时任务 避免同一账号多地登录下，定时任务回显数据无法区分用户
    public static Map<String, Map<String, String>> taskParams = new ConcurrentHashMap<>();//

    /**
     * userId关联sid（解决同一用户id，在多个web端连接的问题）
     */
    public static Map<String, Set<String>> conns = new ConcurrentHashMap<>();

    private String sid = null;

    private String userId;

    /**
     * 注入SpringBean
     */

    private static INetworkElementService networkElementService;
    private static WUserService userService;
    private static IZabbixService zabbixService;
    private static ITopologyService topologyService;
    private static IProblemService problemService;
    @Autowired
    public void setNetworkElementService(INetworkElementService networkElementService) {
        NoticeEndpoint.networkElementService = networkElementService;
    }
    @Autowired
    public void setProblemService(IProblemService problemService){
        NoticeEndpoint.problemService = problemService;
    }

    @Autowired
    public void setUserService(WUserService userService){
        NoticeEndpoint.userService = userService;
    }
    @Autowired
    public void setZabbixService(IZabbixService zabbixService){
        NoticeEndpoint.zabbixService = zabbixService;
    }
    @Autowired
    public void setTopologyService(ITopologyService topologyService){
        NoticeEndpoint.topologyService = topologyService;
    }
    /**
     * 连接成功后调用的方法
     * @param session
     * @param userId
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        // 方法一：前端发送用户信息（明文）
        // 方法二：做分布式登录 SSO。部署到同一个服务器，认证授权使用同一个项目
        NoticeWebsocketResp resp = this.userService.selectObjById(Long.parseLong(userId));
        if(true){
            this.sid = UUID.randomUUID().toString();
            this.userId = userId;
            clients.put(this.sid, session);

            Map userMap = taskParams.get(this.sid);
            if(userMap == null){// 单独记录sid,避免同一账号多地登录
                userMap = new HashMap();
                userMap.put("userId", this.userId);
                taskParams.put(this.sid, userMap);
            }

            Set<String> clientSet = conns.get(userId);
            if (clientSet==null){
                clientSet = new HashSet<>();
                conns.put(userId, clientSet);
            }

            clientSet.add(this.sid);
            log.info(this.sid + "用户Id:" + userId + "连接开启！");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        log.info(this.sid + "连接断开！");

        clients.remove(this.sid);

        /**
         * 清除定时任务信息
         *
         */
        taskParams.remove(this.sid);
    }

    /**
     * 判断是否连接的方法
     * @return
     */
    public static boolean isServerClose() {
        if (NoticeEndpoint.clients.values().size() == 0) {
            log.info("已断开");
            return true;
        }else {
            log.info("已连接");
            return false;
        }
    }

    /**
     * 发送给所有用户
     * @param noticeType
     */
    public static void sendMessage(String noticeType){
        NoticeWebsocketResp noticeWebsocketResp = new NoticeWebsocketResp();
        noticeWebsocketResp.setNoticeType(noticeType);
        sendMessage(noticeWebsocketResp);
    }

    /**
     * 发送给所有用户
     * @param noticeWebsocketResp
     */
    public static void sendMessage(NoticeWebsocketResp noticeWebsocketResp){
        String message = JSONObject.toJSONString(noticeWebsocketResp);
        for (Session session1 : NoticeEndpoint.clients.values()) {
            try {
                session1.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据用户id发送给某一个用户
     * **/
    public static void sendMessageByUserId(String userId, NoticeWebsocketResp noticeWebsocketResp) {
        if (!StringUtils.isEmpty(userId)) {
            String message = JSONObject.toJSONString(noticeWebsocketResp);
            Set<String> clientSet = conns.get(userId);
            if (clientSet != null) {
                Iterator<String> iterator = clientSet.iterator();
                while (iterator.hasNext()) {
                    String sid = iterator.next();
                    Session session = clients.get(sid);
                    if (session != null) {
                        try {
                            session.getBasicRemote().sendText(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    /**
     * 收到客户端消息后调用的方法
     * @param message
     * @param session
     */
    @OnMessage(maxMessageSize = 1048576)
    public void onMessage(String message, Session session) {
        log.info("收到来自窗口 " + this.userId + " 的信息:" + message);

        // 测试聊天
//        NoticeWebsocket.sendMessage("测试推送消息");
        // "收到来自窗口 " + this.userId + " 的信息:" + message
//        NoticeWebsocketResp noticeWebsocketResp = new NoticeWebsocketResp();
//        noticeWebsocketResp.setNoticeType("1");
//        noticeWebsocketResp.setNoticeInfo("收到来自窗口 " + this.userId + " 的信息:" + message);
//        sendMessageByUserId("好友Id", getNeAvailable(message));

        // 接收消息
//        parseParams(message);
        parseParams2(message);

//        Map<String, Object> map = (Map) JSON.parse(message);
        Map map = JSONObject.parseObject(message, Map.class);
        // 校验参数，调用指定api
        if (map.get("noticeType").equals("1")) {
            taskSendMessageByUserId(this.sid, getNeAvailable(message));
        }
        if (map.get("noticeType").equals("2")) {
            taskSendMessageByUserId(this.sid, snmpStatus(map.get("params")));
        }
        if (map.get("noticeType").equals("3")) {
            taskSendMessageByUserId(this.sid, getItemLastValue(map.get("params")));
        }
        if (map.get("noticeType").equals("4")) {
            taskSendMessageByUserId(this.sid, getMacDT(map.get("params")));
        }
        if (map.get("noticeType").equals("5")) {
            taskSendMessageByUserId(this.sid, interfaceEvent(map.get("params").toString()));
        }
        if (map.get("noticeType").equals("6")) {
            taskSendMessageByUserId(this.sid, getProblem(map.get("params")));
        } if (map.get("noticeType").equals("7")) {
            taskSendMessageByUserId(this.sid, getProblemCpu(map.get("params")));
        }if (map.get("noticeType").equals("8")) {
            taskSendMessageByUserId(this.sid, getProblemLimit(map.get("params")));
        }if (map.get("noticeType").equals("9")) {
            taskSendMessageByUserId(this.sid, getNeInterfaceDT(map.get("params")));
        }
        // 返回数据给当前用户
//        taskSendMessageByUserId(this.sid, getNeAvailable(message));
    }

    // 处理数据 关联到一个账号（存在一个账号多地登陆时，无法分别发送给登录用户，只能统一发送给这个账号的所有登录同一数据）
    public void parseParams(String message){
        Map map = (Map) JSON.parse(message);
        if(map != null && !map.isEmpty()){
            Map userMap = clientsParams.get(this.userId);
            if(map.get("noticeType") != null){
                if(userMap.get(map.get("noticeType").toString()) != null){
                    userMap.remove(map.get("noticeType").toString());
                    userMap.put(map.get("noticeType"), message);
                }else{
                    userMap.put(map.get("noticeType"), message);
                }
                clientsParams.put(this.userId, userMap);
            }
        }
    }

    /**
     * 保存参数，使用定时任务发送指定数据（解决同一账号多地登陆时，分别响应）
     * @param message
     */
    public void parseParams2(String message){
        Map map = (Map) JSON.parse(message);
        if(map != null && !map.isEmpty()){
            Map userMap = taskParams.get(this.sid);
            if(map.get("noticeType") != null){
                if(userMap.get(map.get("noticeType").toString()) != null){
                    userMap.remove(map.get("noticeType").toString());
                    userMap.put(map.get("noticeType"), message);
                }else{
                    userMap.put(map.get("noticeType"), message);
                }
                taskParams.put(this.sid, userMap);
            }
        }
    }

    // 网元状态
    public NoticeWebsocketResp getNeAvailable(String params){
        NoticeWebsocketResp resp = this.networkElementService.getNeAvailable(params);
        return resp;
    }

    // snmp状态
    public NoticeWebsocketResp snmpStatus(Object params){
        NoticeWebsocketResp resp = networkElementService.getSnmpSatus(JSONObject.toJSONString(params));
        return resp;
    }

    // interface event
    public NoticeWebsocketResp interfaceEvent(String params){
        NoticeWebsocketResp resp = this.networkElementService.interfaceEvent(params);
        return resp;
    }

    public NoticeWebsocketResp getItemLastValue(Object params){
        NoticeWebsocketResp resp = this.zabbixService.getItemLastValue(JSONObject.toJSONString(params));
        return resp;
    }

    public NoticeWebsocketResp getMacDT(Object params){
        NoticeWebsocketResp resp = this.topologyService.getMacDT(JSONObject.toJSONString(params));
        return resp;
    }
    public NoticeWebsocketResp getProblem(Object params){
        NoticeWebsocketResp resp = problemService.getProblem(JSONObject.toJSONString(params));
        return resp;
    }
    public NoticeWebsocketResp getProblemCpu(Object params){
        NoticeWebsocketResp resp = problemService.getProblemCpu(JSONObject.toJSONString(params));
        return resp;
    }
    public NoticeWebsocketResp getProblemLimit(Object params){
        NoticeWebsocketResp resp = problemService.getProblemLimit(JSONObject.toJSONString(params));
        return resp;
    }
    public NoticeWebsocketResp getNeInterfaceDT(Object params){
        NoticeWebsocketResp resp = networkElementService.getNeInterfaceDT(JSONObject.toJSONString(params));
        return resp;
    }
    /**
     * 发生错误时的回调函数
     * @param error
     */
    @OnError
    public void onError(Throwable error) {
        log.info("错误");
        error.printStackTrace();
    }

    /**
     * 根据用户sid发送消息
     * @param sid
     * @param noticeWebsocketResp
     */
    public static void taskSendMessageByUserId(String sid, NoticeWebsocketResp noticeWebsocketResp) {
        if (!StringUtils.isEmpty(sid)) {
            String message = JSONObject.toJSONString(noticeWebsocketResp);
            Map userMap = taskParams.get(sid);
            if (userMap != null) {
                Session session = clients.get(sid);
                if (session != null) {
                    try {
                        session.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // 创建定时任务，发送消息
//    @Async
    @Scheduled(cron = "*/10 * * * * ?")
    public void ExecutionTimer(){
        outCycle:for (String key : taskParams.keySet()){// 校验用户是否已断开，或断开时删除该用户定时任务信息
            Map<String, String> params = taskParams.get(key);
//            String userId = null;
//            for(String key2 : params.keySet()){
//                if(key2.equals("userId")){
//                    userId = params.get(key2);break;
//                }
//            }
            for(String type : params.keySet()){
                if(type.equals("1")){
                    Map param = JSONObject.parseObject(params.get(type), Map.class);
                    taskSendMessageByUserId(key, getNeAvailable(JSON.toJSONString(param)));
                }if(type.equals("2")){
                    Map param = JSONObject.parseObject(params.get(type), Map.class);
                    taskSendMessageByUserId(key, snmpStatus(param.get("params")));
                }else if(type.equals("5")){
                    Map param = JSONObject.parseObject(params.get(type), Map.class);
                    taskSendMessageByUserId(key, interfaceEvent(param.get("params").toString()));
                }else if(type.equals("7")){
                    Map param = JSONObject.parseObject(params.get(type), Map.class);
                    taskSendMessageByUserId(key, getProblemCpu(param.get("params")));
                }else if(type.equals("8")){
                    Map param = JSONObject.parseObject(params.get(type), Map.class);
                    taskSendMessageByUserId(key, getProblemLimit(param.get("params")));
                }else if(type.equals("9")){
                    Map param = JSONObject.parseObject(params.get(type), Map.class);
                    taskSendMessageByUserId(key, getNeInterfaceDT(param.get("params")));
                }else{
                    continue ;// outCycle
                }
            }
        }
    }

    @Scheduled(cron = "*/10 * * * * ?")
    public void topologyProblem() throws InterruptedException {
        outCycle:for (String key : taskParams.keySet()){// 校验用户是否已断开，或断开时删除该用户定时任务信息
            Map<String, String> params = taskParams.get(key);
            for(String type : params.keySet()){
                if(type.equals("3")){
                    Thread.sleep(10000);
                    Map param = JSONObject.parseObject(params.get(type), Map.class);
                    taskSendMessageByUserId(key, getItemLastValue(param.get("params")));
                }else{
                    continue ;// outCycle
                }
            }
        }
    }

    @Scheduled(cron = "*/10 * * * * ?")
    public void problem(){
        outCycle:for (String key : taskParams.keySet()){// 校验用户是否已断开，或断开时删除该用户定时任务信息
            Map<String, String> params = taskParams.get(key);
            for(String type : params.keySet()){
                if(type.equals("6")){
                    Map param = JSONObject.parseObject(params.get(type), Map.class);
                    taskSendMessageByUserId(key, getProblem(param.get("params")));
                    break ;
                }else {
                    continue ;// outCycle
                }
            }
        }
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void ExecutionTimer2(){
        outCycle:for (String key : taskParams.keySet()){// 校验用户是否已断开，或断开时删除该用户定时任务信息
            Map<String, String> params = taskParams.get(key);
            for(String type : params.keySet()){
                if(type.equals("4")){
                    Map param = JSONObject.parseObject(params.get(type), Map.class);
                    taskSendMessageByUserId(key, getMacDT(param.get("params")));
                }else {
                    continue ;// outCycle
                }
            }
        }
    }

}
