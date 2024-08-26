package com.luoyan.yanapicommon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.luoyan.yanapicommon.model.entity.User;

/**
 * 用户服务
 */
public interface InnerUserService {

    /**
     * 数据库中查是否已分配给用户密钥（accessKey、secretKey）
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);
}
