//
//  CreateOperation.java
//  The tool for DiME (Data Identity Message Envelope)
//  A powerful universal data format that is built for secure, and integrity protected communication between trusted
//  entities in a network.
//
//  Released under the MIT licence, see LICENSE for more information.
//  Copyright (c) 2022 Shift Everywhere AB. All rights reserved.
//
package io.dimeformat.tool;

import io.dimeformat.Data;
import io.dimeformat.Item;
import io.dimeformat.enums.Claim;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CreateOperation extends Operation {

    public static final String NAME = "create";

    @Override
    public String getDescription() {
        return "Creates a Dime item from a type (only 'data' type supported).";
    }

    @Override
    public List<Option> getOptions() {
        ArrayList<Option> array = new ArrayList<>();
        Collections.addAll(array,
                new Option(Option.CTX, false, "The context to set in the generated item."),
                new Option(Option.ISS, false, "The identifier (UUID) of the issuer of the item."),
                new Option(Option.TYPE,
                        true,
                        "Specifies the type of Dime item to create.",
                        List.of("data")));
        return array;
    }

    @Override
    public String execute() throws Exception {
        String type = arguments.get(Option.TYPE);
        if (type == null || type.length() == 0) {
            DimeTool.showErrorMessage(this, "Missing required option: " + Option.TYPE + ".", true);
            return null;
        }
        UUID issuerId = arguments.getUUID(Option.ISS);
        Item item = create(type, issuerId);
        if (item == null) {
            DimeTool.showErrorMessage(this, "Invalid/unsupported Dime type: " + type + ".", true);
            return null;
        }
        String context = arguments.get(Option.CTX);
        if (context != null && context.length() > 0) {
            item.putClaim(Claim.CTX, context);
        }
        if (item instanceof Data) {
            Data data = populateData((Data) item);
            if (data != null) {
                return data.exportToEncoded();
            }
        }
        return null;
    }

    private Item create(String type, UUID issuerId) {
        switch (type.toLowerCase()) {
            case "data":
                return new Data(issuerId);
            default:
                return null;
        }
    }

    private Data populateData(Data data) {
        String payload = arguments.get(Option.PAYLOAD);
        if (payload == null || payload.length() == 0) {
            DimeTool.showErrorMessage(this, "Missing required option: " + Option.PAYLOAD + ".", true);
            return null;
        }
        String mimeType = arguments.get(Option.MIM);
        data.setPayload(payload.getBytes(StandardCharsets.UTF_8), mimeType);
        return data;
    }


}
