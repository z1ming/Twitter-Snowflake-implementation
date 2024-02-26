# 实现 Twitter Snowflake 算法（Java，Go）

Twitter 的雪花算法使用 Scala 实现[1]，该项目使用 Java 和 GO 对其进行了实现。

## Twitter Snowflake 算法思想

采用 64 位长整形实现，划分为四个区域：

- 符号，占用 1 位
- 毫秒级时间戳，占用 41 位
- 5 位数据中心 + 5 位机器 ID，占用 10 位
- 序列号，占用 12 位

```
 +------+--------------------+-----------------------+----------+
 | sign | delta milliSeconds | dataCenter | workerId | sequence |
 +------+--------------------+-----------------------+----------+
   1bit        41bits                  10bits           12bits
```
## 贡献

如果您有任何问题，欢迎提交 merge request 或 issue。
