package com.example.dockerpoc;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.cache.annotation.Cacheable;

@Service
public class CryptoService {

    private final PriceRepository repository;
    private final RestClient restClient;
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "MeterRegistry is managed by Spring and intended to be shared")
    private final MeterRegistry meterRegistry;

    public CryptoService(PriceRepository repository, RestClient.Builder restClientBuilder, MeterRegistry meterRegistry) {
        this.repository = repository;
        this.restClient = restClientBuilder.baseUrl("https://api.coinbase.com").build();
        this.meterRegistry = meterRegistry;
    }

    @Cacheable(cacheNames = "coinbaseSpot", key = "#symbol.toUpperCase()")
    public PriceEntity fetchAndSavePrice(String symbol) {
        CoinbaseResponse.Data data = fetchSpotData(symbol);

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

    public List<PriceEntity> getAllHistoryDesc() {
        return repository.findAllByOrderByIdDesc();
    }

    public List<PriceEntity> getHistoryBySymbolDesc(String symbol) {
        return repository.findBySymbolOrderByIdDesc(symbol);
    }

    public long getHistoryCount() {
        return repository.countAllBy();
    }

    @Cacheable(cacheNames = "coinbaseSpotReadonly", key = "#symbol.toUpperCase()")
    public CoinbaseResponse.Data getSpotPrice(String symbol) {
        return fetchSpotData(symbol);
    }

    private CoinbaseResponse.Data fetchSpotData(String symbol) {
        String normalizedSymbol = symbol.toUpperCase(Locale.ROOT);
        String pair = normalizedSymbol + "-USD";
        Counter counter = Counter.builder("coinbase.spot.count")
                .description("Count of Coinbase spot requests")
                .tag("symbol", normalizedSymbol)
                .register(meterRegistry);
        Timer timer = Timer.builder("coinbase.spot.latency")
                .description("Latency for Coinbase spot requests")
                .tag("symbol", normalizedSymbol)
                .publishPercentileHistogram()
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);
        counter.increment();

        CoinbaseResponse response = timer.record(() -> restClient.get()
                .uri("/v2/prices/{pair}/spot", pair)
                .retrieve()
                .body(CoinbaseResponse.class));

        if (response == null || response.data() == null) {
            throw new IllegalStateException("Empty response from Coinbase");
        }

        return response.data();
    }
}
