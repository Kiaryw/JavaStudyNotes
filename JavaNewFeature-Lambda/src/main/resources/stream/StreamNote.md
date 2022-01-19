# Stream
**Stream（流）**，是用**函数式编程**在集合类上进行复杂操作的工具。可以说，Stream是Java 8依托于**函数式接口**和**Lambda表达式**这两个特性为集合类库做的一个类库。

它使用一种类似用 **SQL 语句**（Stream有类似SQL的聚合操作，比如`filter`, `map`, `reduce`, `find`, `match`, `sorted`等）从数据库查询数据的直观方式来提供一种对 Java 集合运算和表达的高阶抽象。

Stream不会像集合那样存储和管理元素，而是**按需计算**。

- 这里按需计算我的理解是，根据自己的需要，对Stream进行处理，Stream的来源可以是Collection，Array，I/O channel，generator等；
- Stream里的一些方法返回的Stream对象不是一个新的集合，而是一种**创建新集合的配方**，涉及到两个概念
  - **惰性求值方法**，是一种转换流的操作，比如像filter，map这种只描述Stream，最终不产生新集合的方法；
  - **及早求值方法**，是一种终结流的操作，比如像count，collect这种最终会从Stream产生值的方法。

- Stream流的中间操作会**返回流对象本身**，这样来多个操作就能够串联成pipelining，所以就能做一些优化操作，比如延迟操作和短路。
  - 延迟操作？
  - 短路？

- Stream还支持内部迭代，区别于Iterator和For-each这种显式的在集合外进行迭代的外部迭代方式，Stream通过**访问者模式（visitor）**实现内部迭代。
  - 访问者（visitor）
- Stream可以并行化操作，与迭代器只能串行化操作不同，使用Stream并行去遍历时，数据会被分成多个段，其中每一个都在不同的线程中处理，然后将结果一起输出。比如「map操作」。



## Stream的优点

### 1. 更加清晰的代码结构

Stream使用了Lambda表达式，在代码可读性上更强。举例来说：

```java
// code 1
List<Integer> list = Arrays.asList(1, 2, 3);
List<Integer> filterList = new ArrayList<>();

for (Integer i : list) {
  	if (i > 2 && i < 10 && (i % 2 == 0)) {
      	filterList.add(i);
    }
}

// code 2
List<Integer> list = Arrays.asList(1, 2, 3)
  												 .stream()
  												 .filter(i -> i > 2)
  												 .filter(i -> i < 10)
  												 .filter(i -> i % 2 == 0)
  												 .collect(Collector.toList());
```

上面两段代码做了同一件事，但是第二段代码的可读性明显更强，我们只需要关注「筛选条件」就够了，`filter`用来按照「筛选条件」过滤，`collect`用来将最终结果从`stream`中收集到`List`中。

并且第二段代码看似没有使用循环，是因为Stream会进行**隐式的循环**，即**「内部迭代」**。

### 2. 无需关心变量状态

Stream是被设计为**「不可变的」**，这个「不可变」有两层含义：

1. 每次Stream操作都会生成一个新的Stream，所以**Stream是不可变的**，和String类似。
2. 在Stream中只保存原集合的引用，所以在进行一些修改元素的操作时，是通过原元素生成一份新的元素，**Stream的任何操作都不会影响到原对象**。

简而言之，在Stream中，无需关心变量的状态，因为不会有操作原对象集合带来的副作用。

### 3. 延迟执行和优化

Stream只有在遇到终结流的操作（及早求值方法）的时候才会执行。

```java
Arrays.asList(1, 2, 3).stream()
											.filter(i -> i > 2)
											.peek(System.out::println);
// 因为filter和peek方法都是转换流的惰性求值方法，所以不会触发执行

Arrays.asList(1, 2, 3).stream()
											.filter(i -> i > 2)
											.peek(System.out::println)
  										.count();
// count是一个及早求值方法，计算出Stream中的元素个数，返回Long值。

```

Stream这种没有终结操作就不执行的特性被称为**延迟执行**。

与此同时，Stream还会对API中的无状态方法进行名为**循环合并**的优化。



## 创建流

### 1. 通过Stream接口创建

