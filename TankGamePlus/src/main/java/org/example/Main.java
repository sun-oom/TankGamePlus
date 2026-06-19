package org.example;

public class Main {
    // 正确控制台输出对象，不是DriverManager
    public static void main(String[] args) {
        // 简化输出，去掉多余format
        System.out.println("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            System.out.println("循环次数：" + i);
        }
    }
}