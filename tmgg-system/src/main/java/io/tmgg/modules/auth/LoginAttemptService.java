package io.tmgg.modules.auth;

import io.tmgg.framework.cache.CacheService;
import io.tmgg.framework.dbconfig.DbValue;
import jakarta.annotation.Resource;
import org.ehcache.Cache;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class LoginAttemptService {

    @DbValue("sys.login.lock.maxAttempts")
    private int MAX_ATTEMPTS;

    /**
     * 锁定时间，分钟
     */
    @DbValue("sys.login.lock.time")
    private long LOCK_TIME;

    private final Cache<String, Integer> loginAttempts;
    private final Cache<String, Long> lockedAccounts ;


    public LoginAttemptService(CacheService cacheService){
        loginAttempts = cacheService.newCache("loginAttempts", Integer.class, 100, 2, Duration.ofDays(1));
        lockedAccounts = cacheService.newCache("lockedAccounts", Long.class, 1000, 1, Duration.ofDays(1));
    }



    /**
     * 记录登录失败
     * @param username 用户名
     */
    public void loginFailed(String username) {
        // 如果账户已锁定，直接返回
        if (isAccountLocked(username)) {
            return;
        }

        // 原子性地增加失败次数
        Integer attemptCount = loginAttempts.get(username);
        if (attemptCount == null) {
            attemptCount = 0;
        }
        attemptCount++;

        loginAttempts.put(username, attemptCount);

        // 如果失败次数达到阈值，锁定账户
        if (attemptCount >= MAX_ATTEMPTS) {
            lockAccount(username);
        }
    }

    /**
     * 登录成功时清除失败记录
     * @param username 用户名
     */
    public void loginSucceeded(String username) {
        loginAttempts.remove(username);
        lockedAccounts.remove(username);
    }

    /**
     * 检查账户是否被锁定
     * @param username 用户名
     * @return 是否被锁定
     */
    public boolean isAccountLocked(String username) {
        Long lockTime = lockedAccounts.get(username);
        if (lockTime == null) {
            return false;
        }

        // 检查锁定是否已过期
        if (System.currentTimeMillis() - lockTime > LOCK_TIME * 60 * 1000) {
            lockedAccounts.remove(username);
            loginAttempts.remove(username);
            return false;
        }

        return true;
    }

    /**
     * 锁定账户
     * @param username 用户名
     */
    private void lockAccount(String username) {
        lockedAccounts.put(username, System.currentTimeMillis());
    }

    /**
     * 获取剩余尝试次数
     * @param username 用户名
     * @return 剩余尝试次数
     */
    public int getRemainingAttempts(String username) {
        if (isAccountLocked(username)) {
            return 0;
        }

        Integer attemptCount = loginAttempts.get(username);
        if (attemptCount == null) {
            return MAX_ATTEMPTS;
        }

        return MAX_ATTEMPTS - attemptCount;
    }
}
