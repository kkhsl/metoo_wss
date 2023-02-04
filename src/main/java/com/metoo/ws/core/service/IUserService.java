package com.metoo.ws.core.service;

import com.metoo.ws.core.entity.User;
import com.metoo.ws.core.vo.UserVo;

public interface IUserService {

    /**
     * 根据Username 查询一个User 对象
     * @param username
     * @return
     */
    User selectByName(String username);

    User selectObjById(Long id);

}
