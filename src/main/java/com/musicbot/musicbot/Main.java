package com.musicbot.musicbot;

public class Main {
    public static void main(String[] args) {
        int time = 218;
        String str = String.format("%d:%02d", time / 60, time % 60);
        System.out.println(str);
    }
}
