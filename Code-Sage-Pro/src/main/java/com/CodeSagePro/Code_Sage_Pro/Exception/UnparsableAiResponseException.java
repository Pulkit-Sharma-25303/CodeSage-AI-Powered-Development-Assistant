package com.CodeSagePro.Code_Sage_Pro.Exception;


import java.io.IOException;

public class UnparsableAiResponseException extends IOException {
    private final String aiResponse;

    public UnparsableAiResponseException(String message, String aiResponse) {
        super(message);
        this.aiResponse = aiResponse;
    }

    public String getAiResponse() {
        return aiResponse;
    }
}