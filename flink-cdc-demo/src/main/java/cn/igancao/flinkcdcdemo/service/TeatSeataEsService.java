package cn.igancao.flinkcdcdemo.service;

import cn.igancao.flinkcdcdemo.domain.TeatSeata;
import cn.igancao.flinkcdcdemo.mapper.es.TeatSeataEsMapper;
import org.dromara.easyes.annotation.IndexName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TeatSeataEsService {

    @Autowired
    private TeatSeataEsMapper teatSeataEsMapper;

    @PostConstruct
    public void init() {
        IndexName indexName = TeatSeata.class.getAnnotation(IndexName.class);
        Boolean exist = teatSeataEsMapper.existsIndex(indexName.value());
        if (Boolean.FALSE.equals(exist)) {
            //创建索引
            createEsIndex();
        }
    }

    /**
     * 创建索引
     */
    public void createEsIndex() {
        teatSeataEsMapper.createIndex();
    }

    public void insertDoc(TeatSeata teatSeata) {
        teatSeataEsMapper.insert(teatSeata);
    }

    public void updateDoc(TeatSeata teatSeata) {
        teatSeataEsMapper.updateById(teatSeata);
    }

    public void deleteDoc(Long id) {
        teatSeataEsMapper.deleteById(id);
    }
}
