//
//  KeyOperation.java
//  The tool for DiME (Data Identity Message Envelope)
//  A powerful universal data format that is built for secure, and integrity protected communication between trusted
//  entities in a network.
//
//  Released under the MIT licence, see LICENSE for more information.
//  Copyright (c) 2022 Shift Everywhere AB. All rights reserved.
//
package io.dimeformat.tool;

import io.dimeformat.Dime;
import io.dimeformat.Key;
import io.dimeformat.enums.KeyCapability;
import java.util.*;
import static java.util.stream.Collectors.toList;

public class KeyOperation extends Operation {

    public static final String NAME = "key";

    @Override
    public String getDescription() {
        return "Generates a new key with a specified capability.";
    }

    @Override
    public List<Option> getOptions() {
        ArrayList<Option> array = new ArrayList<>();
        Collections.addAll(array,
                new Option(Option.CAP, true, "The capability of the generated key, space separated list.", List.of("encrypt\t\t(Encryption capability)", "exchange\t(Key exchange/agreement capability)", "sign\t\t(Digital signature capability)")),
                new Option(Option.CRYPTO_SUITE, false, "Name of the cryptographic suite to use, omit to use default suite."),
                new Option(Option.CTX, false, "The context to set in the generated key."),
                new Option(Option.DAYS, false, "Number of days generated key should be valid, leave out for no expiration date."),
                new Option(Option.ISS, false, "The identifier (UUID) of the issuer of the key."));
        return array;
    }

    @Override
    public String execute() throws Exception {
        String capArray = arguments.get(Option.CAP);
        if (capArray == null || capArray.length() == 0) { return null; }
        List<KeyCapability> capList = Arrays.stream(capArray.split(" ")).map(use -> KeyCapability.valueOf(use.toUpperCase())).collect(toList());
        long validFor = arguments.getValidFor(Option.DAYS, Dime.VALID_FOR_1_DAY);
        UUID issuerId = arguments.getUUID(Option.ISS);
        String context = arguments.get(Option.CTX);
        String suite = arguments.get(Option.CRYPTO_SUITE) == null || arguments.get(Option.CRYPTO_SUITE).length() == 0 ? Dime.crypto.getDefaultSuiteName() : arguments.get(Option.CRYPTO_SUITE);
        Key key = Key.generateKey(capList, validFor, issuerId, context, suite);
        if (arguments.hasOption(Option.LEGACY)) {
            key.convertToLegacy();
        }
        return key.exportToEncoded();
    }

}
