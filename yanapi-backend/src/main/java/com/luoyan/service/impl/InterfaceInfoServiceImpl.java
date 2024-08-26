package com.luoyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luoyan.common.ErrorCode;
import com.luoyan.exception.BusinessException;
import com.luoyan.mapper.InterfaceInfoMapper;
import com.luoyan.model.dto.interfaceInfo.InterfaceInfoQueryRequest;
import com.luoyan.model.vo.InterfaceInfoVO;
import com.luoyan.service.InterfaceInfoService;
import com.luoyan.yanapicommon.model.entity.InterfaceInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
* @author luoyan
* @description 针对表【interface_info(接口信息)】的数据库操作Service实现
* @createDate 2024-08-20 22:09:57
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService{

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        // 判断接口信息对象是否为空,为空则抛出参数错误的异常
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取接口信息对象的名称
        String name = interfaceInfo.getName();

        // 如果是添加操作,所有参数必须非空,否则抛出参数错误的异常
        if (add) {
            if (StringUtils.isAnyBlank(name)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        // 如果接口名称不为空且长度大于50,抛出参数错误的异常,错误信息为"名称过长"
        // 本期写成<50,(没有解决),第二期视频中解决了
        if (StringUtils.isNotBlank(name) && name.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
    }

    @Override
    public Wrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        return null;
    }

    @Override
    public Page<InterfaceInfoVO> getInterfaceInfoVOPage(Page<InterfaceInfo> interfaceInfoPage, HttpServletRequest request) {
        return null;
    }

    @Override
    public Page<InterfaceInfo> searchFromEs(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        return null;
    }

}




