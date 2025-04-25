import{_ as s,c as a,a as e,o as i}from"./app-DJw0z2e2.js";const l={};function p(t,n){return i(),a("div",null,n[0]||(n[0]=[e(`<h1 id="扩展模块-支付" tabindex="-1"><a class="header-anchor" href="#扩展模块-支付"><span>扩展模块 - 支付</span></a></h1><p>app端需没有实例化控制器，需继承 PaymentController, 并设置RequestMapping</p><p>示例</p><div class="language-java line-numbers-mode" data-highlighter="prismjs" data-ext="java"><pre><code><span class="line"><span class="token keyword">package</span> <span class="token namespace">cn<span class="token punctuation">.</span>crec<span class="token punctuation">.</span>venue<span class="token punctuation">.</span>venue<span class="token punctuation">.</span>app<span class="token punctuation">.</span>controler</span><span class="token punctuation">;</span></span>
<span class="line"></span>
<span class="line"><span class="token keyword">import</span> <span class="token import"><span class="token namespace">io<span class="token punctuation">.</span>tmgg<span class="token punctuation">.</span>payment<span class="token punctuation">.</span></span><span class="token class-name">PaymentController</span></span><span class="token punctuation">;</span></span>
<span class="line"><span class="token keyword">import</span> <span class="token import"><span class="token namespace">lombok<span class="token punctuation">.</span>extern<span class="token punctuation">.</span>slf4j<span class="token punctuation">.</span></span><span class="token class-name">Slf4j</span></span><span class="token punctuation">;</span></span>
<span class="line"><span class="token keyword">import</span> <span class="token import"><span class="token namespace">org<span class="token punctuation">.</span>springframework<span class="token punctuation">.</span>web<span class="token punctuation">.</span>bind<span class="token punctuation">.</span>annotation<span class="token punctuation">.</span></span><span class="token operator">*</span></span><span class="token punctuation">;</span></span>
<span class="line"></span>
<span class="line"><span class="token annotation punctuation">@Slf4j</span></span>
<span class="line"><span class="token annotation punctuation">@RestController</span></span>
<span class="line"><span class="token annotation punctuation">@RequestMapping</span><span class="token punctuation">(</span><span class="token string">&quot;rest/payment&quot;</span><span class="token punctuation">)</span></span>
<span class="line"><span class="token keyword">public</span> <span class="token keyword">class</span> <span class="token class-name">PaymentAppController</span> <span class="token keyword">extends</span> <span class="token class-name">PaymentController</span> <span class="token punctuation">{</span></span>
<span class="line"></span>
<span class="line"></span>
<span class="line"><span class="token punctuation">}</span></span>
<span class="line"></span>
<span class="line"></span>
<span class="line"></span></code></pre><div class="line-numbers" aria-hidden="true" style="counter-reset:line-number 0;"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div><p>前端支付页面，uniapp示例</p><div class="language-text line-numbers-mode" data-highlighter="prismjs" data-ext="text"><pre><code><span class="line">&lt;template&gt;</span>
<span class="line">  &lt;view style=&quot;padding:32rpx&quot;&gt;</span>
<span class="line">    &lt;u-toast ref=&quot;uToast&quot;&gt;&lt;/u-toast&gt;</span>
<span class="line"></span>
<span class="line">    &lt;view style=&quot;display: flex;align-items: center;flex-direction: column&quot;&gt;</span>
<span class="line">      &lt;view&gt;</span>
<span class="line">        &lt;u--text :text=&quot;previewOrder.description&quot;&gt;&lt;/u--text&gt;</span>
<span class="line">      &lt;/view&gt;</span>
<span class="line">      &lt;u-gap&gt;&lt;/u-gap&gt;</span>
<span class="line">      &lt;view&gt;</span>
<span class="line">        &lt;u--text size=&quot;32&quot; mode=&quot;price&quot; :text=&quot;previewOrder.amount / 100&quot;&gt;&lt;/u--text&gt;</span>
<span class="line">      &lt;/view&gt;</span>
<span class="line">    &lt;/view&gt;</span>
<span class="line">    &lt;u-gap&gt;&lt;/u-gap&gt;   </span>
<span class="line">    &lt;u-radio-group</span>
<span class="line">        v-model=&quot;selectedPaymentChannelId&quot;</span>
<span class="line">        placement=&quot;column&quot;</span>
<span class="line">        @change=&quot;onPaymentChannelChange&quot;</span>
<span class="line">    &gt;</span>
<span class="line">      &lt;u-radio</span>
<span class="line">          :customStyle=&quot;{marginBottom: &#39;8px&#39;}&quot;</span>
<span class="line">          v-for=&quot;(item, index) in paymentChannelList&quot;</span>
<span class="line">          :key=&quot;index&quot;</span>
<span class="line">          :label=&quot;item.name&quot;</span>
<span class="line">          :name=&quot;item.id&quot;</span>
<span class="line">      &gt;</span>
<span class="line">      &lt;/u-radio&gt;</span>
<span class="line">    &lt;/u-radio-group&gt;</span>
<span class="line"></span>
<span class="line">    &lt;u-gap&gt;&lt;/u-gap&gt;</span>
<span class="line">    &lt;u-gap&gt;&lt;/u-gap&gt;</span>
<span class="line">    &lt;u-button @click=&quot;callFrontPay&quot; text=&quot;确认支付&quot; type=&quot;primary&quot;&gt;&lt;/u-button&gt;</span>
<span class="line"></span>
<span class="line">  &lt;/view&gt;</span>
<span class="line">&lt;/template&gt;</span>
<span class="line"></span>
<span class="line">&lt;script&gt;</span>
<span class="line">import http from &#39;@/common/vmeitime-http/interface&#39;;</span>
<span class="line"></span>
<span class="line">export default {</span>
<span class="line">  data() {</span>
<span class="line">    return {</span>
<span class="line">      orderId: null, // 业务订单id</span>
<span class="line"></span>
<span class="line">      // 业务标识， 如订单业务，充值业务等</span>
<span class="line">      bizCode: null,</span>
<span class="line"></span>
<span class="line">      // 选择的支付渠道</span>
<span class="line">      selectedPaymentChannelId: null,</span>
<span class="line"></span>
<span class="line">	  // 支付成功后返回页面</span>
<span class="line">	  returnPage: &#39;/&#39;,</span>
<span class="line"></span>
<span class="line">      paymentChannelList: [],</span>
<span class="line"></span>
<span class="line">      previewOrder: {</span>
<span class="line">        amount: 0</span>
<span class="line">      },</span>
<span class="line"></span>
<span class="line">    }</span>
<span class="line">  },</span>
<span class="line"></span>
<span class="line">  onLoad: function (options) {</span>
<span class="line">    this.orderId = options.orderId</span>
<span class="line">    this.bizCode = options.bizCode</span>
<span class="line">	this.returnPage = decodeURIComponent( options.returnPage)</span>
<span class="line">    this.getPreviewOrder()</span>
<span class="line">    this.getPaymentChannelList()</span>
<span class="line">  },</span>
<span class="line">  methods: {</span>
<span class="line">    getPreviewOrder() {</span>
<span class="line">      http.get(&#39;/rest/payment/previewOrder&#39;, {orderId: this.orderId, bizCode: this.bizCode}).then(response =&gt; {</span>
<span class="line">        this.previewOrder = response.data.data</span>
<span class="line">      })</span>
<span class="line">    },</span>
<span class="line"></span>
<span class="line">    getPaymentChannelList() {</span>
<span class="line">      http.get(&#39;/rest/payment/channelList&#39;, {orderId: this.orderId, bizCode: this.bizCode}).then(response =&gt; {</span>
<span class="line">        this.paymentChannelList = response.data.data</span>
<span class="line">		if(this.paymentChannelList.length &gt; 0){</span>
<span class="line">			this.selectedPaymentChannelId = this.paymentChannelList[0].id</span>
<span class="line">		}</span>
<span class="line">      })</span>
<span class="line">    },</span>
<span class="line">    onPaymentChannelChange(v) {</span>
<span class="line">      this.selectedPaymentChannelId = v;</span>
<span class="line">    },</span>
<span class="line"></span>
<span class="line"></span>
<span class="line">    callFrontPay() {</span>
<span class="line">      let params = {orderId: this.orderId, bizCode: this.bizCode, channelId: this.selectedPaymentChannelId};</span>
<span class="line">      http.get(&#39;/rest/payment/createOrder&#39;, params).then(response =&gt; {</span>
<span class="line">        let payInfo = response.data.data</span>
<span class="line">        uni.requestPayment({</span>
<span class="line">          ...payInfo,</span>
<span class="line">          package: payInfo.packageValue,</span>
<span class="line">          success: this.paySuccess,</span>
<span class="line">          fail: this.payFail</span>
<span class="line">        })</span>
<span class="line">      })</span>
<span class="line">    },</span>
<span class="line"></span>
<span class="line">    paySuccess(e) {</span>
<span class="line">	  const url = this.returnPage</span>
<span class="line">      this.$refs.uToast.show({</span>
<span class="line">        message: &#39;支付成功&#39;,</span>
<span class="line">		complete(){</span>
<span class="line">			uni.navigateTo({</span>
<span class="line">				url:url</span>
<span class="line">			})</span>
<span class="line">		}</span>
<span class="line">      });</span>
<span class="line">    },</span>
<span class="line">    payFail(e) {</span>
<span class="line">      this.$refs.uToast.show({</span>
<span class="line">        message: &#39;支付失败&#39;,</span>
<span class="line">      });</span>
<span class="line">    }</span>
<span class="line"></span>
<span class="line">  }</span>
<span class="line">}</span>
<span class="line">&lt;/script&gt;</span>
<span class="line"></span>
<span class="line"></span>
<span class="line"></span></code></pre><div class="line-numbers" aria-hidden="true" style="counter-reset:line-number 0;"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div>`,6)]))}const d=s(l,[["render",p]]),r=JSON.parse('{"path":"/plugins/payment.html","title":"扩展模块 - 支付","lang":"zh-CN","frontmatter":{},"headers":[],"git":{"updatedTime":1743829596000,"contributors":[{"name":"jiangtao","username":"jiangtao","email":"jiangtao.cctv@qq.com","commits":1,"url":"https://github.com/jiangtao"},{"name":"moon","username":"moon","email":"410518072@qq.com","commits":1,"url":"https://github.com/moon"}],"changelog":[{"hash":"5a3c78879d624561ecc88812252ba7802dc87ade","time":1743829596000,"email":"410518072@qq.com","author":"moon","message":"文档"},{"hash":"6c294b1869680043375f629e748f8ed02ee6e157","time":1742540963000,"email":"jiangtao.cctv@qq.com","author":"jiangtao","message":"支付渠道管理"}]},"filePathRelative":"plugins/payment.md"}');export{d as comp,r as data};
