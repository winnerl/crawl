package com.fun.crawl;


import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.rocks.BreadthCrawler;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.apache.commons.lang.StringUtils;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Crawling news from github news
 * 自动爬取新闻网站，继承 BreadthCrawler（广度爬虫）
 * BreadthCrawler 是 WebCollector 最常用的爬取器之一
 *
 * @author hu
 */
public class DemoAutoNewsCrawler extends BreadthCrawler {
    /**
     * @param crawlPath crawlPath is the path of the directory which maintains
     *                  information of this crawler
     * @param autoParse if autoParse is true,BreadthCrawler will auto extract
     *                  links which match regex rules from pag
     */
    public DemoAutoNewsCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);

        /**设置爬取的网站地址
         * addSeed 表示添加种子
         * 种子链接会在爬虫启动之前加入到抓取信息中并标记为未抓取状态.这个过程称为注入*/
        this.addSeed("https://github.blog/");

        /**
         * 循环添加了4个种子，其实就是分页，结果类似：
         * https://github.blog/page/2/
         * https://github.blog/page/3/
         * https://github.blog/page/4/
         * https://github.blog/page/5/
         */
        for (int pageIndex = 2; pageIndex <= 5; pageIndex++) {
            String seedUrl = String.format("https://github.blog/page/%d/", pageIndex);
            this.addSeed(seedUrl);
        }

        /** addRegex 参数为一个 url 正则表达式, 可以用于过滤不必抓取的链接，如 .js .jpg .css ... 等
         * 也可以指定抓取某些规则的链接，如下 addRegex 中会抓取 此类地址：
         * https://github.blog/2018-07-13-graphql-for-octokit/
         * */
        this.addRegex("https://github.blog/[0-9]{4}-[0-9]{2}-[0-9]{2}-[^/]+/");
        /**
         * 过滤 jpg|png|gif 等图片地址 时：
         * this.addRegex("-.*\\.(jpg|png|gif).*");
         * 过滤 链接值为 "#" 的地址时：
         * this.addRegex("-.*#.*");
         */

        /**设置线程数*/
        setThreads(50);
        getConf().setTopN(100);

        /**
         * 是否进行断电爬取，默认为 false
         * setResumable(true);
         */
    }

    /**
     * 必须重写 visit 方法，作用是:
     * 在整个抓取过程中,只要抓到符合要求的页面,webCollector 就会回调该方法,并传入一个包含了页面所有信息的 page 对象
     *
     * @param page
     * @param next
     */
    @Override
    public void visit(Page page, CrawlDatums next) {
        String url = page.url();
        /**如果此页面地址 确实是要求爬取网址，则进行取值
         */
        if (page.matchUrl("https://github.blog/[0-9]{4}-[0-9]{2}-[0-9]{2}[^/]+/")) {

            /**
             * 通过 选择器 获取页面 标题以及 正文内容
             * */
            String title = page.select("h1[class=post__title my-2 f00-light lh-condensed]").first().text();
            String select = page.select("div[class=post__content markdown-body col-12 col-md-10 mb-2 mb-md-4]").first().text();

            System.out.println("URL:\n" + url);
            System.out.println("title:\n" + title);
            System.out.println("content:\n" + select);

        }
    }
    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotEmpty(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    public static void main(String[] args) throws Exception {
        /**
         * DemoAutoNewsCrawler 构造器中会进行 数据初始化，这两个参数接着会传给父类
         * super(crawlPath, autoParse);
         * crawlPath：表示设置保存爬取记录的文件夹，本例运行之后会在应用根目录下生成一个 "crawl" 目录存放爬取信息
         * */
        DemoAutoNewsCrawler crawler = new DemoAutoNewsCrawler("crawl", true);
        /**
         * 启动爬虫，爬取的深度为4层
         * 添加的第一层种子链接,为第1层
         */
        crawler.start(4);
// 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor("jobob");
        gc.setOpen(false);
        // gc.setSwagger2(true); 实体属性 Swagger2 注解
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://localhost:3306/ant?useUnicode=true&useSSL=false&characterEncoding=utf8");
        // dsc.setSchemaName("public");
        dsc.setDriverName("com.mysql.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("密码");
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName(scanner("模块名"));
        pc.setParent("com.baomidou.ant");
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        // 如果模板引擎是 freemarker
        String templatePath = "/templates/mapper.xml.ftl";
        // 如果模板引擎是 velocity
        // String templatePath = "/templates/mapper.xml.vm";

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/resources/mapper/" + pc.getModuleName()
                        + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });
        /*
        cfg.setFileCreate(new IFileCreate() {
            @Override
            public boolean isCreate(ConfigBuilder configBuilder, FileType fileType, String filePath) {
                // 判断自定义文件夹是否需要创建
                checkDir("调用默认方法创建的目录");
                return false;
            }
        });
        */
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        // templateConfig.setEntity("templates/entity2.java");
        // templateConfig.setService();
        // templateConfig.setController();

        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setSuperEntityClass("com.baomidou.ant.common.BaseEntity");
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        strategy.setSuperControllerClass("com.baomidou.ant.common.BaseController");
        strategy.setInclude(scanner("表名，多个英文逗号分割").split(","));
        strategy.setSuperEntityColumns("id");
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setTablePrefix(pc.getModuleName() + "_");
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }

}
