//
//  ThumbprintOperation.java
//  The tool for DiME (Data Identity Message Envelope)
//  A powerful universal data format that is built for secure, and integrity protected communication between trusted
//  entities in a network.
//
//  Released under the MIT licence, see LICENSE for more information.
//  Copyright (c) 2022 Shift Everywhere AB. All rights reserved.
//
package io.dimeformat.tool;

import io.dimeformat.Item;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ThumbprintOperation extends Operation {

    public static final String NAME = "thumbprint";

    @Override
    public String getDescription() {
        return "Generates a thumbprint of a Dime item.";
    }

    @Override
    public List<Option> getOptions() {
        ArrayList<Option> array = new ArrayList<>();
        Collections.addAll(array,
                new Option(Option.ITEM, true, "Dime encoded item or envelope to generate a thumbprint for."));
        return array;
    }

    @Override
    public String execute() throws Exception {
        String encodedItem = arguments.get(Option.ITEM);
        if (encodedItem == null || encodedItem.length() == 0) {
            DimeTool.showErrorMessage(this, "Missing required option: " + Option.ITEM + ".", true);
            return null;
        }
        Item item = Item.importFromEncoded(encodedItem);
        return item.generateThumbprint();
    }

}
