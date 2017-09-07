/*******************************************************************************
 * Copyright 2016-2017 ZTE, Inc. and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
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
