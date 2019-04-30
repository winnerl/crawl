package com.fun.crawl.controller;


import com.fun.crawl.model.dto.SysRoleDTO;
import com.fun.crawl.service.PanUserService;
import com.fun.crawl.util.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@RestController
@RequestMapping("/pan")
@Api(value = "网盘接口", tags = {"网盘操作接口"})
public class LoginPanController {


    @Autowired
    private PanUserService panUserService;

    @Autowired
    private HttpServletRequest request;

    @ApiOperation(value = "获取二维码地址", notes = "网盘接口", httpMethod = "POST")
    @ApiImplicitParam(name = "imageUrl", value = "获取二维码地址", required = true, dataType = "Map")
    @GetMapping("/imageUrl")
    public ApiResult<Map<String,String>> imageUrl(){
        return new ApiResult<Map<String,String>>(panUserService.getQrCodeUrl(request));
    }


    @ApiOperation(value = "判断二维码是否扫描登录", notes = "判断二维码是否扫描登录", httpMethod = "POST")
    @ApiImplicitParam(name = "unicast", value = "判断二维码是否扫描登录", required = true, dataType = "integer")
    @GetMapping("/{sign}")
    public ApiResult<Boolean> unicast(@PathVariable("sign") String sign){
        return new ApiResult<Boolean>(panUserService.unicast(sign));
    }



}
