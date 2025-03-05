主要写一个map，key为Excel中的列名，值为一个函数，方便自定义

```java
        Map<String, Function<SysUser,Object>> columns = new LinkedHashMap<>();
        columns.put("姓名", SysUser::getName); 
        columns.put("描述", user->"年龄:" + user.getAge() + ", 性别:" + user.getSex());


        ExcelExportTool.exportTable("用户列表.xlsx", columns, list, response);
```