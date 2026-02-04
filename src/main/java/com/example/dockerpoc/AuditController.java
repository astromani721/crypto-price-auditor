package com.example.dockerpoc;

import java.util.List;
import java.util.Locale;
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
}
