package com.metoo.ws.core.mapper;

import com.metoo.ws.core.entity.RoleRes;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleResMapper {

    int insert(List<RoleRes> roleResList);

    int delete(Long id);
}
