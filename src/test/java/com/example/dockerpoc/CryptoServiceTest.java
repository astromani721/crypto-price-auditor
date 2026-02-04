package com.example.dockerpoc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class CryptoServiceTest {

    @Mock
    private PriceRepository repository;

    @Mock
    private RestClient.Builder restClientBuilder;

    @Mock
    private RestClient restClient;

    @SuppressWarnings("rawtypes")
    @Mock
    private RestClient.RequestHeadersUriSpec uriSpec;

    @SuppressWarnings("rawtypes")
    @Mock
    private RestClient.RequestHeadersSpec headersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private CryptoService service;

    @BeforeEach
    void setUp() {
        when(restClientBuilder.baseUrl("https://api.coinbase.com")).thenReturn(restClientBuilder);
        when(restClientBuilder.build()).thenReturn(restClient);
        service = new CryptoService(repository, restClientBuilder);
    }

    @Test
    void fetchAndSavePrice_mapsResponseAndSaves() {
        CoinbaseResponse response = new CoinbaseResponse(new CoinbaseResponse.Data("BTC", "USD", "123.45"));

        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri("/v2/prices/{pair}/spot", "BTC-USD")).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CoinbaseResponse.class)).thenReturn(response);
        when(repository.save(any(PriceEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PriceEntity saved = service.fetchAndSavePrice("btc");

        assertEquals("BTC", saved.getSymbol());
        assertEquals("USD", saved.getCurrency());
        assertEquals(123.45, saved.getPrice());
        assertNotNull(saved.getFetchedAt());
        verify(repository).save(any(PriceEntity.class));
    }

    @Test
    void getAllHistory_delegatesToRepository() {
        List<PriceEntity> entities = List.of(new PriceEntity());
        when(repository.findAll()).thenReturn(entities);

        List<PriceEntity> result = service.getAllHistory();

        assertEquals(entities, result);
    }

    @Test
    void getHistoryBySymbol_delegatesToRepository() {
        List<PriceEntity> entities = List.of(new PriceEntity());
        when(repository.findBySymbol("BTC")).thenReturn(entities);

        List<PriceEntity> result = service.getHistoryBySymbol("BTC");

        assertEquals(entities, result);
    }
}
