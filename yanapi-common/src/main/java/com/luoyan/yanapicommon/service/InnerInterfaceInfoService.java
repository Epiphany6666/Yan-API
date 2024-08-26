package com.luoyan.yanapicommon.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.luoyan.yanapicommon.model.entity.InterfaceInfo;

/**
* @author luoyan
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2024-08-20 22:09:57
*/
public interface InnerInterfaceInfoService {
    /**
     * 从数据库中查询模拟接口是否存在（请求路径、请求方法、请求参数）
     */
    InterfaceInfo getInterfaceInfo(String path, String method);
}
