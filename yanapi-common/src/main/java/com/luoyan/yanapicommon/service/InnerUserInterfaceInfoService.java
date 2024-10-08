package com.luoyan.yanapicommon.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.luoyan.yanapicommon.model.entity.UserInterfaceInfo;

/**
* @author 52837
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2024-08-23 09:28:26
*/
public interface InnerUserInterfaceInfoService {

    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);
}
