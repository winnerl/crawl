package com.fun.crawl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fun.crawl.model.FileExtend;
import com.fun.crawl.service.FileExtendService;
import com.fun.crawl.service.PanApiService;
import com.fun.crawl.utils.PanCoreUtil;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.fun.crawl.service.PanApiService.creatTxtFile;
import static com.fun.crawl.service.PanApiService.writeTxtFile;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CrawlTest {


    @Autowired
    private FileExtendService fileExtendService;

    @Test
    public void contextLoads() {
    }


    public  List<FileExtend> generciTreeJSON(List<FileExtend> fileExtends, String pathName, long parentId, String bdstoken, List<FileExtend> allTree) {
        for (FileExtend fileExtend : fileExtends) {
            fileExtend.setParent_id(parentId);
            fileExtendService.save(fileExtend);
            //重新添加到新的集合中
            allTree.add(fileExtend);
            String name = fileExtend.getServer_filename();
            String path_Name = fileExtend.getPath();
            if (fileExtend.getIsdir().equals(1L) && fileExtend.getDir_empty().equals(0L)) {
                List<FileExtend> secondlist = new ArrayList<>();
                secondlist = PanApiService.listAll(bdstoken, path_Name, "time", 1, 0, PanCoreUtil.standard_cookie);
                if (CollectionUtils.isNotEmpty(secondlist)) {
                    generciTreeJSON(secondlist, path_Name, fileExtend.getId(), bdstoken, allTree);
                }
            }
        }
        return allTree;
    }



    @Test
    public void test5() {

//        String bdstoken = "b36239e89e956b05f3afc936fddb1932";
//        String cookie = "STOKEN=674addc4b339a7e52ec29791e143f322a218cb1ac5375ec2fbfe54ff1c39e75c;BDUSS=xNdW1GSzBzclZnfmRkREZiNG5hUzVqSW9wVFFvM3RjaUJ5MnZWflFaVEhFT1ZjSVFBQUFBJCQAAAAAAAAAAAEAAADBdz2kuLXV29HiAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMeDvVzHg71cbm;PTOKEN=e07b1dec7a10aae894fa0924e0c01bff;PASSID=goNKuU;pan_login_way=1;SCRC=165216725eb8d4b7c49301ccf18e911c;PANWEB=1;BAIDUID=7A0ED7379D2DF6FABFF24EF49508F188:FG=1;UBI=fi_PncwhpxZ%7ETaJc7hJSF%7Eepeow4wDjRHhi;PANPSC=8925963882460975593%3AzDDqYSNgXHX%2F1QAio3BlZJgn1M6s6PFh9WUMreUOSM0Rb%2FSlZHlX5dDjoYzeqPGVmldUB8t5cdsS2dE4%2BlKp2nAy8T66GFwAWy0HYE%2FyD5Qcejzkeg3hChBQd5JJtSGFOBgtS35m32hVGMKi8gMzL5C9qgHiau%2Fla7VX5fYmxGfiLaLUl0KD5HjWW6xWDgObhhFKCeua86IBDChpkEtqiw%3D%3D;";
//        PanCoreUtil.standard_cookie = cookie;
//        List<FileExtend> time = PanApiService.list(bdstoken, 1, 500, "/", "time", 1, 0, cookie);
//        List<FileExtend> listTree = new ArrayList<>();//用来存放数据
//        listTree = generciTreeJSON(time, "", 0, bdstoken, listTree);
//        List<FileExtend> listTree = fileExtendService.list();
        QueryWrapper<FileExtend> wrapper = new QueryWrapper<>();
//        wrapper.lambda().eq(FileExtend::getIsdir,1);
        List<FileExtend>   listTre=  fileExtendService.list(wrapper);
        Object o = JSON.toJSON(listTre);
        try {
            boolean json = creatTxtFile("json");


            boolean b = writeTxtFile(o.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }





}
