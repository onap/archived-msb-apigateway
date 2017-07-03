package org.onap.msb.apiroute.wrapper.consulextend.async;

import java.math.BigInteger;

public class ConsulResponseHeader {
    private final long lastContact;
    private final boolean knownLeader;
    private final BigInteger index;
    
    public ConsulResponseHeader(long lastContact, boolean knownLeader, BigInteger index) {
        this.lastContact = lastContact;
        this.knownLeader = knownLeader;
        this.index = index;
    }

    public long getLastContact() {
        return lastContact;
    }

    public boolean isKnownLeader() {
        return knownLeader;
    }

    public BigInteger getIndex() {
        return index;
    }
}
