//
//  Operation.java
//  The tool for DiME (Data Identity Message Envelope)
//  A powerful universal data format that is built for secure, and integrity protected communication between trusted
//  entities in a network.
//
//  Released under the MIT licence, see LICENSE for more information.
//  Copyright (c) 2022 Shift Everywhere AB. All rights reserved.
//
package io.dimeformat.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public abstract class Operation {

    public static void registerOperation(String name, Class<? extends Operation> operationType) {
        if (Operation.operations == null) {
            Operation.operations = new HashMap<>();
        }
        Operation.operations.put(name, operationType);
    }

    public static List<Operation> allOperations() {
        if (operations != null) {
            List<Operation> list = new ArrayList<>();
            for (String name: operations.keySet()) {
                list.add(Operation.fetchOperation(name));
            }
            return list;
        }
        return null;
    }

    public static Operation fetchOperation(Arguments arguments) {
        Operation operation = Operation.fetchOperation(arguments.getOperationName());
        if (operation != null) {
            operation.arguments = arguments;
        }
        return operation;
    }

    private static Operation fetchOperation(String name) {
        Class<? extends Operation> operationType = Operation.operations.get(name);
        if (operationType != null) {
            try {
                Operation operation = Objects.requireNonNull(operationType).getDeclaredConstructor().newInstance();
                operation.setName(name);
                return operation;
            } catch (Exception e) {
                /* ignore this */
            }
        }
        return null;
    }

    public String getName() {
        return _name;
    };

    protected void setName(String name) {
        _name = name;
    }
    protected String _name = null;

    public abstract String getDescription();

    public abstract List<Option> getOptions();

    protected Arguments arguments;

    public abstract String execute() throws Exception;

    public boolean showHelp() {
        return this.arguments.hasOption(Option.HELP);
    }


    private static HashMap<String, Class<? extends Operation>> operations;

}
