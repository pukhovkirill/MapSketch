package com.pukhovkirill.mapsketch;

import org.mybatis.spring.annotation.MapperScan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.pukhovkirill.mapsketch.repository")
public class MapSketchApplication {

    public static void main(String[] args) {
        SpringApplication.run(MapSketchApplication.class, args);
    }

}
