//
//  IirOperation.java
//  The tool for DiME (Data Identity Message Envelope)
//  A powerful universal data format that is built for secure, and integrity protected communication between trusted
//  entities in a network.
//
//  Released under the MIT licence, see LICENSE for more information.
//  Copyright (c) 2022 Shift Everywhere AB. All rights reserved.
//
package io.dimeformat.tool;

import io.dimeformat.IdentityIssuingRequest;
import io.dimeformat.Item;
import io.dimeformat.Key;
import io.dimeformat.enums.IdentityCapability;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IirOperation extends Operation {

    public static final String NAME = "iir";

    @Override
    public String getDescription() {
        return "Generate a new identity issuing request (IIR) from a key. Key provided should have 'sign' capability.";
    }

    @Override
    public List<Option> getOptions() {
        ArrayList<Option> array = new ArrayList<>();
        Collections.addAll(array,
                new Option(Option.KEY, true, "Dime encoded key to use when generating the IIR."),
                new Option(Option.CAP,
                        false,
                        "List of capabilities that should be request for in the IIR.",
                        List.of("generic\t\t(default capability, generic use)", "identify\t(capability to identify, authenticated use)", "issue\t\t(capability to issue additional identities)")));
        return array;
    }

    @Override
    public String execute() throws Exception {
        String encodedKey = arguments.get(Option.KEY);
        if (encodedKey == null) {
            DimeTool.showErrorMessage(this, "Missing required key.", true);
            return null;
        }
        Key key = Item.importFromEncoded(encodedKey);
        String[] array = arguments.getArray(Option.CAP);
        IdentityCapability[] caps = null;
        if (array != null && array.length > 0) {
            caps = Arrays.stream(array).map(cap -> IdentityCapability.valueOf(cap.toUpperCase())).toArray(IdentityCapability[]::new);
        }
        IdentityIssuingRequest iir = IdentityIssuingRequest.generateIIR(key, caps);
        if (arguments.hasOption(Option.LEGACY) && !iir.isLegacy()) {
            iir.convertToLegacy();
            iir.sign(key);
        }
        return iir.exportToEncoded();
    }

}
