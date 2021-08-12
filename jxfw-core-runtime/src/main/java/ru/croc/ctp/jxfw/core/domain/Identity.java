package ru.croc.ctp.jxfw.core.domain;

import java.io.Serializable;

/**
 * 
 * @author AKogun
 *
 * @param <ID>
 */
public interface Identity<ID extends Serializable> {
    
    ID getId();
    
    String getTypeName();

    Identity<String> UNKNOWN = new Identity<String>() {
        @Override
        public String getId() {
            return "Unknown";
        }

        @Override
        public String getTypeName() {
            return "Unknown";
        }
    };
}
