package com.chao.service;

import com.baomidou.mybatisplus.core.toolkit.Sequence;
import com.chao.entity.Device;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class DeviceServiceTests
{
    @Autowired
    DeviceService deviceService;

    @Test
    void testGetDeviceDataCount()
    {
        Sequence sequence = new Sequence();
        Device newDevice = new Device();
        newDevice.setId(sequence.nextId());
        newDevice.setName("新设备");
        newDevice.setIp("192.0.0.6");
        newDevice.setStatus(1);
        newDevice.setCreateTime(LocalDateTime.now());
        deviceService.save(newDevice);
    }
}
