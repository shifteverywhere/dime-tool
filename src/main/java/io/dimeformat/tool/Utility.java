//
//  Utility.java
//  The tool for DiME (Data Identity Message Envelope)
//  A powerful universal data format that is built for secure, and integrity protected communication between trusted
//  entities in a network.
//
//  Released under the MIT licence, see LICENSE for more information.
//  Copyright (c) 2022 Shift Everywhere AB. All rights reserved.
//
package io.dimeformat.tool;

import java.io.*;

public class Utility {

    public static String readFile(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder builder = new StringBuilder();
        String str;
        while ((str = br.readLine()) != null) {
            builder.append(str);
        }
        return builder.toString();
    }

    public static void outputFile(String encoded, String filename) throws IOException {
        BufferedWriter bwr = new BufferedWriter(new FileWriter(filename));
        bwr.write(encoded);
        bwr.flush();
        bwr.close();
    }

    public static void outputScreen(String encoded) {
        System.out.println(encoded);
    }

}
