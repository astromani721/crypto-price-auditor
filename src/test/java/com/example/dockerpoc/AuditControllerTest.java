package com.example.dockerpoc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

@ExtendWith(MockitoExtension.class)
class AuditControllerTest {

    @Mock
    private CryptoService service;

    private AuditController controller;

    @BeforeEach
    void setUp() {
        controller = new AuditController(service, new SimpleMeterRegistry());
    }

    @Test
    void auditPrice_delegatesToService() {
        PriceEntity entity = new PriceEntity();
        when(service.fetchAndSavePrice("BTC")).thenReturn(entity);

        PriceEntity result = controller.auditPrice("BTC");

        assertEquals(entity, result);
        verify(service).fetchAndSavePrice("BTC");
    }

    @Test
    void getSpotPrice_delegatesToService() {
        CoinbaseResponse.Data data = new CoinbaseResponse.Data("BTC", "USD", "123.45");
        when(service.getSpotPrice("BTC")).thenReturn(data);

        CoinbaseResponse.Data result = controller.getSpotPrice("BTC");

        assertEquals(data, result);
        verify(service).getSpotPrice("BTC");
    }

    @Test
    void getAllHistory_delegatesToService() {
        List<PriceEntity> entities = List.of(new PriceEntity());
        when(service.getAllHistoryDesc()).thenReturn(entities);

        List<PriceEntity> result = controller.getAllHistory();

        assertEquals(entities, result);
        verify(service).getAllHistoryDesc();
    }

    @Test
    void getSymbolHistory_uppercasesSymbol() {
        List<PriceEntity> entities = List.of(new PriceEntity());
        when(service.getHistoryBySymbolDesc("BTC")).thenReturn(entities);

        List<PriceEntity> result = controller.getSymbolHistory("btc");

        assertEquals(entities, result);
        verify(service).getHistoryBySymbolDesc("BTC");
    }

    @Test
    void health_callsSpotAndCount_andValidatesSpot() {
        CoinbaseResponse.Data data = new CoinbaseResponse.Data("BTC", "USD", "123.45");
        when(service.getSpotPrice("BTC")).thenReturn(data);
        when(service.getHistoryCount()).thenReturn(0L);

        var result = controller.health();

        assertEquals("BTC", result.get("symbol"));
        assertEquals(0L, result.get("count"));
        assertEquals(true, result.get("healthy"));
        verify(service).getSpotPrice("BTC");
        verify(service).getHistoryCount();
    }
}
