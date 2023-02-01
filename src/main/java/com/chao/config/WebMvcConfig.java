package com.chao.config;

import com.chao.common.JacksonObjectMapper;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.*;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Response;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;


@Configuration
@EnableSwagger2
@EnableKnife4j
public class WebMvcConfig implements WebMvcConfigurer
{
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 扩展MVC框架的消息转换器
     *
     * @param converters 转换器
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters)
    {
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将Java转换为Json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        converters.add(0, messageConverter);


    }

    private ApiInfo apiInfo()
    {
        return new ApiInfoBuilder()
                .title("门禁管理系统")
                .version("1.0")
                .description("门禁管理系统接口文档")
                .contact(new Contact("Chao", null, "Sand_van@hotmail.com"))
                .build();
    }

    @Bean
    public Docket creatRestApi()
    {
        List<Response> responseList = new ArrayList<>();
        responseList.add(new ResponseBuilder().code("401").description("Unauthorized").build());
        responseList.add(new ResponseBuilder().code("403").description("Forbidden").build());
        responseList.add(new ResponseBuilder().code("404").description("Not Found").build());
        responseList.add(new ResponseBuilder().code("430").description("普通错误").build());

        return new Docket(DocumentationType.SWAGGER_2)
                .globalResponses(HttpMethod.GET, responseList)
                .globalResponses(HttpMethod.POST, responseList)
                .globalResponses(HttpMethod.PUT, responseList)
                .globalResponses(HttpMethod.DELETE, responseList)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.chao.controller"))
//                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
}
