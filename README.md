# WechatPayDemo

1、根据调用API必须遵循的协议规则，提交方式均采用post，提交和返回数据格式都为XML，根节点名为xml，字符统一采用UTF-8编码，签名算法使用MD5。

2、本次后台代码使用Java语言编写。由于微信支付要求传输方式必须采用https，如果已为Nginx服务器配置证书，只需再配置一下Nginx配置文件，在server参数下添加如下代码，让Nginx支持将.do和.jsp请求转发到后端Tomcat服务器上即可

```
location ~ (\.jsp)|(\.do)$ {  
         proxy_pass http://i-test.com.cn:80;  
         proxy_redirect off;  
         proxy_set_header Host $host;  
         proxy_set_header X-Real-IP $remote_addr;  
         proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;  
         client_max_body_size 10m;  
         client_body_buffer_size 128k;  
         proxy_connect_timeout 90;  
         proxy_send_timeout 90;  
         proxy_read_timeout 90;  
         proxy_buffer_size 4k;  
         proxy_buffers 4 32k;  
         proxy_busy_buffers_size 64k;  
         proxy_temp_file_write_size 64k;
}
```

3、如下图所示是微信官方给出的小程序支付业务流程时序图，基本流程如下：
![image](http://oih08wgjx.bkt.clouddn.com/images/blog/wxpay_develop_process/1.png)

1)小程序内发起支付请求，调用后台接口，同时将支付商品信息以及用户在小程序授权登录过后得到的code一起发送到后台服务器；  
2)后台通过code 去换取session_key和 openid，此处调用的微信接口如下：  

> https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code

参数说明：  
appid	小程序唯一标识  
secret	小程序的 app secret  
js_code	上面提到的登录时获取的 code  
grant_type	固定填写为 authorization_code  

3)服务器端调用支付统一下单接口，调用该接口会在微信支付服务后台生成预支付交易单，同时返回prepay_id，这个prepay_id就是预支付的ID。后面小程序调用支付需要用到它
这里调用的接口链接为：

> https://api.mch.weixin.qq.com/pay/unifiedorder

参数和说明太多，可以参考如下链接去详细了解：https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=9_1

这里做几点说明：  
a、nonce_str随机字符串字段由后台自己根据一定的策略生成；  
b、sign字段为签名字段，根据官方描述，签名的原则是先把所有要传的非空参数值的参数按照key=value的格式，并按照ASCII码从小到大排序，即key1=value1&key2=value2…拼接成字符串stringA，再用stringA拼接上key得到stringSignTemp字符串，并对stringSignTemp进行MD5运算，再将得到的字符串所有字符转换为大写，即可得到sign值。（key值设置路径：微信商户平台(pay.weixin.qq.com)-->账户设置-->API安全-->密钥设置）。

> 签名生成算法参见：https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=4_3  

c、需要把所有的参数封装为XML格式用post方式发送到统一下单接口  

4)将获取到的prepay_id以及生成的nonceStr等数据组装好返回给小程序，再由小程序去调起微信支付API  