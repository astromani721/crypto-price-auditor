package com.example.dockerpoc;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.cache.annotation.Cacheable;

@Service
public class CryptoService {

    private final PriceRepository repository;
    private final RestClient restClient;

    public CryptoService(PriceRepository repository, RestClient.Builder restClientBuilder) {
        this.repository = repository;
        this.restClient = restClientBuilder.baseUrl("https://api.coinbase.com").build();
    }

    @Cacheable(cacheNames = "coinbaseSpot", key = "#symbol.toUpperCase()")
    public PriceEntity fetchAndSavePrice(String symbol) {
        String pair = symbol.toUpperCase() + "-USD";

        CoinbaseResponse response = restClient.get()
                .uri("/v2/prices/{pair}/spot", pair)
                .retrieve()
                .body(CoinbaseResponse.class);

        if (response == null || response.data() == null) {
            throw new IllegalStateException("Empty response from Coinbase");
        }

        CoinbaseResponse.Data data = response.data();

        PriceEntity entity = new PriceEntity();
        entity.setSymbol(data.base());
        entity.setPrice(Double.parseDouble(data.amount()));
        entity.setCurrency(data.currency());
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
