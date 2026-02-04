package com.example.dockerpoc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuditControllerTest {

    @Mock
    private CryptoService service;

    @InjectMocks
    private AuditController controller;

    @Test
    void auditPrice_delegatesToService() {
        PriceEntity entity = new PriceEntity();
        when(service.fetchAndSavePrice("BTC")).thenReturn(entity);

        PriceEntity result = controller.auditPrice("BTC");

        assertEquals(entity, result);
        verify(service).fetchAndSavePrice("BTC");
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
}
