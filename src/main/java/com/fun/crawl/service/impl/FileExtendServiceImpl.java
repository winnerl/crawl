package com.fun.crawl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fun.crawl.base.service.impl.BaseServiceImpl;
import com.fun.crawl.exception.BusinessException;
import com.fun.crawl.mapper.FileExtendMapper;
import com.fun.crawl.model.FileExtend;
import com.fun.crawl.model.PanUser;
import com.fun.crawl.model.query.FileExtendQuery;
import com.fun.crawl.service.FileExtendService;
import com.fun.crawl.service.PanUserService;
import com.fun.crawl.utils.PanApiService;
import com.fun.crawl.utils.PanCoreUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jobob
 * @since 2019-04-22
 */
@Service
public class FileExtendServiceImpl extends BaseServiceImpl<FileExtendMapper, FileExtend> implements FileExtendService {


    @Autowired
    private FileExtendMapper fileExtendMapper;

    @Autowired
    private PanUserService panUserService;


    @Override
    public int deleteByIsDelete(long isDel) {
        QueryWrapper<FileExtend> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(FileExtend::getIs_exist, 0L);
        return fileExtendMapper.delete(wrapper);
    }

    @Override
    public FileExtendQuery queryByPage(FileExtendQuery query) {
        fileExtendMapper.queryByPage(query);
        return query;
    }

    @Override
    public String getVideoStream(Long uk, String path) {
        PanUser panUser = panUserService.selectByUk(uk);
        if (panUser == null) {
            throw new BusinessException("pan is not exist");
        }
        String cookie = panUser.getCookie();
        String jsons = panUser.getJsons();

        QueryWrapper<FileExtend> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(FileExtend::getPath, path);
        queryWrapper.lambda().eq(FileExtend::getOper_id, uk);
        FileExtend fileExtend = fileExtendMapper.selectOne(queryWrapper);
        if (fileExtend.getCategory().intValue() != 1) {
            throw new BusinessException("file type error");
        }

        try {
            Map<String, String> map = PanCoreUtil.toMap(jsons);
            String bdstoken = map.get("bdstoken");
            String videoAdToken = PanApiService.getVideoAdToken(bdstoken, path, cookie);
            String videoStream = PanApiService.getVideoStream(videoAdToken, path, cookie);
            if (videoStream.indexOf("\"errno\":133") != -1) {
                JSONObject parse = (JSONObject) JSON.parse(videoStream);
                videoAdToken = parse.getString("adToken");
                videoStream = PanApiService.getVideoStream(videoAdToken, path, cookie);
            }

            return videoStream;
        } catch (Exception e) {

        }
        return "";
    }

    /**
     * 获取音乐文件下载地址
     *
     * @param oper_id
     * @param mpath
     * @return
     */
    @Override
    public String getMusicUrl(Long uk, String path) {

        PanUser panUser = panUserService.selectByUk(uk);
        if (panUser == null) {
            throw new BusinessException("pan is not exist");
        }
        String cookie = panUser.getCookie();
        String jsons = panUser.getJsons();
        QueryWrapper<FileExtend> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(FileExtend::getPath, path);
        queryWrapper.lambda().eq(FileExtend::getOper_id, uk);
        FileExtend fileExtend = fileExtendMapper.selectOne(queryWrapper);
        if (fileExtend.getCategory().intValue() != 2) {
            throw new BusinessException("file type error");
        }

        try {
            Map<String, String> map = PanCoreUtil.toMap(jsons);
            String bdstoken = map.get("bdstoken");
            String sign3 = map.get("sign3");
            String sign1 = map.get("sign1");
            String timestamp = map.get("timestamp");
            String sign = PanCoreUtil.getDownloadSign(sign3, sign1);
            List<Long> fidles = new ArrayList<>();
            fidles.add(fileExtend.getFs_id());
            String url = PanApiService.apiDownloadURL(bdstoken, sign, fidles, timestamp, cookie);
            if (url.indexOf("\"errno\":112") != -1) {

            }

            return url;
        } catch (Exception e) {
            e.printStackTrace();
            PanCoreUtil.diskHome();
            PanCoreUtil.sendTodiskHomeOne("https://pan.baidu.com/disk/home?errno=0&errmsg=Auth%20Login%20Sucess&&bduss=&ssnerror=0&traceid=#/all?path=%2F&vmode=list");
            Map<String, String> smap = PanCoreUtil.sendTodiskHomeTwo();
            String bdstoken = smap.get("bdstoken");
            String userPaninfo = "";
            try {
                userPaninfo = PanCoreUtil.mapToJson(smap, false);
                PanUser newpanUser = new PanUser();
                Map<String, String> headMap = PanCoreUtil.xmlHttpHead();
                String headMapString = PanCoreUtil.mapToJson(headMap, false);

                newpanUser.setCookie(PanCoreUtil.standard_cookie)
                        .setCreateTime(new Date())
                        .setHeaders(headMapString)
                        .setUk(Long.valueOf(smap.get("uk")))
                        .setJsons(userPaninfo)
                        .setPanName(smap.get("username"))
                        .setModifyTime(new Date())
                        .setUid(0L);
                boolean save = panUserService.save(newpanUser);

            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
        }
        return "";
    }


}
