package cn.igancao.flinkcdcdemo.sink;

import cn.igancao.flinkcdc.annotation.FlinkSink;
import cn.igancao.flinkcdc.common.data.DataChangeInfo;
import cn.igancao.flinkcdc.common.service.FlinkJobSink;
import cn.igancao.flinkcdcdemo.domain.ESysDistrict;
import cn.igancao.flinkcdcdemo.service.SysDistrictEsService;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@FlinkSink(name = "erp.e_sys_district_bak", database = "erp", table = "erp.e_sys_district_bak")
public class SysDistrictSink implements FlinkJobSink {

    @Autowired
    private SysDistrictEsService sysDistrictEsService;

    @Override
    public void invoke(DataChangeInfo value, Context context) throws Exception {
        log.info("SysDistrictSink invoke, params:[dataChangeInfo:{}, context:{}]", value, context);
    }

    @Override
    public void insert(DataChangeInfo value, Context context) throws Exception {
        log.info("SysDistrictSink insert, params:[dataChangeInfo:{}, context:{}]", value, context);
        ESysDistrict eSysDistrict = JSON.parseObject(value.getAfterData(), ESysDistrict.class);
        sysDistrictEsService.insertDoc(eSysDistrict);
    }

    @Override
    public void update(DataChangeInfo value, Context context) throws Exception {
        log.info("SysDistrictSink update, params:[dataChangeInfo:{}, context:{}]", value, context);
        ESysDistrict eSysDistrict = JSON.parseObject(value.getAfterData(), ESysDistrict.class);
        sysDistrictEsService.updateDoc(eSysDistrict);
    }

    @Override
    public void delete(DataChangeInfo value, Context context) throws Exception {
        log.info("SysDistrictSink delete, params:[dataChangeInfo:{}, context:{}]", value, context);
        ESysDistrict eSysDistrict = JSON.parseObject(value.getBeforeData(), ESysDistrict.class);
        sysDistrictEsService.deleteDoc(eSysDistrict.getId());
    }

    @Override
    public void handleError(DataChangeInfo value, Context context, Throwable throwable) {
        log.error("SysDistrictSink handleError, params:[dataChangeInfo:{}, context:{}], error:{}", value, context, Throwables.getStackTraceAsString(throwable));
    }
}
