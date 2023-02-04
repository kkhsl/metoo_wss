package com.metoo.ws.core.service.impl;

import com.metoo.ws.core.entity.User;
import com.metoo.ws.core.mapper.UserMapper;
import com.metoo.ws.core.service.IRoleService;
import com.metoo.ws.core.service.IUserRoleService;
import com.metoo.ws.core.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IUserRoleService userRoleService;
    @Autowired
    private IRoleService roleService;

    @Override
    public User selectByName(String username) {
        return this.userMapper.selectByName(username);
    }

    @Override
    public User selectObjById(Long id) {
        return this.userMapper.selectObjById(id);
    }


}
