package com.joeylee.common.domain.entity;

import com.joeylee.common.utils.http.BaseHttpUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 节点信息
 *
 * @author joeylee
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RibbonServer {

    private String host;

    private Integer port;

    public String getUrl() {
        return BaseHttpUtil.getMiddle(host, port);
    }
}