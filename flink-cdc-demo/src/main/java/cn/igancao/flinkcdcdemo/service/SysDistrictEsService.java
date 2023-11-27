package cn.igancao.flinkcdcdemo.service;

import cn.igancao.flinkcdcdemo.domain.ESysDistrict;
import cn.igancao.flinkcdcdemo.mapper.es.SysDistrictESMapper;
import org.dromara.easyes.annotation.IndexName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class SysDistrictEsService {

    @Autowired
    private SysDistrictESMapper sysDistrictESMapper;

    @PostConstruct
    public void init() {
        IndexName indexName = ESysDistrict.class.getAnnotation(IndexName.class);
        Boolean exist = sysDistrictESMapper.existsIndex(indexName.value());
        if (Boolean.FALSE.equals(exist)) {
            //创建索引
            createEsIndex();
        }
    }

    /**
     * 创建索引
     */
    public void createEsIndex() {
        sysDistrictESMapper.createIndex();
    }


    public void deleteEsIndex() {
        IndexName indexName = ESysDistrict.class.getAnnotation(IndexName.class);
        sysDistrictESMapper.deleteIndex(indexName.value());
    }

    public void insertDoc(ESysDistrict esSysDistrict) {
        sysDistrictESMapper.insert(esSysDistrict);
    }

    public void updateDoc(ESysDistrict esSysDistrict) {
        sysDistrictESMapper.updateById(esSysDistrict);
    }

    public void deleteDoc(Long id) {
        sysDistrictESMapper.deleteById(id);
    }
}
