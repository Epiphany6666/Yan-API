package com.luoyan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luoyan.yanapicommon.model.entity.UserInterfaceInfo;

import java.util.List;

/**
* @author 52837
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Mapper
* @createDate 2024-08-23 09:28:26
* @Entity com.luoyan.model.entity.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {
    // 查询用户接口信息表中，按照指定的 limit 参数进行筛选。
    // 返回前 limit 条记录的接口信息列表
    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);

}




