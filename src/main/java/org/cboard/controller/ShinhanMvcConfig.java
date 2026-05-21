package org.cboard.controller;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(NotLocalDevCondition.class)
@ComponentScan(basePackages = "com.shinhan.ctrl")
public class ShinhanMvcConfig {
}
