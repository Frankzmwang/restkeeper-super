package com.itheima.restkeeper.log.binding;

import com.itheima.restkeeper.source.LogSource;
import org.springframework.cloud.stream.annotation.EnableBinding;

/**
 * @ClassName LogBinding.java
 * @Description 日志Binding支持LogSource发送
 */
@EnableBinding(LogSource.class)
public class LogSourceBinding {
}
