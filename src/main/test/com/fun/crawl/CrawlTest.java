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
import java.util.concurrent.*;

import static com.fun.crawl.service.PanApiService.creatTxtFile;
import static com.fun.crawl.service.PanApiService.writeTxtFile;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CrawlTest {

    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);

    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(16, 16, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);



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
                secondlist = PanApiService.listAll(bdstoken, path_Name, "name", 0, 0, PanCoreUtil.standard_cookie);
                if (CollectionUtils.isNotEmpty(secondlist)) {
                    generciTreeJSON(secondlist, path_Name, fileExtend.getId(), bdstoken, allTree);
                }
            }
        }
        return allTree;
    }



    @Test
    public void test5() {
        //盘1
//        String bdstoken = "b36239e89e956b05f3afc936fddb1932";
//        String cookie = "STOKEN=674addc4b339a7e52ec29791e143f322a218cb1ac5375ec2fbfe54ff1c39e75c;BDUSS=xNdW1GSzBzclZnfmRkREZiNG5hUzVqSW9wVFFvM3RjaUJ5MnZWflFaVEhFT1ZjSVFBQUFBJCQAAAAAAAAAAAEAAADBdz2kuLXV29HiAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMeDvVzHg71cbm;PTOKEN=e07b1dec7a10aae894fa0924e0c01bff;PASSID=goNKuU;pan_login_way=1;SCRC=165216725eb8d4b7c49301ccf18e911c;PANWEB=1;BAIDUID=7A0ED7379D2DF6FABFF24EF49508F188:FG=1;UBI=fi_PncwhpxZ%7ETaJc7hJSF%7Eepeow4wDjRHhi;PANPSC=8925963882460975593%3AzDDqYSNgXHX%2F1QAio3BlZJgn1M6s6PFh9WUMreUOSM0Rb%2FSlZHlX5dDjoYzeqPGVmldUB8t5cdsS2dE4%2BlKp2nAy8T66GFwAWy0HYE%2FyD5Qcejzkeg3hChBQd5JJtSGFOBgtS35m32hVGMKi8gMzL5C9qgHiau%2Fla7VX5fYmxGfiLaLUl0KD5HjWW6xWDgObhhFKCeua86IBDChpkEtqiw%3D%3D;";
//盘2
//        String bdstoken = "ce227419da0dd342072c269b9fad2425";
//        String cookie = "STOKEN=fee993085adff9f4b60bed7bdfde75acde104e4549e3bcf27575da04f1d8ce6a;BDUSS=DJpbElTNHVRNG1YalFzRGlwZnRQOWxWY3h1MDNOUkppcXBhaEF2OGprc3hiZVZjRVFBQUFBJCQAAAAAAAAAAAEAAAA3KpLzu6q46DEwMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADHgvVwx4L1cU;PTOKEN=81155e453fe07f14ee39803a6af1c383;PASSID=Ao8OCU;pan_login_way=1;SCRC=115981f050750a7bcfa9240196228671;PANWEB=1;BAIDUID=CD2951A5294FB2ECA7E625A2B7492BCB:FG=1;UBI=fi_PncwhpxZ%7ETaJc05dAqGIH5nnT6MktKSi;PANPSC=12077440575639904876%3A42QK88Byhyau5rWhU59w5mcohJxoEIVK41JYFyfxTjEn6x%2Fevz%2BGJAZuq6kOUHWCq1ytK3VLUr02UCfo%2F%2Btcu%2FM5ssIgGrSsDonIysmqxJtFah9uWgECZ6pSErueTcfn7QFBovD43aui%2BY1X7udr9G2gud9gLjYtRogbeq4A%2FVImU589kUerftFHJCSdHjFtfPVXoOSO8Qs7qAHgpfUwRg%3D%3D;";
//盘3
        String bdstoken = "edd45be886bb078b0cac7fefe55c6ead";
        String cookie = "STOKEN=9c4a8b6e1d346fad16325be0eccdaedba397306c19728824b69afffa12125145;BDUSS=dUQk9vN2gza3plcy0tUzRNWTg2dTNtbjlEdzk3bjFlNHA5dFNGcC1PVUJjLVZjRVFBQUFBJCQAAAAAAAAAAAEAAAAVPr6Xu6q46DEwMgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHmvVwB5r1ceE;PTOKEN=89330098339f760e6c541a8131d39686;PASSID=7aktIm;pan_login_way=1;SCRC=3135c758da7c7e3732617f300057a8a1;PANWEB=1;BAIDUID=2FFD8D768B71ABAE6E6041466E6DD988:FG=1;UBI=fi_PncwhpxZ%7ETaJc35mhUrhSR%7EBgjVNRb9Y;PANPSC=14694853682027687889%3ADlDk7b64pQdfJXcrm8dr8pgn1M6s6PFhZpKg6vHEZYvWLfyJno3RQgjn4HR56deP%2BWHvCWahCWJVVrvvMfx5cSQh6I2BTZ9zBbuKXKm819xhBaVJALTttrpg0t5RWtZXRNA9VDv6F%2BN2TQaGfItAk1vKX4DMNpv6HhgzMjI7O4rZ81l%2BOzvAFuyf1EfF%2F6a8ECu7OGpslid9bfFV5UX2VQ%3D%3D;";
        PanCoreUtil.standard_cookie = cookie;
//        List<FileExtend> time = PanApiService.list(bdstoken, 1, 500, "/", "name", 1, 0, cookie);
        List<FileExtend> listTree = new ArrayList<>();//用来存放数据
//        generciTreeJSON(time, "", 0, bdstoken, listTree);
        QueryWrapper<FileExtend> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(FileExtend::getIsdir,1);
        wrapper.lambda().eq(FileExtend::getOper_id,1049844577);
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
