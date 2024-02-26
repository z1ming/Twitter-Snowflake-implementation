# Twitter-Snowflake-implementation
Twitter(X) 雪花算法的 Java 和 Go 实现

```
/**
 * 0-00000000000000000000000000000000000000000-0000000000-000000000000
 * | |_______________________________________| |________| |__________|
 * 1               41                              10          12
 * 符号            时间戳                      5数据中心 + 5机器  序列号
 */
```

# 参考

1. [Twitter Snowflake](https://github.com/twitter-archive/snowflake)
2. [Leaf——美团点评分布式ID生成系统](https://tech.meituan.com/2017/04/21/mt-leaf.html)
2. [百度 UidGenerator](https://github.com/baidu/uid-generator/blob/master/README.zh_cn.md)
3. [滴滴 Tinyid](https://github.com/didi/tinyid)
