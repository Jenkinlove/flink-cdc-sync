package cn.igancao.flinkcdcdemo.sink;

import cn.igancao.flinkcdc.annotation.FlinkSink;
import cn.igancao.flinkcdc.common.data.DataChangeInfo;
import cn.igancao.flinkcdc.common.service.FlinkJobSink;
import cn.igancao.flinkcdcdemo.domain.TeatSeata;
import cn.igancao.flinkcdcdemo.service.TeatSeataEsService;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Slf4j
@FlinkSink(name = "erp.teat_seata_t1", database = "erp", table = "erp.teat_seata_t1")
public class TeatSeataSink implements FlinkJobSink {
    @Autowired
    private TeatSeataEsService teatSeataEsService;

    @Override
    public void invoke(DataChangeInfo value, Context context) throws Exception {
        log.info("TeatSeataSink invoke, params:[dataChangeInfo:{}, context:{}]", value, context);
    }

    @Override
    public void insert(DataChangeInfo value, Context context) throws Exception {
        log.info("TeatSeataSink insert, params:[dataChangeInfo:{}, context:{}]", value, context);
        TeatSeata teatSeata = JSON.parseObject(value.getAfterData(), TeatSeata.class);
        teatSeataEsService.insertDoc(teatSeata);
    }

    @Override
    public void update(DataChangeInfo value, Context context) throws Exception {
        log.info("TeatSeataSink update, params:[dataChangeInfo:{}, context:{}]", value, context);
        TeatSeata teatSeata = JSON.parseObject(value.getAfterData(), TeatSeata.class);
        teatSeataEsService.updateDoc(teatSeata);
    }

    @Override
    public void delete(DataChangeInfo value, Context context) throws Exception {
        log.info("TeatSeataSink delete, params:[dataChangeInfo:{}, context:{}]", value, context);
        TeatSeata teatSeata = JSON.parseObject(value.getBeforeData(), TeatSeata.class);
        teatSeataEsService.deleteDoc(teatSeata.getId());
    }

    @Override
    public void handleError(DataChangeInfo value, Context context, Throwable throwable) {
        log.error("TeatSeataSink handleError, params:[dataChangeInfo:{}, context:{}], error:{}", value, context, Throwables.getStackTraceAsString(throwable));
    }

    public static void main(String[] args) {
        System.out.println(new Date().getTime());
    }


}
