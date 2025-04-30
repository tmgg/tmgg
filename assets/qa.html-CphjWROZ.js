import{_ as e,c as n,a as s,o as i}from"./app-Brlie9mU.js";const l={};function d(t,a){return i(),n("div",null,a[0]||(a[0]=[s(`<h1 id="常见问题" tabindex="-1"><a class="header-anchor" href="#常见问题"><span>常见问题</span></a></h1><h2 id="如何设置某个请求不需要登录" tabindex="-1"><a class="header-anchor" href="#如何设置某个请求不需要登录"><span>如何设置某个请求不需要登录？</span></a></h2><p>方法1、在方法上增加@Public注解</p><p>方法2、在配置文件中增加exclude配置（ide会自动提示）</p><h2 id="如何覆盖框架页面" tabindex="-1"><a class="header-anchor" href="#如何覆盖框架页面"><span>如何覆盖框架页面-？</span></a></h2><p>在项目中创建同路径的页面即可</p><h2 id="如何查看管理员密码-密码丢失后怎么办" tabindex="-1"><a class="header-anchor" href="#如何查看管理员密码-密码丢失后怎么办"><span>如何查看管理员密码，密码丢失后怎么办？</span></a></h2><p>首次运行程序时候，会自动创建管理员，并将密码打印在控制台。 如果后期网络密码，需在数据库中将用户表（sys_user)的管理员（superAdmin)记录的密码设置为空。然后启动后台，新密码将自动生成并打印在控制台</p><h2 id="为什么不用redis" tabindex="-1"><a class="header-anchor" href="#为什么不用redis"><span>为什么不用redis</span></a></h2><p>系统系统尽量少依赖中间件，方便开发和部署</p><h2 id="推荐部署工具" tabindex="-1"><a class="header-anchor" href="#推荐部署工具"><span>推荐部署工具</span></a></h2><p>推荐使用docker-admin，支持源码构建、部署</p><h2 id="如何接受前端日期区间" tabindex="-1"><a class="header-anchor" href="#如何接受前端日期区间"><span>如何接受前端日期区间</span></a></h2><p>前端传入时间戳数组， 可使用FieldDateRange组件 后端使用RequestBody接受对象</p><p>可参考操作日志</p><p>【提示】，查询日期区间数据是，记得将结束日期设置为23.59.59</p><div class="language-text line-numbers-mode" data-highlighter="prismjs" data-ext="text"><pre><code><span class="line">  @Data</span>
<span class="line">   public static class QueryParam {</span>
<span class="line">       Date[] dateRange;</span>
<span class="line">       String name;</span>
<span class="line">   }</span>
<span class="line"></span>
<span class="line"></span>
<span class="line"></span>
<span class="line">   @RequestMapping(&quot;page&quot;)</span>
<span class="line">   public AjaxResult page(@RequestBody QueryParam queryParam, @PageableDefault(sort = &quot;createTime&quot;, direction = Sort.Direction.DESC) Pageable pageable) {</span>
<span class="line">       Date[] dateRange = queryParam.getDateRange();</span>
<span class="line"></span>
<span class="line">       JpaQuery&lt;SysLog&gt; q = new JpaQuery&lt;&gt;();</span>
<span class="line">       </span>
<span class="line">       if(dateRange !=null){</span>
<span class="line">           dateRange[1] = DateUtil.endOfDay(dateRange[1]);</span>
<span class="line">           q.between(&quot;createTime&quot;, dateRange);</span>
<span class="line">       }</span>
<span class="line"></span>
<span class="line">       q.like(SysLog.Fields.name, queryParam.getName());</span>
<span class="line"></span>
<span class="line">       Page&lt;SysLog&gt; page = sysOpLogService.findAll(q, pageable);</span>
<span class="line">       return AjaxResult.ok().data(page);</span>
<span class="line">   }</span>
<span class="line"></span>
<span class="line"></span></code></pre><div class="line-numbers" aria-hidden="true" style="counter-reset:line-number 0;"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div>`,17)]))}const c=e(l,[["render",d]]),p=JSON.parse('{"path":"/qa.html","title":"常见问题","lang":"zh-CN","frontmatter":{},"headers":[{"level":2,"title":"如何设置某个请求不需要登录？","slug":"如何设置某个请求不需要登录","link":"#如何设置某个请求不需要登录","children":[]},{"level":2,"title":"如何覆盖框架页面-？","slug":"如何覆盖框架页面","link":"#如何覆盖框架页面","children":[]},{"level":2,"title":"如何查看管理员密码，密码丢失后怎么办？","slug":"如何查看管理员密码-密码丢失后怎么办","link":"#如何查看管理员密码-密码丢失后怎么办","children":[]},{"level":2,"title":"为什么不用redis","slug":"为什么不用redis","link":"#为什么不用redis","children":[]},{"level":2,"title":"推荐部署工具","slug":"推荐部署工具","link":"#推荐部署工具","children":[]},{"level":2,"title":"如何接受前端日期区间","slug":"如何接受前端日期区间","link":"#如何接受前端日期区间","children":[]}],"git":{"updatedTime":1745715745000,"contributors":[{"name":"jiangtao","username":"jiangtao","email":"jiangtao.cctv@qq.com","commits":1,"url":"https://github.com/jiangtao"}],"changelog":[{"hash":"7183b56fe4ba811be68707506350830a8d66080f","time":1745715745000,"email":"jiangtao.cctv@qq.com","author":"jiangtao","message":"init"}]},"filePathRelative":"qa.md"}');export{c as comp,p as data};
