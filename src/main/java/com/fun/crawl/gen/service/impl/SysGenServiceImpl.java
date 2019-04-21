package com.fun.crawl.gen.service.impl;


import com.fun.crawl.gen.GenUtil;
import com.fun.crawl.gen.mapper.ColumnInfoMapper;
import com.fun.crawl.gen.mapper.TableInfoMapper;
import com.fun.crawl.gen.model.dto.BuildConfigDTO;
import com.fun.crawl.gen.model.entity.ColumnInfo;
import com.fun.crawl.gen.model.entity.TableInfo;
import com.fun.crawl.gen.service.SysGenService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.ZipOutputStream;


@Service
public class SysGenServiceImpl implements SysGenService {

    @Autowired
    private TableInfoMapper tableInfoMapper;

    @Autowired
    private ColumnInfoMapper columnInfoMapper;

    @Override
    public byte[] genCodeByTableName(BuildConfigDTO buildConfigDTO) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

        for (String tableName : buildConfigDTO.getTableName()) {
            //查询表信息
           TableInfo table = tableInfoMapper.getOne(tableName);
            //查询列信息
            List<ColumnInfo> columns = columnInfoMapper.listByTableName(tableName);
            //生成代码
            GenUtil.generatorCode(buildConfigDTO,table, columns, zip);
        }
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    /**
     * 根据表名生成代码
     *
     * @param buildConfigDTO
     * @return
     */
    @Override
    public void genCodeByTableNameToPackeage(BuildConfigDTO buildConfigDTO) {

        for (String tableName : buildConfigDTO.getTableName()) {
            //查询表信息
            TableInfo table = tableInfoMapper.getOne(tableName);
            //查询列信息
            List<ColumnInfo> columns = columnInfoMapper.listByTableName(tableName);
            //生成代码
            GenUtil.generatorCodeToPackage(buildConfigDTO,table, columns);
        }






    }


}
