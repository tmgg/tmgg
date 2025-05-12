package io.tmgg.modules.auth;

import io.tmgg.framework.dbconfig.DbValue;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LoginAttemptService {

    @DbValue("sys.login.lock.maxFailedAttempts")
    private int MAX_ATTEMPTS;

    /**
     * 锁定时间，分钟
     */
    @DbValue("sys.login.lock.time")
    private long LOCK_TIME;

    // 使用ConcurrentMap保证线程安全
    private static final ConcurrentMap<String, AtomicInteger> attemptsCache = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, Long> lockedAccounts = new ConcurrentHashMap<>();

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
        AtomicInteger attemptCount = attemptsCache.get(username);
        if (attemptCount == null) {
            attemptCount = new AtomicInteger(0);
            AtomicInteger existingAttempt = attemptsCache.putIfAbsent(username, attemptCount);
            if (existingAttempt != null) {
                attemptCount = existingAttempt;
            }
        }

        int newCount = attemptCount.incrementAndGet();

        // 如果失败次数达到阈值，锁定账户
        if (newCount >= MAX_ATTEMPTS) {
            lockAccount(username);
        }
    }

    /**
     * 登录成功时清除失败记录
     * @param username 用户名
     */
    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
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
            attemptsCache.remove(username);
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

        AtomicInteger attemptCount = attemptsCache.get(username);
        if (attemptCount == null) {
            return MAX_ATTEMPTS;
        }

        return MAX_ATTEMPTS - attemptCount.get();
    }
}
