# Stream
**Stream（流）**，是用**函数式编程**在集合类上进行复杂操作的工具。

使用一种类似用 **SQL 语句**（Stream有类似SQL的聚合操作，比如`filter`, `map`, `reduce`, `find`, `match`, `sorted`等）从数据库查询数据的直观方式来提供一种对 Java 集合运算和表达的高阶抽象。

Stream不会像集合那样存储和管理元素，而是**按需计算**。

- 这里按需计算我的理解是，根据自己的需要，对Stream进行处理，Stream的来源可以是Collection，Array，I/O channel，generator等；
- Stream里的一些方法返回的Stream对象不是一个新的集合，而是一种**创建新集合的配方**，涉及到两个概念
  - **惰性求值方法**，比如像filter这种只描述Stream，最终不产生新集合的方法；
  - **及早求值方法**，比如像count这种最终会从Stream产生值的方法。

- Stream流的中间操作会**返回流对象本身**，这样来多个操作就能够串联成pipelining，所以就能做一些优化操作，比如延迟操作和短路。
  - 延迟操作？
  - 短路？

- Stream还支持内部迭代，区别于Iterator和For-each这种显式的在集合外进行迭代的外部迭代方式，Stream通过**访问者模式（visitor）**实现内部迭代。
  - 访问者（visitor）
- Stream可以并行化操作，与迭代器只能串行化操作不同，使用Stream并行去遍历时，数据会被分成多个段，其中每一个都在不同的线程中处理，然后将结果一起输出。比如「map操作」。