package com.chao.service;

import com.baomidou.mybatisplus.core.toolkit.Sequence;
import com.chao.common.CommonEnum;
import com.chao.common.WebSocketConfigurator;
import com.chao.entity.Device;
import com.chao.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/deviceSocket", configurator = WebSocketConfigurator.class)
@Component
@Slf4j
public class DeviceWebSocket
{
    private static DeviceService deviceService;

    private static UserService userService;

    private Session session;
    // 当前设备的ID
    private Long deviceID = 0L;
    // 当前设备的名字
    private String deviceName = "未初始化设备";
    // 当前设备IP
    private String deviceIP = "0.0.0.0";
    // 当前接收的卡号
    private Long receiveCardID = 0L;
    // 下一个应该接收到的信息种类
    private byte exceptMessage = 0;

    private static final CopyOnWriteArraySet<DeviceWebSocket> deviceWebSocketSet = new CopyOnWriteArraySet<>();

    /*https://www.cnblogs.com/dyingstraw/p/12865800.html#
    spring管理的都是单例（singleton），和websocket（多对象）相冲突。
    项目启动时初始化，会初始化websocket（非用户连接的），spring同时会为其注入service，该对象的service不是null，被成功注入。
    但是，由于spring默认管理的是单例，所以只会注入一次service。当新用户进入聊天时，系统又会创建一个新的websocket对象，
    这时矛盾出现了：spring管理的都是单例，不会给第二个websocket对象注入service，所以导致只要是用户连接创建的websocket对象，都不能再注入了。

    像controller里面有service，service里面有dao。因为controller，service，dao都有是单例，所以注入时不会报null。
    但是websocket不是单例，所以使用spring注入一次后，后面的对象就不会再注入了，会报null。
    */
    @Autowired
    public void setDeviceService(DeviceService deviceService)
    {
        DeviceWebSocket.deviceService = deviceService;
    }

    @Autowired
    public void setUserService(UserService userService)
    {
        DeviceWebSocket.userService = userService;
    }

    @OnOpen
    public void onOpen(Session session)
    {
        this.session = session;
        this.exceptMessage = MessageType.DEVICE_SEND_DEVICE_DATA;
        this.deviceIP = (String) this.session.getUserProperties().get("ip");
//        deviceWebSocketSet.add(this);

        log.info("[webSocket消息] 有新的连接，总数：{}", deviceWebSocketSet.size() + 1);
    }

    @OnClose
    public void onClose()
    {
        //数据库更新状态
        {
            Device deviceToSql = new Device();
            deviceToSql.setId(this.deviceID);
            deviceToSql.setStatus(CommonEnum.DEVICE_STATUS_OFFLINE);
            deviceService.updateById(deviceToSql);
        }
        deviceWebSocketSet.remove(this);
        log.info("[webSocket消息] 连接断开，总数：{}", deviceWebSocketSet.size());
    }

