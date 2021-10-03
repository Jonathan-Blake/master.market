package com.stocktrader.market.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class ErrorResponse {
    @JsonProperty
    private Instant timestamp;
    @JsonProperty
    private String error;
    @JsonProperty
    private String message;
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String path;

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
