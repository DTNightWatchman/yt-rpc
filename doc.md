# YT-RPC

rpc定义：远程过程调用，是一种计算机通信协议，允许程序在不同的计算机之间进行通信，像本地调用一样调用远程。

## 实现思路
基本设计，首先是需要有服务消费者和服务提供者两个角色：
![img.png](assets/img.png)



消费者想要调用提供者，就需要提供者启动一个web服务，然后通过请求客户端发送HTTP或者其他协议的请求来调用

比如请求 yt/order 地址之后就会调用 orderService 的 order 方法：

![image-20240626162703273](assets/image-20240626162703273.png)

但是如果提供者提供了多个服务和方法，每个接口和方法都需要单独写一个接口，消费者对每个接口单独写一个HTTP调用的逻辑是不合适的

可以提供一个统一的服务调用接口，通过请求处理器，根据客户端的请求参数来进行不同的处理，调用不同的服务和方法

可以在服务提供者程序维护一个本地服务注册器，记录服务和对应实现类的映射

比如，消费者要调用orderService的order方法，可以发送一个请求，参数是 service=orderService.method=order，然后请求处理器会根据service从服务注册器中找到对应的服务实现类，并且通过Java的反射机制调用method指定的方法。

![image-20240626163049381](assets/image-20240626163049381.png)

需要注意的是，由于Java对象无法直接在网络中传输，所以要对传输的参数进行序列化和反序列化。

![image-20240626163127987](assets/image-20240626163127987.png)为了简化消费者发请求的代码，实现类似本地调用的体验，可以基于代理模式，为消费者生成一个代理对象，由代理对象完成请求和响应的过程。

所谓代理，就是有人帮你做一些事情，不需要自己去做。

所以就能够绘制出一个简易的RPC框架图：

![image-20240626163311861](assets/image-20240626163311861.png)

虚线部分就是RPC框架需要提供的模块和能力

### 拓展实现

#### 1. 服务注册发现

消费者如何知道提供者的调用地址？

需要一个注册中心，来保存服务提供者的地址，消费者要调用服务时，只需要从注册中心获取到服务提供者地址即可。

架构图：

![image-20240626163549757](assets/image-20240626163549757.png)

一般可以使用现成的第三方注册中心，比如Redis、Zookeeper即可。

#### 2. 负载均衡

如果有多个服务提供者，消费者应该调用哪个服务提供者呢

可以给服务调用方增加负载均衡的能力，通过指定不同的算法来决定调用哪一个服务提供者，比如轮询、随机、根据性能动态调用等。

架构图如下：

![image-20240626164839123](assets/image-20240626164839123.png)

#### 3. 容错机制

如果服务调用失败应该如何处理，为了保证分布式系统的高可用，通常会给服务的调用增加一定的容错机制，比如失败重试，降级调用其他接口等。

架构图如下：

![image-20240626165240190](assets/image-20240626165240190.png)



#### 4. 其他需要考虑的

1. 服务提供者下线了，需要一个接口剔除机制
2. 服务消费者每次都从注册中心拉取信息，性能可能较差，可以使用缓存来优化性能
3. 如何优化RPC框架的传输性能：选择合适的网络框架，自定义协议头，节约传输体积等
4. 如何让整个框架易于拓展，可以使用Java的SPI机制，配置化等

## 简易实现

架构图：

![image-20240626170502622](assets/image-20240626170502622.png)

> 注意：不同的web服务器对应的请求处理方式也不相同，比如说Vert.x中是通过 `Handler<HttpServerRequest>` 接口来自定义请求处理器的，并且可以通过 request.bodyHandler 异步处理请求

![image-20240709155917385](assets/image-20240709155917385.png)

此处引入自定义的请求拦截器，这样就已经引入了RPC框架的服务提供者模块，已经能够接受请求并完成服务调用了。

然后是在消费方发起调用，调用这里可以使用代理实现的方式来进行调用，使用静态代理实现：

```java
package com.yt.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.yt.example.common.model.User;
import com.yt.example.common.service.UserService;
import com.yt.ytrpc.model.RpcRequest;
import com.yt.ytrpc.model.RpcResponse;
import com.yt.ytrpc.serializer.JdkSerializer;
import com.yt.ytrpc.serializer.Serializer;

import java.io.IOException;

public class UserServiceProxy implements UserService {
    @Override
    public User getUser(User user) {
        Serializer serializer = new JdkSerializer();

        // 发送请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();

        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(bodyBytes).execute();) {
                result = httpResponse.bodyBytes();
            }

            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);

            return (User) rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
```

![image-20240711093846448](assets/image-20240711093846448.png)

这里使用静态代理实现简单，容易实现，但是需要给每一个服务都单独写一个实现类，非常麻烦，灵活性差

所以一般在RPC框架中是使用动态代理：

动态代理的作用是根据要生成的对象的类型，自动生成一个代理对象

> 常用的动态代理实现方式有 JDK 动态代理和基于字节码生成的动态代理（比如 CGLIB）。前者简单易用、无需引入额外的库，但缺点是只能对接口进行代理；后者更灵活、可以对任何类进行代理，但性能略低于 JDK 动态代理。