```java
// Stream.of(T... values)

Stream<Integer> integerStream = Stream.of(1, 2, 3);

Stream<Double> doubleStream = Stream.of(1.1d, 2.2d, 3.3d);

Stream<String> stringStream = Stream.of("1", "2", "3");

Stream<Object> empty = Stream.empty();

// 创建一个无限元素数量的Stream，
// generate方法从方法参数上来看，它接受一个函数式接口——Supplier作为参数，这个函数式接口是用来创建对象的接口，你可以将其类比为对象的创建工厂，Stream将从此工厂中创建的对象放入Stream中

Stream<String> generate = Stream.generate(() -> "Supplier");

Stream<Integer> generateInteger = Stream.generate(() -> 123);

```

### 2. 通过集合类库进行创建

```java
Stream<Integer> integerStreamList = Arrays.asList(1, 2, 3).stream();
        
Stream<String> stringStreamList = Arrays.asList("1", "2", "3").stream(); 

//stream() 方法本质上还是通过调用一个Stream工具类来创建Stream
default Stream<E> stream() {
    return StreamSupport.stream(spliterator(), false);
}

```



## 并行流

在某些场景下，为了最大化压榨多核CPU的性能，我们可以使用并行流，它通过JDK7中引入的`fork/join`框架来执行并行操作，我们可以通过如下方式创建并行流：

```java
Stream<Integer> integerParallelStream = Stream.of(1, 2, 3).parallel();

Stream<String> stringParallelStream = Stream.of("1", "2", "3").parallel();

Stream<Integer> integerParallelStreamList = List.of(1, 2, 3).parallelStream();

Stream<String> stringParallelStreamList = List.of("1", "2", "3").parallelStream();
```

在Stream的静态方法中没有直接创建并行流的方法，我们需要在构造Stream后再调用一次`parallel()`方法才能创建并行流，因为调用`parallel()`方法并不会重新创建一个并行流对象，而是在原有的Stream对象上面设置了一个并行参数。

```java
default Stream<E> stream() {
    return StreamSupport.stream(spliterator(), false);
}

default Stream<E> parallelStream() {
    return StreamSupport.stream(spliterator(), true);
}
```

一般情况下并不需要并行流，除非Stream中的元素过千，因为将元素分散到不同的CPU中计算也有开销。

并行的好处是充分利用多核CPU的性能，但是使用中往往要对数据进行分割，如果数据的数据结构是链表或者Hash这种不易切割的结构，切割起来的效率就并不高。所以只有当Stream中的元素数量足够多，并行流才能带来比较明显的性能提升。

另外，可以使用`sequential()`将并行流转换为串行流。

```java
Stream.of(1, 2, 3).paralle().sequential();
```

## 连接流

`contact()`可以将两个Stream连接在一起使用。

```java
Stream<Integer> contact = Stream.contact(Stream.of(1, 2, 3), Stream.of(4, 5, 6));

//如果是两种不同的泛型流进行组合，自动推断会自动的推断出两种类型相同的父类
Stream<Integer> integerStream = Stream.of(1, 2, 3);
Stream<String> stringStream = Stream.of("1", "2", "3");
Stream<? extends Serializable> stream = Stream.concat(integerStream, stringStream);
```



## Stream 操作

Stream操作

- 转换操作
  - 无状态方法：此方法的执行无需依赖前面方法执行的结果集。
    - **map()**：此方法的参数是一个Function对象，它可以使你对集合中的元素做自定义操作，并保留操作后的元素。
    - **filter()**：此方法的参数是一个Predicate对象，Predicate的执行结果是一个Boolean类型，所以此方法只保留返回值为true的元素，正如其名我们可以使用此方法做一些筛选操作。
    - **flatMap()**：此方法和map()方法一样参数是一个Function对象，但是此Function的返回值要求是一个Stream，该方法可以将多个Stream中的元素聚合在一起进行返回。
    - **peek()**：peek方法接受一个Consumer对象做参数，这是一个无返回值的参数，我们可以通过peek方法做些打印元素之类的操作。
  - 有状态方法
- 终结操作
  - 终结方法



### 基础类型Stream

