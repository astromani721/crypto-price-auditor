package com.example.dockerpoc;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final CryptoService service;
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "MeterRegistry is managed by Spring and intended to be shared")
    private final MeterRegistry meterRegistry;

    public AuditController(CryptoService service, MeterRegistry meterRegistry) {
        this.service = service;
        this.meterRegistry = meterRegistry;
    }

    @PostMapping("/{symbol}")
    public PriceEntity auditPrice(@PathVariable String symbol) {
        String normalizedSymbol = symbol.toUpperCase(Locale.ROOT);
        Counter counter = Counter.builder("audit.price.count")
                .description("Count of price audit requests")
                .tag("symbol", normalizedSymbol)
                .register(meterRegistry);
        Timer timer = Timer.builder("audit.price.latency")
                .description("Latency for price audit")
                .tag("symbol", normalizedSymbol)
                .publishPercentileHistogram()
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);
        counter.increment();
        return timer.record(() -> service.fetchAndSavePrice(symbol));
    }

    @GetMapping("/{symbol}/spot")
    public CoinbaseResponse.Data getSpotPrice(@PathVariable String symbol) {
        return service.getSpotPrice(symbol);
    }

    @GetMapping("/history")
    public List<PriceEntity> getAllHistory() {
        return service.getAllHistoryDesc();
    }

    @GetMapping("/history/{symbol}")
    public List<PriceEntity> getSymbolHistory(@PathVariable String symbol) {
        return service.getHistoryBySymbolDesc(symbol.toUpperCase(Locale.ROOT));
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        String symbol = "BTC";
        CoinbaseResponse.Data spot = service.getSpotPrice(symbol);
        long count = service.getHistoryCount();
        boolean spotOk = spot != null
                && "BTC".equalsIgnoreCase(spot.base())
                && "USD".equalsIgnoreCase(spot.currency())
                && spot.amount() != null
                && isPositiveNumber(spot.amount());
        if (!spotOk) {
            throw new IllegalStateException("Invalid spot response for health check");
        }
        return Map.of(
                "symbol", spot.base(),
                "count", count,
                "healthy", count >= 0
        );
    }

    private boolean isPositiveNumber(String amount) {
        try {
            return Double.parseDouble(amount) > 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
