package com.fun.crawl.controller;

import com.fun.crawl.model.query.FileExtendQuery;
import com.fun.crawl.service.FileExtendService;
import com.fun.crawl.service.PanUserService;
import com.fun.crawl.util.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/file")
@Api(value = "网盘接口", tags = {"网盘操作接口"})
public class FilePanController {

    @Autowired
    private PanUserService panUserService;

    @Autowired
    private FileExtendService fileExtendService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;


    @ApiOperation(value = "获取文件列表", notes = "获取文件列表", httpMethod = "GET")
    @ApiImplicitParam(name = "unicast", value = "获取文件列表", required = true, dataType = "Map")
    @GetMapping("/page")
    public ApiResult<FileExtendQuery> puser(FileExtendQuery query) {
        return new ApiResult<>(fileExtendService.pageByQuery(query));




    }





}
