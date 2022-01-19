# HTTP 

## HTTP Request Methods

- **GET**：请求一个指定资源的表示形式，使用GET的请求应该只被用于获取数据。
- **HEAD**：请求一个与GET请求的响应相同的响应，但没有响应体。
- **POST**：用于将实体提交到指定的资源，通常导致在服务区上的状态变化或副作用。
- **PUT**：用请求有效载荷（request payload）替换目标资源的所有当前表示。
- **DELETE**：用于删除指定资源。
- **CONNECT**：用于建立一个到由目标资源标识的服务器的隧道
- **OPTIONS**：用于描述目标资源的通信选项。
- **TRACE**：沿着到目标资源的路径执行一个消息环回测试。
- **PATCH**：用于对资源应用部分修改。

> PUT和PATCH的区别？
>
> PATCH是对PUT的补充，用来对已知资源来进行**局部**更新。
>
> PUT是用请求有效荷载(request payload)来替换目标资源的**所有**当前表示，这种更新资源的场景下，要求前端提供一个完整的资源对象。但是如果需求不要求更新完整的资源对象，而是只需要更新部分资源（比如说一条记录中的某个字段），使用PUT就会造成带宽浪费的问题以及在API中暴露本不需要修改的字段的安全风险。所以使用PATCH来进行局部更新。

### 幂等性

一个HTTP方法是**幂等**的，指的是同样的请求被执行一次和连续执行多次的效果是一样的，服务器的状态也是一样的。换言之，幂等方法**不应该具有副作用**。

幂等性只与后端服务器的实际状态有关，每一次请求接收到的状态码不一定相同。比如`DELETE`方法第一次执行可能返回`200`，但是之后可能返回`404`。

正确实现的条件下，`GET`，`PUT`，`DELETE`等方法都是幂等的，`POST`方法不是幂等的。

**所有Safe方法都是幂等的，但不是所有幂等方法都是安全的。**

- 一个HTTP方法是**安全（Safe）**的，指的是这个方法不会修改服务器的数据，即该方法对服务器是只读的。

- 这些方法是安全的：`GET`，`HEAD`，`OPTIONS`。

  

## HTTP Security

### HTTP Cookies

**HTTP Cookie**：服务器发送到用户浏览器并保存在本地的一小块数据。浏览器会存储cookie并在之后的请求中将cookie一起发回给同一个服务器。通常来说，HTTP cookie用来判断两个请求是否来自同一个浏览器，即保持用户登录状态。它为「无状态（stateless）」的HTTP protocol 记录了「有状态（stateful）信息」。



#### Purposes

- Session状态管理（登录状态，购物车，游戏分数或其他server需要记录的东西）
- 个性化设置（用户偏好，主题，或其他设置）
- Tracking（记录或分析用户行为）



### Creating cookies

当服务器收到 HTTP 请求时，服务器可以在响应头里面添加一个`Set-Cookie`选项。浏览器收到响应后通常会保存下 Cookie，之后对该服务器每一次请求中都通过 `Cookie`请求头部将 Cookie 信息发送给服务器。另外，Cookie 的过期时间、域、路径、有效期、适用站点都可以根据需要来指定。

```json
Set-Cookie: <cookie-name>=<cookie-value>
```

HTTP Response 的`Set-Cookie`告诉客户端存储cookies，后续对服务器的请求，浏览器都会使用先前保存的cookies来作为Cookie header来传回给服务器。



### Lifetime of a Cookie

- Session Cookie：当前session结束后被删除。浏览器定义「current session」何时结束，某些浏览器重启后会恢复cookie，这会导致session cookie永久持续。

- Permanent Cookie在`Expires`属性指定的日期被删除，或者在`Max-Age`属性指定的一段时间后被删除。

  ```json
  Set-Cookie: id=a3fWa; Expires=Thu, 31 Oct 2021 07:28:00 GMT;
  ```

  

### Restrict Access To Cookie

通过`Secure`和`HttpOnly`属性确保cookies被安全发送并不会被预期意外的第三方或者脚本访问。

标记为 `Secure` 的 Cookie 只应通过被 HTTPS 协议加密过的请求发送给服务端，因此可以预防 man-in-the-middle 攻击者的攻击。但即便设置了 `Secure` 标记，敏感信息也不应该通过 Cookie 传输，因为 Cookie 有其固有的不安全性，`Secure` 标记也无法提供确实的安全保障, 例如，可以访问客户端硬盘的人可以读取它。

具有`HttpOnly`属性的cookie对JavaScript是不可访问的，只能被发送给服务器。例如，持久化服务器端会话的 Cookie 不需要对 JavaScript 可用。

```java
Set-Cookie: id=a3fWa; Expires=Thu, 21 Oct 2021 07:28:00 GMT; Secure; HttpOnly
```

### Where Cookies Are Sent

`Domain`和`Path`属性定义了cookie的作用域，即cookie可以被发给哪些URL。

#### Domain

`Domain` 指定了哪些主机可以接受 Cookie。如果不指定，默认为origin，不包含子域名。如果指定了`Domain`，则一般包含子域名。因此，指定 `Domain` 比省略它的限制要少。但是，当子域需要共享有关用户的信息时，这可能会有所帮助。 

#### Path

`Path` 标识指定了主机下的哪些路径可以接受 Cookie（该 URL 路径必须存在于请求 URL 中）。以字符 `%x2F` ("/") 作为路径分隔符，子路径也会被匹配。

#### SameSite

`SameSite` Cookie 允许服务器要求某个 cookie 在跨站请求时不会被发送，（其中  [Site (en-US)](https://developer.mozilla.org/en-US/docs/Glossary/Site) 由可注册域定义），从而可以阻止跨站请求伪造攻击（[CSRF](https://developer.mozilla.org/zh-CN/docs/Glossary/CSRF)）。