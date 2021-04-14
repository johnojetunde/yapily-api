package com.yapily.marvel.app.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.java8.Java8CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

import static com.yapily.marvel.domain.util.MapperUtil.MAPPER;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;

@RequiredArgsConstructor
public final class RetrofitProvider {

    private final Integer connectionTimeoutInSeconds;
    private final Integer readTimeoutInSeconds;
    private final Integer writeTimeoutInSeconds;

    public <T> T initializer(Class<T> apiInterface, String baseUrl) {
        return provideRetrofit(baseUrl).create(apiInterface);
    }

    private Retrofit provideRetrofit(String baseUrl) {
        return new Retrofit.Builder()
                .client(provideOkHttpClient(connectionTimeoutInSeconds, readTimeoutInSeconds, writeTimeoutInSeconds))
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create(MAPPER))
                .addCallAdapterFactory(Java8CallAdapterFactory.create())
                .build();
    }

    private static OkHttpClient provideOkHttpClient(Integer connectionTimeout,
                                                    Integer readTimeout,
                                                    Integer writeTimeout) {
        return new OkHttpClient.Builder()
                .connectTimeout(connectionTimeout, SECONDS)
                .readTimeout(readTimeout, SECONDS)
                .writeTimeout(writeTimeout, SECONDS)
                .addInterceptor(new LoggingInterceptor())
                .build();
    }

    @Slf4j
    public static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
            Response response = chain.proceed(request);
            long t2 = System.nanoTime();
            log.debug(format(
                    "Retrofit Received response  in %.1fms%n", (t2 - t1) / 1e6d));
            return response;
        }
    }
}