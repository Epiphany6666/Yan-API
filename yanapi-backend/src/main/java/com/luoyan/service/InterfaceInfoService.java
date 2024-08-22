package com.luoyan.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luoyan.model.dto.interfaceInfo.InterfaceInfoQueryRequest;
import com.luoyan.model.entity.InterfaceInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luoyan.model.entity.Post;
import com.luoyan.model.vo.InterfaceInfoVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author luoyan
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2024-08-20 22:09:57
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);

    Wrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceInfoQueryRequest);

    Page<InterfaceInfoVO> getInterfaceInfoVOPage(Page<InterfaceInfo> interfaceInfoPage, HttpServletRequest request);

    Page<InterfaceInfo> searchFromEs(InterfaceInfoQueryRequest interfaceInfoQueryRequest);
}
