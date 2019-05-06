package com.fun.crawl.controller;

import com.fun.crawl.model.query.FileExtendQuery;
import com.fun.crawl.service.FileExtendService;
import com.fun.crawl.service.PanUserService;
import com.fun.crawl.util.ApiResult;
import com.fun.crawl.utils.PanApiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    @ApiImplicitParam(name = "page", value = "获取文件列表", required = true, dataType = "Map")
    @GetMapping("/page")
    public ApiResult<FileExtendQuery> puser(FileExtendQuery query) {
        return new ApiResult<FileExtendQuery>(fileExtendService.queryByPage(query));
    }


    @ApiOperation(value = "获取视频文件流", notes = "获取视频文件流", httpMethod = "GET")
    @ApiImplicitParam(name = "获取视频文件流", value = "获取视频文件流", required = true, dataType = "Map")
    @GetMapping("/{oper_id}")
    public ApiResult<String> puser(@PathVariable("oper_id") Long oper_id,
                                   @RequestParam("vpath") String vpath
    ) {
         String videoStream=fileExtendService.getVideoStream(oper_id,vpath);
         return new ApiResult<String>(videoStream);
    }


    @ApiOperation(value = "获取音频文件", notes = "获取音频文件", httpMethod = "GET")
    @ApiImplicitParam(name = "获取音频文件", value = "获取音频文件", required = true, dataType = "Map")
    @GetMapping("/music")
    public ApiResult<String> mpath(@RequestParam("oper_id") Long oper_id,
                                   @RequestParam("mpath") String mpath
    ) {
        String url=fileExtendService.getMusicUrl(oper_id,mpath);
        return new ApiResult<String>(url);
    }


}
