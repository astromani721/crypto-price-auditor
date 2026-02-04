package com.example.dockerpoc;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CryptoService {

    private final PriceRepository repository;
    private final RestClient restClient;

    public CryptoService(PriceRepository repository, RestClient.Builder restClientBuilder) {
        this.repository = repository;
        this.restClient = restClientBuilder.baseUrl("https://api.coinbase.com").build();
    }

    public PriceEntity fetchAndSavePrice(String symbol) {
        String pair = symbol.toUpperCase() + "-USD";

        CoinbaseResponse response = restClient.get()
                .uri("/v2/prices/{pair}/spot", pair)
                .retrieve()
                .body(CoinbaseResponse.class);

        PriceEntity entity = new PriceEntity();
        entity.setSymbol(response.data().base());
        entity.setPrice(Double.parseDouble(response.data().amount()));
        entity.setCurrency(response.data().currency());
        entity.setFetchedAt(LocalDateTime.now());

        return repository.save(entity);
    }

    public List<PriceEntity> getAllHistory() {
        return repository.findAll();
    }

    public List<PriceEntity> getHistoryBySymbol(String symbol) {
        return repository.findBySymbol(symbol);
    }
}
