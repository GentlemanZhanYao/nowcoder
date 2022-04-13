package com.nowcoder.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class AlphaTestImpl implements AlphaTest{
    @Override
    public void select() {
        System.out.println("测试");
    }
}
