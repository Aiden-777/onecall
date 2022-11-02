# OneCall 后端

EasyTron 的简单实践，功能：

1.实时同步最新区块，并通过ws广播给所有监听用户

2.实现对某一个地址进行 trc20 、 trx的实时转账监听

要扩展trc10监听只需要在Spring容器中添加 trx 转账处理器，十分简单。

---

Tron区块链依赖：[EasyTron](https://github.com/Aiden-777/EasyTron)、[trident](https://github.com/tronprotocol/trident)

需要额外服务：redis - 缓存最近区块信息

运行前请在 application.yml 配置 [apiKey](https://cn.developers.tron.network/reference/apikey)

Donate：TTTTTtczA5UZM65QJpncXUsH8KwgJTHyXw