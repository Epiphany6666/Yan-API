package com.luoyan.model.dto.interfaceInfo;

import lombok.Data;
import java.io.Serializable;

/**
 * 创建请求
 *
 * @TableName product
 */
@Data
public class InterfaceInfoAddRequest implements Serializable {

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求头
     */
    private String request_header;

    /**
     * 响应头
     */
    private String response_header;

    /**
     * 请求类型
     */
    private String method;
}