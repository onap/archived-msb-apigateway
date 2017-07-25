package org.onap.msb.apiroute.wrapper.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk7.Jdk7Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class Jackson {
    //use static singleton, make sure to reuse!
    public static final ObjectMapper MAPPER = newObjectMapper();

    private Jackson() {
        /* singleton */
    }

    private static ObjectMapper newObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        return configure(mapper);
    }

    private static ObjectMapper configure(ObjectMapper mapper) {
        mapper.registerModule(new GuavaModule());
        mapper.registerModule(new JodaModule());
        mapper.registerModule(new Jdk7Module());

        return mapper;
    }
}
