//
//  Option.java
//  The tool for DiME (Data Identity Message Envelope)
//  A powerful universal data format that is built for secure, and integrity protected communication between trusted
//  entities in a network.
//
//  Released under the MIT licence, see LICENSE for more information.
//  Copyright (c) 2022 Shift Everywhere AB. All rights reserved.
//
package io.dimeformat.tool;

import java.util.List;

public class Option {

    public static final String ALLOW_CAP    = "--allowcap";
    public static final String AMB          = "--amb";
    public static final String CAP          = "--cap";
    public static final String CRYPTO_SUITE = "--suite";
    public static final String CTX          = "--ctx";
    public static final String DAYS         = "--days";
    public static final String EXCLUDE_CHN  = "--excludechain";
    public static final String GRACE_PERIOD = "--grace";
    public static final String HELP         = "--help";
    public static final String IIR          = "--iir";
    public static final String ISS          = "--iss";
    public static final String ISSUER       = "--issuer";
    public static final String ITEM         = "--item";
    public static final String KEY          = "--key";

    public static final String LEGACY       = "--legacy";
    public static final String MIM          = "--mim";
    public static final String MTD          = "--mtd";
    public static final String OUT          = "--out";
    public static final String PAYLOAD      = "--payload";
    public static final String REQUIRE_CAP  = "--requirecap";
    public static final String SET_DATE     = "--setdate";
    public static final String SUB          = "--sub";
    public static final String SUITES       = "--suites";
    public static final String SYS          = "--sys";
    public static final String TYPE         = "--type";
    public static final String VERIFIER     = "--verifier";
    public static final String VERSION      = "--version";

    public final String name;
    public final boolean required;
    public final String description;

    public final List<String> values;

    public Option(String name, boolean required, String description) {
        this(name, required, description, null);
    }

    public Option(String name, boolean required, String description, List<String> values) {
        this.name = name;
        this.required = required;
        this.description = description;
        this.values = values;
    }

}
