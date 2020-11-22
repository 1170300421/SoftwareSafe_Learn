package com.company;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        String filename="check.txt";
        DoubleAC doubleAC=new DoubleAC();
        doubleAC.Pattern();
        doubleAC.Arrays();
        doubleAC.print();
        doubleAC.Output();
        doubleAC.getText(filename);
    }

}