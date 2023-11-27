package cn.igancao.flinkcdc.config;

import cn.igancao.flinkcdc.common.contants.BaseConstants;
import cn.igancao.flinkcdc.common.contants.FlinkOrdered;
import cn.igancao.flinkcdc.common.enums.MetaModelEnum;
import cn.igancao.flinkcdc.holder.FlinkJobBus;
import cn.igancao.flinkcdc.meta.manage.MemoryMetaManager;
import cn.igancao.flinkcdc.meta.manage.MetaManager;
import cn.igancao.flinkcdc.properties.FlinkProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.ServiceLoader;

@Configuration
@EnableConfigurationProperties(FlinkProperties.class)
@ConditionalOnProperty(name = BaseConstants.ENABLE_PREFIX, havingValue = "true")
public class MetaManagerConfiguration implements Ordered {

    @Autowired
    private FlinkProperties flinkProperties;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public MetaManager metaManager() {
        String metaModel = StringUtils.defaultString(flinkProperties.getMetaModel(), MetaModelEnum.MEMORY.getValue());

        ServiceLoader<MetaManager> metaManagers = ServiceLoader.load(MetaManager.class);
        MetaManager metaManager = new MemoryMetaManager();
        for (MetaManager m : metaManagers) {
            if (metaModel.equals(m.getName())) {
                metaManager = m;
                break;
            }
        }
        FlinkJobBus.setMetaManager(metaManager);
        return metaManager;
    }

    @Override
    public int getOrder() {
        return FlinkOrdered.ORDER_META_MANAGER;
    }
}
