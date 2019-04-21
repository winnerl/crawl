package com.fun.crawl.gen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fun.crawl.gen.mapper.TableInfoMapper;
import com.fun.crawl.gen.model.entity.TableInfo;
import com.fun.crawl.gen.model.query.TableInfoQuery;
import com.fun.crawl.gen.service.TableInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TableInfoServiceImpl extends ServiceImpl<TableInfoMapper, TableInfo> implements TableInfoService {

    @Autowired
    private TableInfoMapper tableInfoMapper;

    @Override
    public TableInfoQuery pageByQuery(TableInfoQuery query) {
        tableInfoMapper.pageByQuery(query);
        return query;
    }

    @Override
    public TableInfo getOne(String tableName) {
        return tableInfoMapper.getOne(tableName);
    }
}
