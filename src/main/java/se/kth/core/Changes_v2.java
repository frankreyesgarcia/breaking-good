package se.kth.core;

import se.kth.breaking_changes.ApiMetadata;

public record Changes_v2(ApiMetadata oldApiVersion, ApiMetadata newApiVersion, BrokenChange changes) {
    public Changes_v2(ApiMetadata oldApiVersion, ApiMetadata newApiVersion) {
        this(newApiVersion, oldApiVersion, new BrokenChange());
    }
    
}
