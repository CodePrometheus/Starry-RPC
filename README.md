# Starry-RPC

🥚 设计一个功能较全的RPC框架

🌾 特点如下

- 基于BIO实现网络传输，运用原生Socket

- 实现Kyro，Json，Hessian以及Protobuf序列化机制

- 实现Gzip及Snappy对数据进行压缩

- 实现客户端调用远程服务时进行的负载均衡，如一致性Hash，轮询及随机负载均衡

- 使用Nacos，zookeeper和Redis作为注册中心，管理相关服务地址信息

- 使用CompletableFuture优化接受客户端返回结果，同时Netty重用Channel避免重复连接服务端

- Netty心跳机制，保证客户端和服务端连接不被断掉，避免重连

- 集成Spring通过注解进行注册服务，以及服务消费

- 利用SPI的扩展机制

- 客户端与服务端通信协议重新设计

- 增加服务版本号以及组号，处理一个接口有多个实现类的情况

- 服务提供侧实现服务自动注册
