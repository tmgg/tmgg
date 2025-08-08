package io.tmgg.framework.cache;

import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Arrays;

@Aspect
@Component
public class CacheAspect {

    @Resource
    private CacheService cacheService;

    @Around("@annotation(ehCache)")
    public <T> T around(ProceedingJoinPoint joinPoint, Cache ehCache) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<T> returnType = signature.getReturnType();

        Object[] args = joinPoint.getArgs();
        Assert.state(args != null && args.length > 0, "缓存注解的方法必须有参数");
        String key = Arrays.deepToString(args);

        String cacheName = ehCache.name();
        org.ehcache.Cache<String, T> cache = cacheService.getOrCreateCache(cacheName, returnType, ehCache.expire());


        if(cache.containsKey(key)){
            return cache.get(key);
        }

        // 执行方法
        T result = (T) joinPoint.proceed();

        // 缓存结果
        if (result != null ) {
            cache.put(key, result);
        }

        return result;
    }





}