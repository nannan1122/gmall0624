package com.atguigu.gmall0624.manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;
@ComponentScan(basePackages = "com.atguigu.gmall0624")
@EnableTransactionManagement
@SpringBootApplication
@MapperScan(basePackages = "com.atguigu.gmall0624.manage.mapper")
public class GmallManageServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallManageServiceApplication.class, args);
	}

}
