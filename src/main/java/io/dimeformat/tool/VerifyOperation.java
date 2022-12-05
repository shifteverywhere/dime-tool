//
//  VerifyOperation.java
//  The tool for DiME (Data Identity Message Envelope)
//  A powerful universal data format that is built for secure, and integrity protected communication between trusted
//  entities in a network.
//
//  Released under the MIT licence, see LICENSE for more information.
//  Copyright (c) 2022 Shift Everywhere AB. All rights reserved.
//
package io.dimeformat.tool;

import io.dimeformat.Dime;
import io.dimeformat.Identity;
import io.dimeformat.Item;
import io.dimeformat.Key;
import io.dimeformat.keyring.IntegrityState;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VerifyOperation extends Operation {

    public static final String NAME = "verify";

    @Override
    public String getDescription() {
        return "Verify integrity and trust of a Dime item.";
    }

    @Override
    public List<Option> getOptions() {
        ArrayList<Option> array = new ArrayList<>();
        Collections.addAll(array,
                new Option(Option.ITEM, true, "Dime encoded item or envelope that should be verified."),
                new Option(Option.GRACE_PERIOD, false, "Specifies a grace period in seconds that should be used when verifying dates."),
                new Option(Option.SET_DATE, false, "Overrides the current system time and uses the provided date for verification, should be provided in RFC 3339 format."),
                new Option(Option.VERIFIER, true, "Dime encoded key or identity that should or envelope that should be verified."));
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
        String encodedVerifier = arguments.get(Option.VERIFIER);
        if (encodedVerifier == null || encodedVerifier.length() == 0) {
            DimeTool.showErrorMessage(this, "Missing required option: " + Option.VERIFIER + ".", true);
            return null;
        }
        Instant overrideTime = arguments.getInstant(Option.SET_DATE);
        if (overrideTime != null) {
            Dime.setOverrideTime(overrideTime);
        }
        long gracePeriod = arguments.getLong(Option.GRACE_PERIOD, 0L);
        if (gracePeriod != 0L) {
            Dime.setGracePeriod(gracePeriod);
        }
        IntegrityState state;
        Item verifier = Item.importFromEncoded(encodedVerifier);
        Key verifyingKey;
        if (verifier instanceof Key) {
            state = item.verify((Key) verifier);
        } else if (verifier instanceof Identity) {
            state = item.verify((Identity) verifier);
        } else {
            DimeTool.showErrorMessage(this, "Unsupported Dime item: " + Option.VERIFIER + ".", true);
            return null;
        }
        return state.toString();
    }

}
