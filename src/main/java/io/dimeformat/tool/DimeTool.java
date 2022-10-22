//
//  DimeTool.java
//  The tool for DiME (Data Identity Message Envelope)
//  A powerful universal data format that is built for secure, and integrity protected communication between trusted
//  entities in a network.
//
//  Released under the MIT licence, see LICENSE for more information.
//  Copyright (c) 2022 Shift Everywhere AB. All rights reserved.
//
package io.dimeformat.tool;

import java.io.*;
import java.util.List;

public class DimeTool {

    ///// PUBLIC /////

    public static final String TOOL_NAME = "DimeTool";
    public static final String TOOL_VERSION = "0.5";

    public static final String DIME_VERSION = "1.2.1";

    /// Contractors ///

    public DimeTool(String[] args) {
        this.arguments = new Arguments(args);
    }

    /// Execute ///

    public int execute() throws Exception {
        String operationName = arguments.getOperationName();
        if (operationName == null) {
            showErrorMessage(null, "Missing required operation.", true);
            return -1;
        } else if (operationName.equals(Option.VERSION)) {
            System.out.println(DimeTool.TOOL_NAME + " v" + DimeTool.TOOL_VERSION + " (io.dimeformat:dime-java-ref:" + DimeTool.DIME_VERSION + ")");
        } else if (operationName.equals(Option.HELP)) {
            showHelp(null, System.out);
        } else if (operationName.equals(Option.SUITES)) {
            System.out.println("\n\tSuite\t\tDescription");
            System.out.println("\t-----\t\t-----------");
            System.out.println("\tSTN\t\t\tDiME Standard Cryptographic Suite");
            System.out.println("\t\t\t\t\tEd25519\t\t\t\t(Digital signatures)");
            System.out.println("\t\t\t\t\tX25519\t\t\t\t(Key agreement)");
            System.out.println("\t\t\t\t\tSalsa20-Poly1305\t(Secret key encryption)");
            System.out.println("\t\t\t\t\tBlake2\t\t\t\t(Cryptographic hash)");
        } else {
            Operation operation = Operation.fetchOperation(arguments);
            if (operation == null) {
                showErrorMessage(null, "Unsupported operation: '" + operationName + "'.", true);
                return -1;
            } else  if (arguments.size() == 0) {
                showErrorMessage(operation, "Missing required options.", true);
                return -1;
            } else {
                if (operation.showHelp()) {
                    DimeTool.showHelp(operation, System.out);
                } else {
                    String output = operation.execute();
                    if (output != null) {
                        if (arguments.hasOption(Option.OUT)) {
                            Utility.outputFile(output, arguments.get(Option.OUT));
                        } else {
                            Utility.outputScreen(output);
                        }
                    }
                }
            }
        }
        return 0;
    }

    /// HELP ///

    public static int showHelp(Operation operation, PrintStream stream) {
        stream.println("Usage: dimetool " + (operation == null ? "OPERATION" : operation.getName()) + " [options] | [--help" + (operation == null ? " | --version" : "")  + "]");
        if (operation == null) {
            List<Operation> operations = Operation.allOperations();
            if (operations != null) {
                stream.println("\n\tOperation\t\tDescription");
                stream.println("\t---------\t\t-----------");
                for (Operation op: operations) {
                    String tabs = op.getName().length() < 4 ? "\t\t\t\t" : "\t\t\t";
                    stream.println("\t" + op.getName() + tabs + op.getDescription());
                }
                stream.println("\n\tUse --help on an operation for specific help on operation usage.");
                stream.print("\n\t" + Option.HELP + "\t\t\tShows this help message and exits.");
                stream.print("\n\t" + Option.VERSION + "\t\tShows version information.\n");
            } else {
                DimeTool.showErrorMessage(null, "Unexpected internal errors, unable to continue.", false);
            }
        } else {
            List<Option> options = operation.getOptions();
            stream.println("\n" + operation.getName() + ": " + operation.getDescription());
            stream.println("\n\tOption\t\t\tDescription");
            stream.println("\t------\t\t\t-----------");
            for (Option option: options) {
                String tabs = option.name.length() < 8 ? "\t\t\t" : option.name.length() < 12 ? "\t\t" : "\t";
                stream.print("\t" + option.name + tabs + option.description);
                stream.print(option.required ? " [REQUIRED]" : " [OPTIONAL]");
                stream.print("\n");
                if (option.values != null) {
                    stream.println("\t\t\t\t\tAccepted values:");
                    for (String value: option.values) {
                        stream.println("\t\t\t\t\t\t" + value);
                    }
                }
            }
            stream.println("\n\tOptions may either specify the value directly, or specify a filename where the value will be read from.");
            stream.print("\n\t" + Option.HELP + "\t\t\t\tShows this help message and exits.\n");
        }
        stream.flush();
        return 0;
    }

    public static void showErrorMessage(Operation operation, String message, boolean showHelp) {
        System.err.println("[ERROR] " + message);
        if (showHelp) {
            System.err.println("");
            showHelp(operation, System.err);
        }
    }

    private final Arguments arguments;

    ///// MAIN /////

    public static void registerOperations() {
        Operation.registerOperation("key", KeyOperation.class);
        Operation.registerOperation("iir", IirOperation.class);
        Operation.registerOperation("self", IssueOperation.class);
        Operation.registerOperation("issue", IssueOperation.class);
        Operation.registerOperation("verify", VerifyOperation.class);
    }

    public static void main(String[] args) {
        DimeTool.registerOperations();
        DimeTool tool = null;
        try {
            tool = new DimeTool(args);
            int status = tool.execute();
            System.exit(status);
        } catch (Exception e) {
            Operation operation = tool != null ? Operation.fetchOperation(tool.arguments) : null;
            DimeTool.showErrorMessage(operation, e.getMessage(), true);
            System.exit(-1);
        }
    }

}
