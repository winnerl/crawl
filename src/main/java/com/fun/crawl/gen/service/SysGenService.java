package com.fun.crawl.gen.service;


import com.fun.crawl.gen.model.dto.BuildConfigDTO;

public interface SysGenService {

    /**
     * 根据表名生成代码
     *
     * @param buildConfigDTO
     * @return
     */
    byte[] genCodeByTableName(BuildConfigDTO buildConfigDTO);


    /**
     * 根据表名生成代码
     *
     * @param buildConfigDTO
     * @return
     */
    void genCodeByTableNameToPackeage(BuildConfigDTO buildConfigDTO);


}
