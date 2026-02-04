package com.example.dockerpoc;

public record CoinbaseResponse(Data data) {
    public record Data(String base, String currency, String amount) {
    }
}