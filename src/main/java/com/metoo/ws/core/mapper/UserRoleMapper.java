package com.metoo.ws.core.mapper;

import com.metoo.ws.core.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserRoleMapper {

    /**
     * 批量添加角色
     * @param userRoles
     * @return
     */
    public int batchAddUserRole(List<UserRole> userRoles);

    /**
     * 批量删除用户角色
     * @param id
     * @return
     */
    int deleteUserByRoleId(Long id);

}
