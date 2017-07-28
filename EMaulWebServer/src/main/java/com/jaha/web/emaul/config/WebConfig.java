package com.jaha.web.emaul.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.jaha.web.emaul.converter.DateConverter;
import com.jaha.web.emaul.converter.ParcelLockerSearchTypeConverter;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Bean
    public LoginChecker LoginCheckInterceptor() {
        return new LoginChecker();
    }

    @Bean
    public BoardAuthChecker getBoardAuthChecker() {
        return new BoardAuthChecker();
    }

    @Bean
    public PagingArgumentResolver getPagingArgumentResolver() {
        return new PagingArgumentResolver();
    }

    @Bean
    public SortHandlerMethodArgumentResolver sortResolver() {
        SortHandlerMethodArgumentResolver shmar = new SortHandlerMethodArgumentResolver();
        // shmar.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        return shmar;
    }

    @Bean
    public PageableHandlerMethodArgumentResolver pageableResolver() {
        PageableHandlerMethodArgumentResolver phmar = new PageableHandlerMethodArgumentResolver(sortResolver());
        // phmar.setOneIndexedParameters(true);
        phmar.setPageParameterName("page");
        phmar.setSizeParameterName("size");

        // Pageable fallback = new PageRequest(0, 20);
        // phmar.setFallbackPageable(fallback);
        // phmar.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);

        return new PageableHandlerMethodArgumentResolver();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(LoginCheckInterceptor());
        registry.addInterceptor(this.getBoardAuthChecker());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(sortResolver());
        argumentResolvers.add(pageableResolver());

        // ProxyingHandlerMethodArgumentResolver resolver = new ProxyingHandlerMethodArgumentResolver(conversionService.getObject());
        // resolver.setBeanFactory(context);
        // resolver.setResourceLoader(context);

        // argumentResolvers.add(resolver);

        argumentResolvers.add(this.getPagingArgumentResolver());
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new ParcelLockerSearchTypeConverter());
        registry.addConverter(new DateConverter());
    }

    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectionRequestTimeout(3000); // 연결 요청 지연이 3초를 초과하면 Exception
        requestFactory.setConnectTimeout(10000); // 서버에 연결 지연이 10초를 초과하면 Exception
        requestFactory.setReadTimeout(3000); // 데이터 수신 지연이 3초를 초과하면 Exception

        return new RestTemplate(requestFactory);
    }

}
