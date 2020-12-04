//package com.hebtu.havefun.config;
//
//import com.fasterxml.jackson.annotation.JsonAutoDetect;
//import com.fasterxml.jackson.annotation.JsonTypeInfo;
//import com.fasterxml.jackson.annotation.PropertyAccessor;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.CachingConfigurerSupport;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.cache.RedisCacheConfiguration;
//import org.springframework.data.redis.cache.RedisCacheManager;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.RedisSerializationContext;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//import java.time.Duration;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author PengHuAnZhi
// * @createTime 2020/12/4 7:58
// * @projectName HaveFun
// * @className RedisConfig.java
// * @description TODO
// */
//@Configuration
//public class RedisConfig extends CachingConfigurerSupport {
//    //固定模板，一般企业中可以直接使用
//    @Bean
//    @SuppressWarnings(value = "all")
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        //实际开发<String, Object>用的很多，所以就这里指定
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(redisConnectionFactory);
//
//        //序列化配置
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
//        Jackson2JsonRedisSerializer<Object> objectJackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
//        objectJackson2JsonRedisSerializer.setObjectMapper(objectMapper);
//        template.setKeySerializer(objectJackson2JsonRedisSerializer);
//
//        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
//
//        //key采用String的序列化方式
//        template.setKeySerializer(stringRedisSerializer);
//        //value采用String的序列化方式
//        template.setValueSerializer(objectJackson2JsonRedisSerializer);
//        //hash的也采用String的序列化方式
//        template.setHashKeySerializer(stringRedisSerializer);
//        //hash的值采用jackson的序列化方式
//        template.setHashValueSerializer(objectJackson2JsonRedisSerializer);
//        template.afterPropertiesSet();
//        return template;
//    }
//
//    /**
//     * 配置一个CacheManager才能使用@Cacheable等注解
//     */
//    @Bean
//    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
//        Jackson2JsonRedisSerializer<Object> objectJackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
//        //生成一个默认配置，通过config对象即可对缓存进行自定义配置
//        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
//        config = config
//                // 设置 key为string序列化
//                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
//                // 设置value为json序列化
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(objectJackson2JsonRedisSerializer))
//                // 不缓存空值
//                .disableCachingNullValues()
//                // 设置缓存的默认过期时间 30分钟
//                .entryTtl(Duration.ofHours(1L));
//
//        //特殊缓存空间应用不同的配置
//        Map<String, RedisCacheConfiguration> map = new HashMap<>();
//        //将轮播图地址设置永不过期，因为基本不会改动它
//        map.put("activity_constant", config.entryTtl(Duration.ofMinutes(-1L)));//provider1缓存空间过期时间 30分钟
//
//        //使用自定义的缓存配置初始化一个RedisCacheManager
//        RedisCacheManager cacheManager = RedisCacheManager.builder(connectionFactory)
//                .cacheDefaults(config) //默认配置
//                .withInitialCacheConfigurations(map) //特殊缓存
//                .transactionAware() //事务
//                .build();
//
//        return cacheManager;
//    }
//}