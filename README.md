# Starry-RPC

🥚 设计一个功能较全的RPC框架

🌾 特点如下

- 基于BIO实现网络传输，运用原生Socket

- 实现Kyro，Json，Hessian以及Protobuf序列化机制

- 实现Gzip及Snappy对数据进行压缩

- 自定义Netty通信协议，客户端与服务端通信协议重新设计，防止粘包

- 实现客户端调用远程服务时进行的负载均衡，如一致性Hash，轮询及随机负载均衡

- 使用Nacos，Zookeeper和Redis作为注册中心，管理相关服务地址信息

- 使用CompletableFuture优化接受客户端返回结果，同时Netty重用Channel避免重复连接服务端

- Netty心跳机制，保证客户端和服务端连接不被断掉，避免重连

- 集成Spring通过注解进行注册服务，以及服务消费

- 利用SPI的扩展机制

- 增加服务版本号以及组号，处理一个接口有多个实现类的情况

- 服务提供侧实现服务自动注册

- 客户端重用相关服务地址信息列表

- 容错机制，如快速失败、失败自动切换和安全失败

- 自定义线程池拒绝策略




## 传输协议

Starry-RPC 协议

~~~json
* +---------------+---------------+-----------------+-------------+-------------+
* |  Magic Number |  Package Type | Serializer Type |Compress Type| Data Length |
* |    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |   4 bytes   |
* +---------------+---------------+-----------------+-------------+-------------+
* |                                    Data Bytes                               |
* |                                Length: ${Data Length}                       |
* +-----------------------------------------------------------------------------+
~~~

|      字段       |                             解释                             |
| :-------------: | :----------------------------------------------------------: |
|  Magic Number   |         魔数，标识一个 Starr-RPC 协议包，0xCAFEBABE          |
|  Package Type   |           包类型，标明这是一个调用请求还是调用响应           |
| Serializer Type |          序列化器类型，标明这个包的数据的序列化方式          |
|  Compress Type  |                           压缩类型                           |
|   Data Length   |                        数据字节的长度                        |
|   Data Bytes    | 传输的对象，通常是一个`RpcRequest`或`RpcClient`对象，取决于`Package Type`字段，对象的序列化方式取决于`Serializer Type`字段。 |
