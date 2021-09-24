package com.itheima.restkeeper.config;

import com.itheima.restkeeper.sink.LogSink;
import com.itheima.restkeeper.source.LogSource;
import org.springframework.cloud.stream.annotation.EnableBinding;

/**
 * @ClassName LogBinding.java
 * @Description 日志Binding支持LogSink接收
 */
@EnableBinding(LogSink.class)
public class LogSinkBinding {
}
