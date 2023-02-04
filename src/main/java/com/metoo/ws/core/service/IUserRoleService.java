package com.metoo.ws.core.service;

import com.metoo.ws.core.entity.UserRole;

import java.util.List;

public interface IUserRoleService {

    int batchAddUserRole(List<UserRole> userRoles);

    boolean deleteUserByRoleId(Long id);
}
