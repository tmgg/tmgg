# 常见问题

## 如何设置某个请求不需要登录？

方法1、在方法上增加@Public注解

方法2、在配置文件中增加exclude配置（ide会自动提示）

## 如何覆盖框架页面-？

在项目中创建同路径的页面即可

## 如何查看管理员密码，密码丢失后怎么办？

首次运行程序时候，会自动创建管理员，并将密码打印在控制台。
如果后期网络密码，需在数据库中将用户表（sys_user)的管理员（superAdmin)记录的密码设置为空。然后启动后台，新密码将自动生成并打印在控制台

## 为什么不用redis

系统系统尽量少依赖中间件，方便开发和部署

## 推荐部署工具

推荐使用docker-admin，支持源码构建、部署

## 如何接受前端日期区间

前端传入时间戳数组， 可使用FieldDateRange组件
后端使用RequestBody接受对象

可参考操作日志

【提示】，查询日期区间数据是，记得将结束日期设置为23.59.59
 ```
   @Data
    public static class QueryParam {
        Date[] dateRange;
        String name;
    }



    @RequestMapping("page")
    public AjaxResult page(@RequestBody QueryParam queryParam, @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Date[] dateRange = queryParam.getDateRange();

        JpaQuery<SysLog> q = new JpaQuery<>();
        
        if(dateRange !=null){
            dateRange[1] = DateUtil.endOfDay(dateRange[1]);
            q.between("createTime", dateRange);
        }

        q.like(SysLog.Fields.name, queryParam.getName());

        Page<SysLog> page = sysOpLogService.findAll(q, pageable);
        return service.autoRender(page);;
    }
 
 ```
