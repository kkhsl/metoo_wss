package com.metoo.ws.core.service.impl;

import com.metoo.ws.core.entity.RoleRes;
import com.metoo.ws.core.mapper.RoleResMapper;
import com.metoo.ws.core.service.IRoleResService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RoleResServiceImpl implements IRoleResService {

    @Autowired
    private RoleResMapper roleResMapper;

    @Override
    public boolean batchAddRoleRes(List<RoleRes> roleRes) {
        try {
            this.roleResMapper.insert(roleRes);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteRoleResByRoleId(Long role_id) {
        try {
            this.roleResMapper.delete(role_id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