在Stream的无状态方法中还有几个和map()与flatMap()对应的方法，它们分别是：

- `mapToInt`，返回值为**IntStream**

- `mapToLong`，返回值为**LongStream**

- `mapToDouble`，返回值为**DoubleStream**

- `flatMapToInt`，返回值为**IntStream**

- `flatMapToLong`，返回值为**LongStream**

- `flatMapToDouble`，返回值为**DoubleStream**



### 无状态方法

#### 循环合并

```java
List<Integer> list = List.of(1, 2, 3).stream()
												 .map(i -> i * 10)
                				 .filter(i -> i < 10)
                				 .filter(i -> i % 2 == 0)
                				 .collect(toList());
```

回顾无状态方法的定义，可以发现其他这三个条件可以放在一个循环里面做，因为filter只依赖map的计算结果，而不必依赖map执行完后的结果集，所以只要保证先操作map再操作filter，它们就可以在一次循环内完成，这种优化方式被称为`循环合并`。

**所有的无状态方法都可以放在同一个循环内执行，它们也可以方便的使用并行流在多个CPU上执行。**

### 有状态方法

| 方法名                           | 方法结果                                                     |
| -------------------------------- | ------------------------------------------------------------ |
| `distinct()`                     | 元素去重。                                                   |
| `sorted()`                       | 元素排序，重载的两个方法，需要的时候可以传入一个排序对象。   |
| `limit(long maxSize)`            | 传入一个数字，代表只取前X个元素。                            |
| `skip(long n)`                   | 传入一个数字，代表跳过X个元素，取后面的元素。                |
| `takeWhile(Predicate predicate)` | JDK9新增，传入一个断言参数当第一次断言为false时停止，返回前面断言为true的元素。 |
| `dropWhile(Predicate predicate)` | JDK9新增，传入一个断言参数当第一次断言为false时停止，删除前面断言为true的元素。 |





以上就是所有的有状态方法，它们的方法执行都必须依赖前面方法执行的结果集才能执行，比如排序方法就需要依赖前面方法的结果集才能进行排序。

同时`limit`方法和`takeWhile`是两个短路操作方法，这意味效率更高，因为可能内部循环还没有走完时就已经选出了我们想要的元素。

所以有状态的方法不像无状态方法那样可以在一个循环内执行，每个有状态方法都要经历一个单独的内部循环，所以编写代码时的顺序会影响到程序的执行结果以及性能。



## 聚合方法

聚合方法代表着整个流计算的最终结果，所以返回值都不是`Stream`，但是返回值有可能是空，比如使用`filter`但是没有匹配到元素，JDK 8中用`Optional`来规避`NullPointerException`，聚合方法都会调用`evaluate`方法，用于判定一个方法是否是聚合方法。



#### 分类

- 简单聚合

  - `count`
  - `forEach`
  - `forEachOrdered`
  - `anyMatch`
  - `allMatch`
  - `noneMatch`
  - `findFirst`
  - `findAny`

  

- 归约

  - `reduce`
  - `max`
  - `min`

- 收集器

  - `collect`



### 简单聚合

| 简单聚合方法                     | 解释                                                         |
| -------------------------------- | ------------------------------------------------------------ |
| `count()`                        | 返回Stream中元素的size大小。                                 |
| `forEach()`                      | 通过内部循环Stream中的所有元素，对每一个元素进行消费，此方法没有返回值。 |
| `forEachOrdered()`               | 和上面方法的效果一样，但是这个可以保持消费顺序，哪怕是在多线程环境下。 |
| `anyMatch(Predicate predicate)`  | 这是一个短路操作，通过传入断言参数判断是否有元素能够匹配上断言。 |
| `allMatch(Predicate predicate)`  | 这是一个短路操作，通过传入断言参数返回是否所有元素都能匹配上断言。 |
| `noneMatch(Predicate predicate)` | 这是一个短路操作，通过传入断言参数判断是否所有元素都无法匹配上断言，如果是则返回true，反之则false。 |
| `findFirst()`                    | 这是一个短路操作，返回Stream中的第一个元素，Stream可能为空所以返回值用Optional处理。 |
| `findAny()`                      | 这是一个短路操作，返回Stream中的任意一个元素，串型流中一般是第一个元素，Stream可能为空所以返回值用Optional处理。 |



