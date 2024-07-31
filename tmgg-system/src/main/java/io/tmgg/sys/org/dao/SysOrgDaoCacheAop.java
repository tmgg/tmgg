package io.tmgg.sys.org.dao;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SysOrgDaoCacheAop {

    @Pointcut("execution(* io.tmgg.sys.org.dao.SysOrgDao.save*(..))")
    public void save() {
    }

    @Pointcut("execution(* io.tmgg.lang.dao.BaseDao+.save*(..)) && target(io.tmgg.sys.org.dao.SysOrgDao)")
    public void superSave() {

    }

    @Pointcut("execution(* io.tmgg.sys.org.dao.SysOrgDao.delete*(..))")
    public void delete() {
    }


    @Pointcut("execution(* io.tmgg.lang.dao.BaseDao+.delete*(..)) && target(io.tmgg.sys.org.dao.SysOrgDao)")
    public void superDelete() {

    }


    @After("save() || delete() || superDelete() || superSave()")
    public void cleanCache(JoinPoint joinPoint) {
        // 获取目标对象
        Object target = joinPoint.getTarget();
        if(target instanceof SysOrgDao){
            SysOrgDao dao = (SysOrgDao) target;
            dao.cleanCache();
        }
    }



}
