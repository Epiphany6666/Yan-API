package com.luoyan.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luoyan.model.dto.userinterfaceinfo.UserInterfaceInfoQueryRequest;
import com.luoyan.yanapicommon.model.entity.UserInterfaceInfo;

/**
 *
 */
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    Wrapper<UserInterfaceInfo> getQueryWrapper(UserInterfaceInfoQueryRequest userUserInterfaceInfoQueryRequest);

    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);
}
