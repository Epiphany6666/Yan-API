package com.luoyan.service.impl.inner;

import com.luoyan.yanapicommon.model.entity.InterfaceInfo;
import com.luoyan.yanapicommon.service.InnerInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {
    @Override
    public InterfaceInfo getInterfaceInfo(String path, String method) {
        return null;
    }
}
