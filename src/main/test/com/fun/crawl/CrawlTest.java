//package com.fun.crawl;
//
//import com.alibaba.fastjson.JSON;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.fun.crawl.model.FileExtend;
//import com.fun.crawl.service.FileExtendService;
//import com.fun.crawl.utils.PanApiService;
//import com.fun.crawl.utils.PanCoreUtil;
//import org.apache.commons.collections.CollectionUtils;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.RejectedExecutionHandler;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
//import static com.fun.crawl.utils.PanApiService.creatTxtFile;
//import static com.fun.crawl.utils.PanApiService.writeTxtFile;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class CrawlTest {
//
//    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);
//
//    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();
//
//    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(16, 16, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);
//
//
//
//    @Autowired
//    private FileExtendService fileExtendService;
//
//
//
//    @Test
//    public void contextLoads() {
//    }
//
//
//    public  List<FileExtend> generciTreeJSON(List<FileExtend> fileExtends, String pathName, long parentId, String bdstoken, List<FileExtend> allTree) {
//        for (FileExtend fileExtend : fileExtends) {
//            fileExtend.setParent_id(parentId);
//            fileExtendService.save(fileExtend);
//            //重新添加到新的集合中
//            allTree.add(fileExtend);
//            String name = fileExtend.getServer_filename();
//            String path_Name = fileExtend.getPath();
//            if (fileExtend.getIsdir().equals(1L) && fileExtend.getDir_empty().equals(0L)) {
//                List<FileExtend> secondlist = new ArrayList<>();
//                secondlist = PanApiService.listAll(bdstoken, path_Name, "name", 0, 0, PanCoreUtil.standard_cookie);
//                if (CollectionUtils.isNotEmpty(secondlist)) {
//                    generciTreeJSON(secondlist, path_Name, fileExtend.getId(), bdstoken, allTree);
//                }
//            }
//        }
//        return allTree;
//    }
//
//
//    public  void  generic(List<FileExtend> fileExtends, String pathName, long parentId, String bdstoken, List<FileExtend> allTree) {
//
//
//
//
//    }
//
//
//
//
//
//    @Test
//    public void test4() {
//
//
//    }
//
//    @Test
//    public void test5() {
//        //盘1
//        String bdstoken1 = "b36239e89e956b05f3afc936fddb1932";
//        String cookie1 = "STOKEN=674addc4b339a7e52ec29791e143f322a218cb1ac5375ec2fbfe54ff1c39e75c;BDUSS=xNdW1GSzBzclZnfmRkREZiNG5hUzVqSW9wVFFvM3RjaUJ5MnZWflFaVEhFT1ZjSVFBQUFBJCQAAAAAAAAAAAEAAADBdz2kuLXV29HiAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMeDvVzHg71cbm;PTOKEN=e07b1dec7a10aae894fa0924e0c01bff;PASSID=goNKuU;pan_login_way=1;SCRC=165216725eb8d4b7c49301ccf18e911c;PANWEB=1;BAIDUID=7A0ED7379D2DF6FABFF24EF49508F188:FG=1;UBI=fi_PncwhpxZ%7ETaJc7hJSF%7Eepeow4wDjRHhi;PANPSC=8925963882460975593%3AzDDqYSNgXHX%2F1QAio3BlZJgn1M6s6PFh9WUMreUOSM0Rb%2FSlZHlX5dDjoYzeqPGVmldUB8t5cdsS2dE4%2BlKp2nAy8T66GFwAWy0HYE%2FyD5Qcejzkeg3hChBQd5JJtSGFOBgtS35m32hVGMKi8gMzL5C9qgHiau%2Fla7VX5fYmxGfiLaLUl0KD5HjWW6xWDgObhhFKCeua86IBDChpkEtqiw%3D%3D;";
////盘2
//        String bdstoken2 = "ce227419da0dd342072c269b9fad2425";
//        String cookie2 = "STOKEN=fee993085adff9f4b60bed7bdfde75acde104e4549e3bcf27575da04f1d8ce6a;BDUSS=DJpbElTNHVRNG1YalFzRGlwZnRQOWxWY3h1MDNOUkppcXBhaEF2OGprc3hiZVZjRVFBQUFBJCQAAAAAAAAAAAEAAAA3KpLzu6q46DEwMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADHgvVwx4L1cU;PTOKEN=81155e453fe07f14ee39803a6af1c383;PASSID=Ao8OCU;pan_login_way=1;SCRC=115981f050750a7bcfa9240196228671;PANWEB=1;BAIDUID=CD2951A5294FB2ECA7E625A2B7492BCB:FG=1;UBI=fi_PncwhpxZ%7ETaJc05dAqGIH5nnT6MktKSi;PANPSC=12077440575639904876%3A42QK88Byhyau5rWhU59w5mcohJxoEIVK41JYFyfxTjEn6x%2Fevz%2BGJAZuq6kOUHWCq1ytK3VLUr02UCfo%2F%2Btcu%2FM5ssIgGrSsDonIysmqxJtFah9uWgECZ6pSErueTcfn7QFBovD43aui%2BY1X7udr9G2gud9gLjYtRogbeq4A%2FVImU589kUerftFHJCSdHjFtfPVXoOSO8Qs7qAHgpfUwRg%3D%3D;";
////盘3
//        String bdstoken3 = "edd45be886bb078b0cac7fefe55c6ead";
//        String cookie3 = "STOKEN=9c4a8b6e1d346fad16325be0eccdaedba397306c19728824b69afffa12125145;BDUSS=dUQk9vN2gza3plcy0tUzRNWTg2dTNtbjlEdzk3bjFlNHA5dFNGcC1PVUJjLVZjRVFBQUFBJCQAAAAAAAAAAAEAAAAVPr6Xu6q46DEwMgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHmvVwB5r1ceE;PTOKEN=89330098339f760e6c541a8131d39686;PASSID=7aktIm;pan_login_way=1;SCRC=3135c758da7c7e3732617f300057a8a1;PANWEB=1;BAIDUID=2FFD8D768B71ABAE6E6041466E6DD988:FG=1;UBI=fi_PncwhpxZ%7ETaJc35mhUrhSR%7EBgjVNRb9Y;PANPSC=14694853682027687889%3ADlDk7b64pQdfJXcrm8dr8pgn1M6s6PFhZpKg6vHEZYvWLfyJno3RQgjn4HR56deP%2BWHvCWahCWJVVrvvMfx5cSQh6I2BTZ9zBbuKXKm819xhBaVJALTttrpg0t5RWtZXRNA9VDv6F%2BN2TQaGfItAk1vKX4DMNpv6HhgzMjI7O4rZ81l%2BOzvAFuyf1EfF%2F6a8ECu7OGpslid9bfFV5UX2VQ%3D%3D;";
//      //盘4
//        String bdstoken4 = "15d6ee935396fd51987973783b3cb19f";
//        String cookie4 ="STOKEN=aae86bf44f7049d74c00a83cc61c8b13e6aea6c40ac49d2d286f7d77d9001574;BDUSS=lVTkFyWWlTb1dpY1U2MUExNX4yQ2VFbTI3WmNEdGpZUlpFUFBhUjBGcE0tLVZjSVFBQUFBJCQAAAAAAAAAAAEAAADCZZLzu6q46DEwNAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAExuvlxMbr5cWk;PTOKEN=0b7ab3c6b889456463d74e0ba66f02ff;PASSID=BsJTfB;pan_login_way=1;SCRC=641d5b5a97e24a5debbf6e6cd4b22e49;PANWEB=1;BAIDUID=890A8D1A084180D7FDD195375675C502:FG=1;UBI=fi_PncwhpxZ%7ETaJczMO2tN-5A1KMaCmXkqH;PANPSC=11369346992412844422%3AjpTWThggNVT7O3vLMRXq0Zgn1M6s6PFhZpKg6vHEZYtV9L96im1g1Ajn4HR56deP%2BWHvCWahCWLpzGM%2F8sAFyCQh6I2BTZ9zIJYi%2Fy6%2BMkZhBaVJALTttmM0MRXtmcWVy0TsRJZx%2FdF2TQaGfItAk1vKX4DMNpv6HhgzMjI7O4rZ81l%2BOzvAFtukglyu8ZF1ECu7OGpslid9bfFV5UX2VQ%3D%3D;";
//
//      //盘5
//        String bdstoken5 = "025c21f02c5388862492814b30cdfda4";
//        String cookie5 ="STOKEN=81a36b6b063182f63f9f3da750480478730dcdcea4bc350c069b35cb98922dc0;BDUSS=FBmdE94fnZ1TWtPbFh1M3Fya3pELXZUSWNSfmowZHhUQjlVV3M1ZEZkcTF-ZVZjSVFBQUFBJCQAAAAAAAAAAAEAAADHW7vzu6q46DEwNQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAALVwvly1cL5cR;PTOKEN=b133d51efb174841cb20dda8fe7a7ae5;PASSID=gaqh0U;pan_login_way=1;SCRC=24b58915c41d9193ddbc8514297001f1;PANWEB=1;BAIDUID=D427F05EAB1FB6E4D6250A3E0700150D:FG=1;UBI=fi_PncwhpxZ%7ETaJc8r41xM91LmmnLLGvkPj;PANPSC=2532267171995234703%3AfLE2AaQsAy9PTIY2%2BkA715gn1M6s6PFhZpKg6vHEZYtNj7dWFKAKRQjn4HR56deP%2BWHvCWahCWJbARaCq7%2FByiQh6I2BTZ9z2uDs6CLtOh1hBaVJALTttiqlH64llb7AnVHzpvZ0Ztd2TQaGfItAk1vKX4DMNpv6HhgzMjI7O4rZ81l%2BOzvAFtukglyu8ZF1ECu7OGpslid9bfFV5UX2VQ%3D%3D;";
//
//
//        genericJSONtext(bdstoken1, cookie1,"1");
////        genericJSONtext(bdstoken2, cookie2,"2");
////        genericJSONtext(bdstoken3, cookie3,"3");
////        genericJSONtext(bdstoken4, cookie4,"4");
////        genericJSONtext(bdstoken5, cookie5,"5");
//        QueryWrapper<FileExtend> wrapper = new QueryWrapper<>();
//
//        wrapper.lambda().eq(FileExtend::getIsdir,1);
//        wrapper.lambda().eq(FileExtend::getOper_id,1542949220);
//        List<FileExtend>   listTre=  fileExtendService.list(wrapper);
//        Object o = JSON.toJSON(listTre);
//        try {
//            boolean json = creatTxtFile(5+"");
//            boolean b = writeTxtFile(o.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void genericJSONtext(String bdstoken, String cookie,String index) {
//        PanCoreUtil.standard_cookie = cookie;
//        List<FileExtend> time = PanApiService.list(bdstoken, 1, 500, "/", "name", 0, 0, cookie);
//        List<FileExtend> listTree = new ArrayList<>();//用来存放数据
//        List<FileExtend> fileExtends = generciTreeJSON(time, "", 0, bdstoken, listTree);
//
//        Object o = JSON.toJSON(fileExtends);
//        try {
//            boolean json = creatTxtFile(index);
//            boolean b = writeTxtFile(o.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//}
