package ru.netology;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {

        var server = new Server(9999, 64);
        server.start();

    }
}


