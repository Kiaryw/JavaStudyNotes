# Class Library 类库

Java 8 引入了**默认方法**和**接口的静态方法**，接口方法也可以包含代码体了。

## 1. 在代码中使用Lambda表达式

```java
// Case

// 使用isDebugEnable方法降低日志性能开销
Logger logger = new Logger();
if (logger.isDebugEnable()) {
  	lgger.debug("Look at this: " + expensiveOperation());
}

// 重写代码：使用Lambda简化（传入Lambda表达式，生成一条用作日志信息的字符串，debug级别以上时才执行）
Logger logger = new Logger();
logger.debug(() -> "Look at this: " + expensiveOperation());


```

## 2. 基本类型

在Java中，有一些相伴的类型，比如`int` 和`Integer` ——前者是**基本类型**，后者是**装箱类型** 。基本类型是建在语言和运行环境中，是基本的程序构建模块；而装箱类型属于普通的`Java`类，只不过是对基本类型的一种封装。

Java的泛型是基于对泛型参数类型的擦除 ——换句话说，假设它是Object 对象的实例——因此只有装箱类型才能作为泛型参数。 这就解释了为什么在 `Java` 中想要一个包含整型值的 列表 `List<int>`，实际上得到的却是一个包含整型对象的列表 `List<Integer>`。

`Java` 的泛型在编译器有效，在运行期被删除，也就是说所有泛型参数类型在编译后都会被清除掉，看下面一个列子，代码如下：

```java
public class Foo {  
    public void listMethod(List<String> stringList){  
    }  
    public void listMethod(List<Integer> intList) {  
    }  
}

// 报错
// 'listMethod(List<String>)' clashes with 'listMethod(List<Integer>)'; both methods have same erasure
// 此错误的意思是说listMethod(List<String>) 方法在编译时擦除类型后的方法是listMethod(List<E>)，它与另外一个方法重复，也就是方法签名重复。
```

由于装箱类型是**对象**，因此在内存中存在额外开销。比如，整型在内存中占用 `4` 字节，整型对象却要占用 `16` 字节。这一情况在数组上更加严重，整型数组中的每个元素 只占用基本类型的内存，而整型对象数组中，每个元素都是内存中的一个指针，指向 `Java` 堆中的某个对象。在最坏的情况下，同样大小的数组，`Integer[]` 要比 `int[]` 多占用 `6` 倍内存。

将基本类型转换为装箱类型，称为**装箱**，反之则称为**拆箱**，两者都需要额外的计算开销。 对于需要大量数值运算的算法来说，装箱和拆箱的计算开销，以及装箱类型占用的额外内 存，会明显减缓程序的运行速度。

为了减小这些性能开销，Stream 类的某些方法对基本类型和装箱类型做了区分。在Java 8中，仅对整型、长整型和双浮点型做了特殊处理，因为它们在数值计算中用得最多，特殊处理后的系统性能提升效果最明显。
