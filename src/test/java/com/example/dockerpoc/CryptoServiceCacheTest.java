package com.example.dockerpoc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;
import org.mockito.Mockito;

@SpringBootTest(classes = {CryptoService.class, CryptoServiceCacheTest.TestConfig.class})
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
@Import(CryptoServiceCacheTest.TestConfig.class)
class CryptoServiceCacheTest {

    @Configuration
    @EnableCaching
    static class TestConfig {
        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("coinbaseSpot");
        }

        @Bean
        PriceRepository priceRepository() {
            return Mockito.mock(PriceRepository.class);
        }

        @Bean
        RestClient.Builder restClientBuilder() {
            RestClient.Builder builder = Mockito.mock(RestClient.Builder.class);
            RestClient client = restClient();
            when(builder.baseUrl("https://api.coinbase.com")).thenReturn(builder);
            when(builder.build()).thenReturn(client);
            return builder;
        }

        @Bean
        RestClient restClient() {
            return Mockito.mock(RestClient.class);
        }

        @SuppressWarnings("rawtypes")
        @Bean
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec() {
            return Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        }

        @SuppressWarnings("rawtypes")
        @Bean
        RestClient.RequestHeadersSpec requestHeadersSpec() {
            return Mockito.mock(RestClient.RequestHeadersSpec.class);
        }

        @Bean
        RestClient.ResponseSpec responseSpec() {
            return Mockito.mock(RestClient.ResponseSpec.class);
        }
    }

    @Autowired
    private CryptoService service;

    @Autowired
    private RestClient.Builder restClientBuilder;

    @Autowired
    private RestClient restClient;

    @Autowired
    @SuppressWarnings("rawtypes")
    private RestClient.RequestHeadersUriSpec uriSpec;

    @Autowired
    @SuppressWarnings("rawtypes")
    @org.springframework.beans.factory.annotation.Qualifier("requestHeadersSpec")
    private RestClient.RequestHeadersSpec headersSpec;

    @Autowired
    private RestClient.ResponseSpec responseSpec;

    @Autowired
    private PriceRepository priceRepository;

    @BeforeEach
    void setUp() {
        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri("/v2/prices/{pair}/spot", "BTC-USD")).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        CoinbaseResponse response = new CoinbaseResponse(new CoinbaseResponse.Data("BTC", "USD", "123.45"));
        when(responseSpec.body(CoinbaseResponse.class)).thenReturn(response);
    }

    @Test
    void fetchAndSavePrice_isCachedBySymbol() {
        when(responseSpec.body(CoinbaseResponse.class))
                .thenReturn(new CoinbaseResponse(new CoinbaseResponse.Data("BTC", "USD", "123.45")));
        when(priceRepository.save(Mockito.any(PriceEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PriceEntity first = service.fetchAndSavePrice("btc");
        PriceEntity second = service.fetchAndSavePrice("BTC");

        assertEquals(first.getSymbol(), second.getSymbol());
        assertEquals(first.getPrice(), second.getPrice());
        verify(restClient, times(1)).get();
        verify(priceRepository, times(1)).save(Mockito.any(PriceEntity.class));
    }
}
