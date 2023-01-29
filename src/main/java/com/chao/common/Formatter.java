package com.chao.common;

import org.apache.commons.lang.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Formatter
{
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(JacksonObjectMapper.DEFAULT_DATE_TIME_FORMAT);

    public static LocalDateTime stringToLocalDataTime(String dateTime)
    {
        if (StringUtils.isEmpty(dateTime))
            return null;

        return LocalDateTime.parse(dateTime, dateTimeFormatter);
    }
}
