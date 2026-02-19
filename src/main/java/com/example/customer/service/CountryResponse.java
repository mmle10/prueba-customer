package com.example.customer.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryResponse {
    
    public Name name;
    public Map<String, String> demonyms;
    
    public static class Name {
        public String common;
        public String official;
    }
    
    public static class Demonym {
        public String f;
        public String m;
    }
    
    public String getDemonym() {
        if (demonyms != null && !demonyms.isEmpty()) {
            // Try to get English demonym first, then fallback to any available
            return demonyms.getOrDefault("eng", demonyms.values().iterator().next());
        }
        return null;
    }
}
