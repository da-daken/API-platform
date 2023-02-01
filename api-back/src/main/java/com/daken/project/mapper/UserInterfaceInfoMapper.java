package com.daken.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daken.common.entity.UserInterfaceInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户调用接口关系(UserInterfaceInfo)表数据库访问层
 *
 * @author makejava
 * @since 2022-11-23 11:06:25
 */
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {


    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);

    boolean addLeftNum(@Param("leftNum") Integer leftNum, @Param("userId") Long userId, @Param("interfaceInfoId") Long interfaceInfoId);
}

