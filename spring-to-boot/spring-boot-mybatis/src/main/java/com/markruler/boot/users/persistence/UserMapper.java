package com.markruler.boot.users.persistence;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    String getUser();

}
