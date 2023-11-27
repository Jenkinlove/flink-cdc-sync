package cn.igancao.flinkcdcdemo;

import org.dromara.easyes.starter.register.EsMapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EsMapperScan("cn.igancao.flinkcdcdemo.mapper.es")
public class FlinkCdcDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlinkCdcDemoApplication.class, args);
    }

}
