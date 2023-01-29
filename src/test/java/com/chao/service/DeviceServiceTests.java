package com.chao.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class DeviceServiceTests
{
    @Autowired
    DeviceService deviceService;

    @Test
    void testGetDeviceDataCount()
    {
        Integer count;
        count = deviceService.getDeviceDataCount(1L);
        log.info(count.toString());
    }
}
