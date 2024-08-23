package com.luoyan.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.luoyan.model.dto.userinterfaceinfo.UserInterfaceInfoQueryRequest;
import com.luoyan.model.entity.InterfaceInfo;
import com.luoyan.model.entity.UserInterfaceInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 52837
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2024-08-23 09:28:26
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    Wrapper<UserInterfaceInfo> getQueryWrapper(UserInterfaceInfoQueryRequest userUserInterfaceInfoQueryRequest);
}
