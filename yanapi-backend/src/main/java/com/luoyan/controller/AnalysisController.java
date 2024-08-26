package com.luoyan.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.luoyan.annotation.AuthCheck;
import com.luoyan.common.BaseResponse;
import com.luoyan.common.ErrorCode;
import com.luoyan.common.ResultUtils;
import com.luoyan.exception.BusinessException;
import com.luoyan.mapper.UserInterfaceInfoMapper;
import com.luoyan.model.vo.InterfaceInfoVO;
import com.luoyan.service.InterfaceInfoService;
import com.luoyan.yanapicommon.model.entity.InterfaceInfo;
import com.luoyan.yanapicommon.model.entity.UserInterfaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分析控制器
 */
@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalysisController {

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private InterfaceInfoService interfaceInfoService;
    
	/**
     * 获取调用次数最多的接口信息列表。
     * 通过用户接口信息表查询调用次数最多的接口ID，再关联查询接口详细信息。
     *
     * @return 接口信息列表，包含调用次数最多的接口信息
     */
    @GetMapping("/top/interface/invoke")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<InterfaceInfoVO>> listTopInvokeInterfaceInfo() {
        // 查询调用次数最多的接口信息列表
        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(3);
        // 将接口信息按照接口ID分组，便于关联查询
        Map<Long, List<UserInterfaceInfo>> interfaceInfoIdObjMap = userInterfaceInfoList.stream()
                .collect(Collectors.groupingBy(UserInterfaceInfo::getInterfaceInfoId));
        // 创建查询接口信息的条件包装器
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        // 设置查询条件，使用接口信息ID在接口信息映射中的键集合进行条件匹配
        queryWrapper.in("id", interfaceInfoIdObjMap.keySet());
        // 调用接口信息服务的list方法，传入条件包装器，获取符合条件的接口信息列表
        List<InterfaceInfo> list = interfaceInfoService.list(queryWrapper);
        // 判断查询结果是否为空
        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 构建接口信息VO列表，使用流式处理将接口信息映射为接口信息VO对象，并加入列表中
        List<InterfaceInfoVO> interfaceInfoVOList = list.stream().map(interfaceInfo -> {
            // 创建一个新的接口信息VO对象
            InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
            // 将接口信息复制到接口信息VO对象中
            BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO);
            // 从接口信息ID对应的映射中获取调用次数
            int totalNum = interfaceInfoIdObjMap.get(interfaceInfo.getId()).get(0).getTotalNum();
            // 将调用次数设置到接口信息VO对象中
            interfaceInfoVO.setTotalNum(totalNum);
            // 返回构建好的接口信息VO对象
            return interfaceInfoVO;
        }).collect(Collectors.toList());
        // 返回处理结果
        return ResultUtils.success(interfaceInfoVOList);
    }
}