package com.fun.crawl.task;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fun.crawl.model.FileExtend;
import com.fun.crawl.model.PanUser;
import com.fun.crawl.service.FileExtendService;
import com.fun.crawl.service.PanUserService;
import com.fun.crawl.utils.PanApiService;
import com.fun.crawl.utils.PanCoreUtil;
import org.apache.commons.collections.CollectionUtils;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class CrawlScheduled {

    @Autowired
    private FileExtendService fileExtendService;

    @Autowired
    private PanUserService panUserService;


    @Scheduled(cron = "0 0 1 * * ?")//每天凌晨1点执行一次
    public void batchInsertMemberWalks() {


    }

    @Scheduled(fixedDelay = 1 * 60 * 1000) // 固定每10分钟执行一次 （执行完后再算时间）
    public void deleteUserWalkStation() {
        List<PanUser> panUserList = panUserService.list();
        for (PanUser panUser : panUserList) {
            String cookie = panUser.getCookie();
            String jsons = panUser.getJsons();
            Long uk = panUser.getUk();
            UpdateWrapper<FileExtend> updateWrapper = new UpdateWrapper();
            updateWrapper.lambda().eq(FileExtend::getIs_exist, 1L);
            updateWrapper.lambda().eq(FileExtend::getOper_id, uk);
            FileExtend fileExtend = new FileExtend();
            fileExtend.setIs_exist(0L);
            boolean update = fileExtendService.update(fileExtend, updateWrapper);
            try {
                Map<String, String> map = PanCoreUtil.toMap(jsons);
                String bdstoken = map.get("bdstoken");
                genericData(bdstoken, cookie);

                QueryWrapper<FileExtend> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(FileExtend::getIs_exist, 0L);
                int sumDel = fileExtendService.deleteByIsDelete(0L);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private void genericData(String bdstoken, String cookie) {
        PanCoreUtil.standard_cookie = cookie;
        List<FileExtend> fileExtendList = PanApiService.list(bdstoken, 1, 500, "/", "name", 0, 0, cookie);
        generciTreeJSON(fileExtendList, "", 0, bdstoken);
    }


    public void generciTreeJSON(List<FileExtend> fileExtends, String pathName, long parentId, String bdstoken) {
        for (FileExtend fileExtend : fileExtends) {
            fileExtend.setParent_id(parentId);
            fileExtend.setModify_time(new Date());
            QueryWrapper<FileExtend> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(FileExtend::getFs_id, fileExtend.getFs_id());
           FileExtend fileExtendSel = fileExtendService.getOne(queryWrapper);
            if (fileExtendSel== null) {
                fileExtend.setCreate_time(new Date());
                fileExtend.setIs_exist(1L);
                fileExtendService.save(fileExtend);
            } else {
                fileExtend=fileExtendSel;
                fileExtend.setModify_time(new Date());
                fileExtend.setIs_exist(1L);
                fileExtendService.updateById(fileExtend);
            }


            //重新添加到新的集合中
            String name = fileExtend.getServer_filename();
            String path_Name = fileExtend.getPath();
            if (fileExtend.getIsdir().equals(1L) && fileExtend.getDir_empty().equals(0L)) {
                List<FileExtend> secondlist = new ArrayList<>();
                secondlist = PanApiService.listAll(bdstoken, path_Name, "name", 0, 0, PanCoreUtil.standard_cookie);
                if (CollectionUtils.isNotEmpty(secondlist)) {
                    generciTreeJSON(secondlist, path_Name, fileExtend.getId(), bdstoken);
                }
            }
        }
    }


}