    @OnMessage
    public void onMessage(ByteBuffer byteBuffer)
    {
        log.info("[webSocket消息] 收到消息种类：{}", byteBuffer.get(0));
        try
        {
            messageHandle(byteBuffer.array(), byteBuffer.array().length);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 通过设备id来查找对应的socket
     *
     * @param deviceID 设备id
     * @return socket
     */
    public static DeviceWebSocket getDeviceWebSocketByDeviceID(Long deviceID)
    {
        for (DeviceWebSocket deviceWebSocket : deviceWebSocketSet)
            if (Objects.equals(deviceWebSocket.deviceID, deviceID))
                return deviceWebSocket;
        return null;
    }


    /**
     * 消息处理
     *
     * @param message    接收到的消息
     * @param messageLen 消息长度
     */
    private void messageHandle(byte[] message, int messageLen)
    {
        boolean isSetExceptMessage = false;
        //如果期待存在而且收到的信息类型与期待不符
        if ((message[0] != this.exceptMessage) && (this.exceptMessage != 0))
        {
            log.info(this.deviceID + '-' + this.deviceName + ":收到了未期待的信息");
            sendFailResponse();
            return;
        }
        //重设期待类型
        switch (message[0])
        {
            case MessageType.DEVICE_SUCCESS:
            {
                break;
            }

            case MessageType.DEVICE_FAIL:
            {
                break;
            }

            case MessageType.DEVICE_SEND_DEVICE_DATA:
            {
                getDeviceInfoHandle(message, messageLen);
                Device deviceToSql = new Device();
                //只在此处注入数据库
                if (this.deviceID == 0)
                {
                    this.deviceID = newID();
                    this.deviceName = "新设备";
                    sendDeviceData(this.deviceID, this.deviceName);

                    deviceToSql.setId(this.deviceID);
                    deviceToSql.setName(this.deviceName);
                    deviceToSql.setStatus(CommonEnum.DEVICE_STATUS_ONLINE);
                    deviceToSql.setIp(this.deviceIP);
                    deviceToSql.setCreateTime(LocalDateTime.now());
                    deviceService.save(deviceToSql);

                    isSetExceptMessage = true;
                    exceptMessage = MessageType.DEVICE_SUCCESS;
                } else if (!isDeviceIdInDataBase(this.deviceID))
                {

                    deviceToSql.setId(this.deviceID);
                    deviceToSql.setName(this.deviceName);
                    deviceToSql.setStatus(CommonEnum.DEVICE_STATUS_ONLINE);
                    deviceToSql.setIp(this.deviceIP);
                    deviceService.save(deviceToSql);

                    sendSuccessResponse();
                } else if (getDeviceWebSocketByDeviceID(this.deviceID) == null)
                {

                    deviceToSql.setId(this.deviceID);
                    deviceToSql.setStatus(CommonEnum.DEVICE_STATUS_ONLINE);
                    deviceToSql.setIp(this.deviceIP);
                    deviceService.updateById(deviceToSql);

                    sendSuccessResponse();
                } else
                {
                    this.deviceID = newID();
                    this.deviceName = "重设的设备";
                    sendDeviceData(this.deviceID, this.deviceName);

                    deviceToSql.setId(this.deviceID);
                    deviceToSql.setName(this.deviceName);
                    deviceToSql.setStatus(CommonEnum.DEVICE_STATUS_ONLINE);
                    deviceToSql.setIp(this.deviceIP);
                    deviceService.save(deviceToSql);

                    isSetExceptMessage = true;
                    exceptMessage = MessageType.DEVICE_SUCCESS;
                }

                deviceWebSocketSet.add(this);
                break;
            }

            case MessageType.DEVICE_SEND_OPEN_REQUEST:
            {
                openRequestHandle(message);
                if (isCardIdPermitted(this.receiveCardID))
                {
                    sendOpenRequest();
                    isSetExceptMessage = true;
                    exceptMessage = MessageType.DEVICE_SUCCESS;
                } else
                    sendFailResponse();
                break;
            }

            default:
            {
                log.info(this.deviceID + '-' + this.deviceName + ":收到未知指令");
            }
        }
        if (!isSetExceptMessage)
            exceptMessage = 0;
    }

    /**
     * 广播信息
     *
     * @param message 信息（字符串）
     */
    private void sendMessage(String message)
    {
        for (DeviceWebSocket deviceWebSocket : deviceWebSocketSet)
        {
            log.info("广播消息：{}", message);
            try
            {
                deviceWebSocket.session.getBasicRemote().sendText(message);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送数据
     *
     * @param data    数据存储地址
     * @param dataLen 数据长度
     */
    private void sendData(byte[] data, int dataLen)
    {
        try
        {
            this.session.getBasicRemote().sendBinary(ByteBuffer.wrap(data, 0, dataLen));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 发送成功响应 0x81
     */
    private void sendSuccessResponse()
    {
        byte[] data = {MessageType.SERVER_SUCCESS};
        sendData(data, 1);
    }

    /**
     * 发送失败响应 0x82
     */
    private void sendFailResponse()
    {
        byte[] data = {MessageType.SERVER_FAIL};
        sendData(data, 1);
    }

    /**
     * 发送重设设备信息 0x83
     *
     * @param deviceID   设备id
     * @param deviceName 设备名
     */
    public void sendDeviceData(Long deviceID, String deviceName)
    {
        byte[] dataToSend = new byte[100];
        dataToSend[0] = MessageType.SERVER_SET_DEVICE_DATA;
        for (int i = 0; i < 8; i++)
            dataToSend[8 - i] = (byte) (deviceID >> (i * 8));
        byte[] deviceNameArray = deviceName.getBytes();
        int deviceNameArrayLen = deviceNameArray.length;
        System.arraycopy(deviceNameArray, 0, dataToSend, 9, deviceNameArrayLen);
        sendData(dataToSend, deviceNameArrayLen + 9);
        //修改设备数据后总是要获取成功码的
        this.exceptMessage = MessageType.DEVICE_SUCCESS;
    }

    /**
     * 发送开门请求 0x84
     */
    public void sendOpenRequest()
    {
        byte[] data = {MessageType.SERVER_SEND_OPEN_REQUEST};
        sendData(data, 1);
    }

    /**
     * 发送蜂鸣请求 0x85
     */
    public void sendBeepRequest()
    {
        byte[] data = {MessageType.SERVER_SEND_BEEP_REQUEST};
        sendData(data, 1);
    }

    /**
     * 获取设备信息 处理0x03
     */
    private void getDeviceInfoHandle(byte[] data, int dataLen)
    {
        this.deviceID = turnArrayToLong(data, 1);
        byte[] stringData = Arrays.copyOfRange(data, 9, dataLen);
        this.deviceName = new String(stringData);
    }

    /**
     * 获取请求开门信息中的卡片id信息 处理0x04
     *
     * @param data 获取的信息
     */
    private void openRequestHandle(byte[] data)
    {
        this.receiveCardID = turnArrayToLong(data, 1);
    }

    /**
     * 将一个数组中的分散数据转换为Long
     *
     * @param data     数据数组
     * @param position 数据开始的位置，一般为1
     * @return 转换完成的结果
     */
    private Long turnArrayToLong(byte[] data, int position)
    {
        long dataLong = 0;
        for (int i = 0; i < 8; i++)
            dataLong = (dataLong << 8) + Byte.toUnsignedLong(data[i + position]);
        return dataLong;
    }

    /**
     * 判断输入的cardID是否可以开门
     *
     * @param cardID 请求的CardID
     * @return 是或否
     */
    private boolean isCardIdPermitted(Long cardID)
    {
        User userByCardID = userService.getUserByCardID(cardID);
        if (userByCardID == null)
            return false;
        return (deviceService.judgeUserAndDevice(userByCardID.getId(), this.deviceID));
    }


    /**
     * 生成一个新的ID
     *
     * @return 通过雪花算法生成的ID
     */
    private Long newID()
    {
        Sequence sequence = new Sequence();
        return sequence.nextId();
    }

    /**
     * 判断数据库中是否有该设备信息
     *
     * @param deviceID 设备的ID
     * @return 是否有信息
     */
    private boolean isDeviceIdInDataBase(Long deviceID)
    {
        return (!(deviceService.getById(deviceID) == null));
    }
}

class MessageType
{
    public static final byte DEVICE_SUCCESS = 0X01;
    public static final byte DEVICE_FAIL = 0X02;
    public static final byte DEVICE_SEND_DEVICE_DATA = 0X03;
    public static final byte DEVICE_SEND_OPEN_REQUEST = 0X04;

    public static final byte SERVER_SUCCESS = (byte) 0x81;
    public static final byte SERVER_FAIL = (byte) 0x82;
    public static final byte SERVER_SET_DEVICE_DATA = (byte) 0x83;
    public static final byte SERVER_SEND_OPEN_REQUEST = (byte) 0x84;
    public static final byte SERVER_SEND_BEEP_REQUEST = (byte) 0x85;
}