//
//  IssueOperation.java
//  The tool for DiME (Data Identity Message Envelope)
//  A powerful universal data format that is built for secure, and integrity protected communication between trusted
//  entities in a network.
//
//  Released under the MIT licence, see LICENSE for more information.
//  Copyright (c) 2022 Shift Everywhere AB. All rights reserved.
//
package io.dimeformat.tool;

import io.dimeformat.*;
import io.dimeformat.enums.IdentityCapability;
import java.util.*;

public class IssueOperation extends Operation {

    public static final String SELF_NAME = "self";
    public static final String ISSUE_NAME = "issue";

    @Override
    public String getDescription() {
        switch (getName()) {
            case IssueOperation.ISSUE_NAME: return "Issue a new identity from an identity issuing request (IIR).";
            case IssueOperation.SELF_NAME: return "Issue a new self-issued identity from a key. Key provided should have 'sign' capability.";
            default: return null;
        }
    }

    @Override
    public List<Option> getOptions() {
        ArrayList<Option> array = new ArrayList<>();
        switch (getName()) {
            case IssueOperation.ISSUE_NAME:
                Collections.addAll(array,
                        new Option(Option.ALLOW_CAP, false, "List of capabilities that are allowed to be requested in the IIR, leave out for no limitation.", List.of("generic\t\t(default capability, generic use)", "identify\t(capability to identify, authenticated use)", "issue\t\t(capability to issue additional identities)")),
                        new Option(Option.AMB, false, "An ambit list that should be set in the issued identity."),
                        new Option(Option.DAYS, false, "Number of days issued identity should be valid, leave out for no expiration date."),
                        new Option(Option.EXCLUDE_CHN, false, "Will skip including the trust chain in the issued identity."),
                        new Option(Option.IIR, true, "Dime encoded IIR to use for identity issuing."),
                        new Option(Option.ISSUER, true, "Dime encoded issuer identity."),
                        new Option(Option.KEY, true, "Dime encoded key to use when issuing the identity, this will be used to sign the identity."),
                        new Option(Option.MTD, false, "A method list that should be set in the issued identity."),
                        new Option(Option.REQUIRE_CAP, false, "List of capabilities that are must be requested in the IIR, leave out for no limitation.", List.of("generic\t\t(default capability, generic use)", "identify\t(capability to identify, authenticated use)", "issue\t\t(capability to issue additional identities)")),
                        new Option(Option.SUB, false, "The subject id (UUID) for the issued identity, a random UUID will be generated if omitted."),
                        new Option(Option.SYS, false, "The system name that should be set in the issued identity, leave out to inherent system from issuer."));
                break;
            case IssueOperation.SELF_NAME:
                Collections.addAll(array,
                        new Option(Option.CAP, false, "List of capabilities that should be set in the issued identity.", List.of("generic\t\t(default capability, generic use)", "identify\t(capability to identify, authenticated use)", "issue\t\t(capability to issue additional identities)", "self\t\t(indicates self-issuing, enforced")),
                        new Option(Option.AMB, false, "An ambit list that should be set in the issued identity."),
                        new Option(Option.DAYS, false, "Number of days issued identity should be valid, leave out for no expiration date."),
                        new Option(Option.KEY, true, "Dime encoded key to use when issuing the identity, this will be used to sign the identity and public key will be included in the issued identity."),
                        new Option(Option.MTD, false, "A method list that should be set in the issued identity."),
                        new Option(Option.SUB, false, "The subject id (UUID) for the issued identity, a random UUID will be generated if omitted."),
                        new Option(Option.SYS, false, "The system name that should be set in the issued identity, leave out to inherent system from issuer."));
                break;
            default:
                return null;
        }
        return array;
    }

    @Override
    public String execute() throws Exception {
        UUID subjectId = arguments.getUUID(Option.SUB);
        if (subjectId == null) {
            subjectId = UUID.randomUUID();
        }
        long validFor = arguments.getValidFor(Option.DAYS, Dime.VALID_FOR_1_DAY);
        String encodedKey = arguments.get(Option.KEY);
        if (encodedKey == null) {
            DimeTool.showErrorMessage(this, "Missing required option for key.", true);
            return null;
        }
        Key issuerKey = Item.importFromEncoded(encodedKey);
        String systemName = arguments.get(Option.SYS);
        if (systemName == null ||systemName.length() == 0) {
            DimeTool.showErrorMessage(this, "Missing required option for system name.", true);
            return null;
        }
        String[] ambit = arguments.getArray(Option.AMB);
        String[] methods = arguments.getArray(Option.MTD);
        Identity identity;
        if (arguments.getOperationName().equals(IssueOperation.SELF_NAME)) { // This is a self-issue
            IirOperation iirOperation = new IirOperation();
            iirOperation.arguments = arguments;
            IdentityIssuingRequest iir = Item.importFromEncoded(iirOperation.execute());
            identity = iir.selfIssueIdentity(subjectId, validFor, issuerKey, systemName, ambit, methods);
            if (arguments.hasOption(Option.LEGACY) && !identity.isLegacy()) {
                identity.convertToLegacy();
                identity.sign(identity, issuerKey, false);
            }
        } else { // This is a hierarchical issue
            boolean includeChain = !arguments.hasOption(Option.EXCLUDE_CHN);
            String[] allArray = arguments.getArray(Option.ALLOW_CAP);
            IdentityCapability[] allCaps = null;
            if (allArray != null && allArray.length > 0) {
                allCaps = Arrays.stream(allArray).map(cap -> IdentityCapability.valueOf(cap.toUpperCase())).toArray(IdentityCapability[]::new);
            }
            String[] reqArray = arguments.getArray(Option.REQUIRE_CAP);
            IdentityCapability[] reqCaps = null;
            if (reqArray != null && reqArray.length > 0) {
                reqCaps = Arrays.stream(reqArray).map(cap -> IdentityCapability.valueOf(cap.toUpperCase())).toArray(IdentityCapability[]::new);
            }
            String encodedIssuer = arguments.get(Option.ISSUER);
            if (encodedIssuer == null) {
                DimeTool.showErrorMessage(this, "Missing required option for issuer.", true);
                return null;
            }
            Identity issuerIdentity = Item.importFromEncoded(encodedIssuer);
            Dime.keyRing.put(issuerIdentity);
            String encodedIir = arguments.get(Option.IIR);
            if (encodedIir == null) {
                DimeTool.showErrorMessage(this, "Missing required option for iir.", true);
                return null;
            }
            IdentityIssuingRequest iir = Item.importFromEncoded(encodedIir);
            if (allCaps == null && reqCaps == null) {
                allCaps = iir.getCapabilities().toArray(new IdentityCapability[0]);
            }
            identity = iir.issueIdentity(subjectId, validFor, issuerKey, issuerIdentity, includeChain, allCaps, reqCaps, systemName, ambit, methods);
        }

        return identity.exportToEncoded();
    }
}
