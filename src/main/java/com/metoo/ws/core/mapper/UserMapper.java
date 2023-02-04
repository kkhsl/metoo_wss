package com.metoo.ws.core.mapper;

import com.metoo.ws.core.entity.User;
import com.metoo.ws.core.vo.UserVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    /**
     * 根据Username 查询一个User 对象
     * @param username
     * @return
     */
    User selectByName(String username);

    User selectObjById(Long id);

}
