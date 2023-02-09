package com.chao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
public class GateSystemApplication
{
    public static void main(String[] args)
    {
        System.setProperty("druid.mysql.usePingMethod","false");
        SpringApplication.run(GateSystemApplication.class, args);
        log.info("服务器启动!");
    }

}
