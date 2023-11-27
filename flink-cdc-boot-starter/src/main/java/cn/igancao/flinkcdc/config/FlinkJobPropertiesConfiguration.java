package cn.igancao.flinkcdc.config;

import cn.igancao.flinkcdc.common.contants.BaseConstants;
import cn.igancao.flinkcdc.common.contants.FlinkOrdered;
import cn.igancao.flinkcdc.common.enums.StartupModeEnum;
import cn.igancao.flinkcdc.common.exception.FlinkCDCException;
import cn.igancao.flinkcdc.holder.FlinkJobPropertiesHolder;
import cn.igancao.flinkcdc.properties.FlinkJobProperties;
import cn.igancao.flinkcdc.properties.FlinkProperties;
import com.google.common.base.Throwables;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectReader;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * flink job 配置信息管理
 */
@Configuration
@EnableConfigurationProperties(FlinkProperties.class)
@ConditionalOnProperty(name = BaseConstants.ENABLE_PREFIX, havingValue = "true")
public class FlinkJobPropertiesConfiguration implements BeanFactoryPostProcessor, Ordered {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        //获取配置文件 resource
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(BaseConstants.CONFIG_FILE);
        if (resource.exists()) {
            //加载配置文件
            registerProperties(resource);
        } else {
            throw new FlinkCDCException("flink config file not found!");
        }
    }

    /**
     * 获取配置文件
     */
    private void registerProperties(Resource resource) {
        try {
            JsonNode jsonNode = objectMapper.readTree(resource.getInputStream());
            ObjectReader objectReader = objectMapper.readerFor(FlinkJobProperties.class);
            if (jsonNode.isArray()) {
                for (JsonNode node : jsonNode) {
                    FlinkJobProperties flinkJobProperties = objectReader.readValue(node);
                    //校验cdc启动类型是否正确
                    StartupModeEnum.getStartupMode(flinkJobProperties.getStartupMode());
                    FlinkJobPropertiesHolder.registerProperties(flinkJobProperties);
                }
            } else if (jsonNode.isObject()) {
                FlinkJobProperties flinkJobProperties = objectReader.readValue(jsonNode);
                //校验cdc启动类型是否正确
                StartupModeEnum.getStartupMode(flinkJobProperties.getStartupMode());
                FlinkJobPropertiesHolder.registerProperties(flinkJobProperties);
            }
        } catch (IOException e) {
            throw new FlinkCDCException("flink config parse error, error: " + Throwables.getStackTraceAsString(e));
        }
    }

    @Override
    public int getOrder() {
        return FlinkOrdered.ORDER_LISTENER_PROPERTIES;
    }

}
