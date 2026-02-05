package com.example.dockerpoc;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final CryptoService service;

    public AuditController(CryptoService service) {
        this.service = service;
    }

    @PostMapping("/{symbol}")
    public PriceEntity auditPrice(@PathVariable String symbol) {
        return service.fetchAndSavePrice(symbol);
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
