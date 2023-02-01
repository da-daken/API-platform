package com.daken.common.service;

public interface InnerUserInterfaceInfoService {
    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);

    /**
     *  查询用户是否还有调用次数
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean isLeftnum(long interfaceInfoId, long userId);

}
