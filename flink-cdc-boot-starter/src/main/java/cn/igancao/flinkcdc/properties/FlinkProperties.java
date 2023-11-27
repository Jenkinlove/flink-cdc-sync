package cn.igancao.flinkcdc.properties;

import cn.igancao.flinkcdc.common.contants.BaseConstants;
import cn.igancao.flinkcdc.common.enums.MetaModelEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.Serializable;

@Component
@EnableConfigurationProperties(FlinkProperties.class)
@ConfigurationProperties(prefix = BaseConstants.PREFIX)
@ConditionalOnProperty(name = BaseConstants.ENABLE_PREFIX, havingValue = "true")
@Data
@Slf4j
public class FlinkProperties implements Serializable {

    private String enable;

    /**
     * cursor 记录方式
     */
    private String metaModel;

    private Nacos nacosConfig;

    private Meta meta;

    @Autowired
    @JsonIgnore
    private Environment environment;

    @PostConstruct
    public void init() {
        this.overrideFromEnv();
    }

    /**
     * 从环境中加载元数据信息
     */
    private void overrideFromEnv() {
        if (StringUtils.isBlank(metaModel)) {
            metaModel = MetaModelEnum.MEMORY.getValue();
        } else {
            MetaModelEnum.getMetaModelEnum(metaModel);
        }
        if (this.meta == null) {
            this.meta = new Meta();
        }

        if (StringUtils.isBlank(meta.getApplicationName())) {
            String applicationName = environment.resolvePlaceholders("${spring.application.name:localhost}");
            this.meta.setApplicationName(applicationName);
        }

        if (StringUtils.isBlank(meta.getPort())) {
            String port = environment.resolvePlaceholders("${server.port:8080}");
            this.meta.setPort(port);
        }

        if (StringUtils.isBlank(meta.getDataDir())) {
            String dataDir = environment.resolvePlaceholders("${user.dir}");
            this.meta.setDataDir(dataDir);
        }

        if (meta.getDataDir().endsWith(File.separator)) {
            meta.setDataDir(meta.getDataDir().substring(0, meta.getDataDir().length() - 1));
        }

        log.info("meta info is : [{}]", meta);
    }


    @Data
    public static class Nacos {
        public static final String DEFAULT_GROUP = "DEFAULT_GROUP";

        private Position position;

        @Data
        public static class Position {
            private String key;

            private String dataId;

            private String group = DEFAULT_GROUP;
        }
    }

    @Data
    public static class Meta {
        private String applicationName;

        private String port;

        private String dataDir;
    }
}
