//
//  Arguments.java
//  The tool for DiME (Data Identity Message Envelope)
//  A powerful universal data format that is built for secure, and integrity protected communication between trusted
//  entities in a network.
//
//  Released under the MIT licence, see LICENSE for more information.
//  Copyright (c) 2022 Shift Everywhere AB. All rights reserved.
//
package io.dimeformat.tool;

import io.dimeformat.Dime;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

public class Arguments {

    /// Constructors ///
    public Arguments(String[] args) {
        parseArguments(args);
    }

    /// Operation ///
    public String getOperationName() { return this.operationName; }

    /// Option methods ///
    public int size() {
        return this.map != null ? this.map.size() : 0;
    }

    public boolean hasOption(String option) {
        return this.map != null && this.map.containsKey(option);
    }

    public String get(String option) {
        return getValue(option);
    }

    public String[] getArray(String option) {
        String strings = get(option);
        return strings != null ? strings.split(" ") : null;
    }

    public Instant getInstant(String option) {
        String time = getValue(option);
        return time != null && time.length() > 0 ? Instant.parse(time) : null;
    }

    public UUID getUUID(String option) {
        String uuid = get(option);
        return uuid != null && uuid.length() > 0 ? UUID.fromString(uuid) : null;
    }

    public long getLong(String option, long defaultValue) {
        String number = getValue(option);
        return number != null && number.length() > 0 ? Long.parseLong(number) : defaultValue;
    }

    public long getValidFor(String option, long baseSeconds) {
        long number = getLong(option, Dime.NO_EXPIRATION);
        if (number != Dime.NO_EXPIRATION) {
            number = number * baseSeconds;
        }
        return number;
    }

    ///// PRIVATE /////

    private static final String OPTION_PREFIX = "--";
    private String operationName;
    private HashMap<String, String> map;


    private String getValue(String option) {
        String value = this.map.get(option);
        if (!option.equals(Option.OUT)) {
            if (value != null && value.length() > 0) {
                File file = new File(value);
                if (file.exists() && !file.isDirectory()) {
                    try {
                        value = Utility.readFile(file);
                    } catch (IOException e) {
                        /* ignore and just return what is in value */
                    }
                }
            }
        }
        return value;
    }

    private void parseArguments(String[] args) {
        if (args == null || args.length == 0) { return; }
        this.operationName = args[0];
        this.map = new HashMap<>();
        if (args.length > 1) {
            for (int index = 1; index < args.length; ) {

                String option = args[index];
                if (!option.startsWith(Arguments.OPTION_PREFIX)) {
                    throw new IllegalArgumentException("Invalid option encountered: " + option);
                }
                int valueIndex = index + 1;
                StringBuilder values = new StringBuilder();
                while (valueIndex < args.length && !args[valueIndex].startsWith(Arguments.OPTION_PREFIX)) {
                    if (values.length() != 0) {
                        values.append(" ");
                    }
                    values.append(args[valueIndex]);
                    valueIndex++;
                }
                map.put(option, values.toString());
                index = valueIndex;
            }
        }
    }

}