### 归约

**归约**：将一个Stream中的所有元素反复结合起来，得到一个结果，这样的操作被称为归约。

#### reduce

```java
Optional<Integer> reduce = List.of(1, 2, 3)
																		.stream()
                										.reduce((i1, i2) -> i1 + i2);

// 另一种写法，用方法引用代表Lambda表达式，看着优雅点点
Optional<Integer> reduce = List.of(1, 2, 3)
  															.stream()
																.reduce(Integer::sum);
```

归约是两两的元素进行处理然后得到一个最终值，所以`reduce`的方法的参数是一个二元表达式，它将两个参数进行任意处理，最后得到一个结果，其中它的参数和结果必须是同一类型。返回值是`Optional`的，这是预防Stream没有元素的情况。也可以使用reduce提供的重载方法，增加一个初始值参数0，来避免没有返回值的情况：

```java
Integer reduce = List.of(1, 2, 3)
											.stream()
											.reduce(0, (i1, i2) -> i1 + i2);
```

在实际方法运行中，初始值会在第一次执行中占据i1的位置，i2则代表Stream中的第一个元素，然后所得的和再次占据i1的位置，i2代表下一个元素。

#### max 和 min

`max` 和 `min` 方法也是归约方法，直接调用了reduce方法。



### 收集器

##### Collect

- 收集

  - `toCollection`
  - `toList`
  - `toUnmodifiableList`
  - `toSet`
  - `toUnmodifiableSet`
  - `toMap`
  - `toConcurrentMap`
  - `toUnmodifiableMap`

  ```java
  // toList
  List.of(1, 2, 3).stream().collect(Collectors.toList());
  
  // toUnmodifiableList
  List.of(1, 2, 3).stream().collect(Collectors.toUnmodifiableList());
  
  // toSet
  List.of(1, 2, 3).stream().collect(Collectors.toSet());
  
  // toUnmodifiableSet
  List.of(1, 2, 3).stream().collect(Collectors.toUnmodifiableSet());
  
  // toMap
  // 第一个参数代表key，它表示你要设置一个Map的key，我这里指定的是元素中的orderNo。
  // 第二个参数代表value，它表示你要设置一个Map的value，我这里直接把元素本身当作值，所以结果是一个Map<String, Order>。
  List<Order> orders = List.of(new Order(), new Order());
  Map<String, Order> map = orders.stream()
                  							 .collect(Collectors.toMap(Order::getOrderNo, order -> order));
  // toMap()虽然强大，但是如果生成Map时指定的key出现重复会抛出IllegalStateException
  // toMap() 还有两个伴生方法：
  // toUnmodifiableMap()：返回一个不可修改的Map。
  // toConcurrentMap()：返回一个线程安全的Map。
  
  ```

  

- 分组

  - `groupingBy`

  如果你想对数据进行分类，但是你指定的key是可以重复的，那么你应该使用groupingBy 而不是toMap。

  ```java
  List<Order> orders = List.of(new Order(), new Order());
  Map<Integer, List<Order>> collect = orders.stream()
                  .collect(Collectors.groupingBy(Order::getOrderType));
  ```

  groupingBy还提供了一个重载，让你可以自定义收集器类型，所以它的第二个参数是一个Collector收集器对象。

  ```java
  List<Order> orders = List.of(new Order(), new Order());
  Map<Integer, Set<Order>> collect = orders.stream()
                  .collect(Collectors.groupingBy(Order::getOrderType, toSet()));
  ```

  

- 分区（将数据按照TRUE或者FALSE进行分组就叫做分区。）

  - `partitioningBy`

  ```java
  // 将一个订单集合按照是否支付进行分组，这就是分区
  List<Order> orders = List.of(new Order(), new Order());
  Map<Boolean, List<Order>> collect = orders.stream()
                  .collect(Collectors.partitioningBy(Order::getIsPaid));        
  
  ```

  

- 其他

  - `mapping`
  - `filtering`
  - `flatMapping`
  - `counting`
  - `reducing`
  - `minBy`
  - `maxBy`
  - `joining`



















