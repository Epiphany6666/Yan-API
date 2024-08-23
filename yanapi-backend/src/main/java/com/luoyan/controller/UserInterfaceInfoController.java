package com.luoyan.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.luoyan.annotation.AuthCheck;
import com.luoyan.common.*;
import com.luoyan.constant.UserConstant;
import com.luoyan.exception.BusinessException;
import com.luoyan.exception.ThrowUtils;
import com.luoyan.model.dto.userinterfaceinfo.UserInterfaceInfoAddRequest;
import com.luoyan.model.dto.userinterfaceinfo.UserInterfaceInfoEditRequest;
import com.luoyan.model.dto.userinterfaceinfo.UserInterfaceInfoQueryRequest;
import com.luoyan.model.dto.userinterfaceinfo.UserInterfaceInfoUpdateRequest;
import com.luoyan.model.entity.UserInterfaceInfo;
import com.luoyan.model.entity.User;
import com.luoyan.service.UserInterfaceInfoService;
import com.luoyan.service.UserService;
import com.luoyan.yanapiclientsdk.client.YanApiClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 接口管理
 */
@RestController
@RequestMapping("/userUserInterfaceInfo")
@Slf4j
public class UserInterfaceInfoController {

    @Resource
    private UserInterfaceInfoService userUserInterfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private YanApiClient yanApiClient;

    // region 增删改查

    /**
     * 创建
     *
     * @param userUserInterfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addUserInterfaceInfo(@RequestBody UserInterfaceInfoAddRequest userUserInterfaceInfoAddRequest, HttpServletRequest request) {
        if (userUserInterfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userUserInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userUserInterfaceInfoAddRequest, userUserInterfaceInfo);
        userUserInterfaceInfoService.validUserInterfaceInfo(userUserInterfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        userUserInterfaceInfo.setUserId(loginUser.getId());
        boolean result = userUserInterfaceInfoService.save(userUserInterfaceInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newUserInterfaceInfoId = userUserInterfaceInfo.getId();
        return ResultUtils.success(newUserInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUserInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userUserInterfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldUserInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldUserInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = userUserInterfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param userUserInterfaceInfoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUserInterfaceInfo(@RequestBody UserInterfaceInfoUpdateRequest userUserInterfaceInfoUpdateRequest) {
        if (userUserInterfaceInfoUpdateRequest == null || userUserInterfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userUserInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userUserInterfaceInfoUpdateRequest, userUserInterfaceInfo);
        // 参数校验
        userUserInterfaceInfoService.validUserInterfaceInfo(userUserInterfaceInfo, false);
        long id = userUserInterfaceInfoUpdateRequest.getId();
        // 判断是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userUserInterfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldUserInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = userUserInterfaceInfoService.updateById(userUserInterfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserInterfaceInfo> getUserInterfaceInfoVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userUserInterfaceInfo = userUserInterfaceInfoService.getById(id);
        if (userUserInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(userUserInterfaceInfo);
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param userUserInterfaceInfoQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserInterfaceInfo>> listUserInterfaceInfoByPage(@RequestBody UserInterfaceInfoQueryRequest userUserInterfaceInfoQueryRequest) {
        long current = userUserInterfaceInfoQueryRequest.getCurrent();
        long size = userUserInterfaceInfoQueryRequest.getPageSize();
        Page<UserInterfaceInfo> userUserInterfaceInfoPage = userUserInterfaceInfoService.page(new Page<>(current, size),
                userUserInterfaceInfoService.getQueryWrapper(userUserInterfaceInfoQueryRequest));
        return ResultUtils.success(userUserInterfaceInfoPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param userUserInterfaceInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserInterfaceInfo>> listUserInterfaceInfoVOByPage(@RequestBody UserInterfaceInfoQueryRequest userUserInterfaceInfoQueryRequest,
                                                                         HttpServletRequest request) {
        long current = userUserInterfaceInfoQueryRequest.getCurrent();
        long size = userUserInterfaceInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<UserInterfaceInfo> userUserInterfaceInfoPage = userUserInterfaceInfoService.page(new Page<>(current, size),
                userUserInterfaceInfoService.getQueryWrapper(userUserInterfaceInfoQueryRequest));
        return ResultUtils.success(userUserInterfaceInfoPage);
    }
    /**
     * 编辑（用户）
     *
     * @param userUserInterfaceInfoEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editUserInterfaceInfo(@RequestBody UserInterfaceInfoEditRequest userUserInterfaceInfoEditRequest, HttpServletRequest request) {
        if (userUserInterfaceInfoEditRequest == null || userUserInterfaceInfoEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userUserInterfaceInfoEditRequest, userInterfaceInfo);
        // 参数校验
        userUserInterfaceInfoService.validUserInterfaceInfo(userInterfaceInfo, false);
        User loginUser = userService.getLoginUser(request);
        long id = userUserInterfaceInfoEditRequest.getId();
        // 判断是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userUserInterfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldUserInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldUserInterfaceInfo.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = userUserInterfaceInfoService.updateById(userInterfaceInfo);
        return ResultUtils.success(result);
    }

}
