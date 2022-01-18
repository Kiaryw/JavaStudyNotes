# Lambda
## 1.Intro
### 1.1 为什么增加Lambda？
多核CPU兴起，涉及锁的编程算法不但容易出错，并且还耗时，因此人们开发了`java.util.concurrent`包和很多第三方类库，但是效果不足够好。
举例来说，面对大型数据集合，Java还欠缺高效的并行操作。
为了编写这类处理批量数据的并行类库，需要在语言层面上修改现有Java：增加Lambda表达式。

面向对象编程是对数据进行抽象，而函数式编程是对行为进行抽象。是两种不同的思考方式。


### 1.2 什么是函数式编程？
核心在于：在思考问题时，使用**不可变值**和**函数**，函数对一个值进行处理，映射成另一个值。

## 2.Lambda
### 2.1 Lambda表达式
Java设计匿名内部类的目的是为了方便程序员将代码作为数据来传递。
下面是一个`Swing Button`的点击事件：
```java
button.addActionListener(new ActionListener() { 
       public void actionPerformed(ActionEvent event) {
            System.out.println("button clicked");
       }
});
```
上面的`ActionListener`是动作监听接口，动作处理需实现接口中的`actionPerformed`方法，一般的做法如上直接`new`一个匿名类，然后直接实现`actionPerformed`方法。

但是这么写，(1)代码冗繁；(2)可读性差，没有清晰地表达程序员的意图，上面的代码中，我们只是想传入「行为」，而不是「对象」。

所以使用Lambda表达式可以解决这两个问题：
```java
button.addActionListener(event -> System.out.println("Button clicked"));
```
上面的代码传入了一段代码块，event是参数名，->将参数和Lambda表达式的主体分开，而主体是用户点击按钮时会运行的代码。

在Lambda表达式中，无需指定类型，程序依然可以被编译，这是因为javac 根据程序的上下文（addActionListener 方法的签名）在后台推断出了参数event 的类型。这意味着如果参数类型不言而明，则无需显式指定。
    
                         

### 2.2 如何辨别Lambda表达式
#### 2.2.1 Lambda表达式不包含参数
`Lambda` 表达式不包含参数，使用空括号 `()` 表示没有参数。该 `Lambda` 表达式 实现了 `Runnable` 接口，该接口也只有一个 `run` 方法，没有参数，且返回类型为 `void`。

```java
Runnable noArguments = () -> System.out.println("Hello World");
```
#### 2.2.2 Lambda表达式包含一个参数
`Lambda` 表达式包含且只包含一个参数，可省略参数的括号。

```java
button.addActionListener(event -> System.out.println("button clicked"));
```
#### 2.2.3 Lambda表达式包含一段代码块
`Lambda` 表达式的主体不仅可以是一个表达式，而且也可以是一段代码块，使用大括号 `({})`将代码块括起来
```java
Runnable multiStatement = () -> {
    System.out.print("Hello");
    System.out.println(" World");
};
```
#### 2.2.4 Lambda表达式包含多个参数
这行代码并不是将两个数字相加，而是创建了一个函数，用来计算 两个数字相加的结果。变量 add 的类型是 BinaryOperator<Long>，它不是两个数字的和，而是将两个数字相加的那行代码。
```java
BinaryOperator<Long> add = (x, y) -> x + y;
```
#### 2.2.5 显式声明参数类型
所有 `Lambda` 表达式中的参数类型都是由编译器推断得出的。但有时最好也可以显式声明参数类型，此时就需要使用小括号将参数括起来，多个参数的情况也是如此。
```java
BinaryOperator<Long> addExplicit = (Long x, Long y) -> x + y;
```

Lambda表达式的类型依赖于上下文环境，是由编译器推断出来的。

### 2.3 引用值，而不是变量

在匿名内部类中如果需要引用它所在方法里的变量，在Java8之前，需要将变量声明为final，意味着该变量不能为其重复赋值，即使用该final变量时，其实是在使用赋给该变量的特定的值。Java8中放松了这一限制，可以使用非final变量，但是该变量在**既成事实上需要是final**的。虽然无需将变量声明为final，但是在Lambda表达式中，如果坚持用作非终态变量，编译器会报错。

换言之，**Lambda表达式引用的是值，而不是变量**。

```java
// 匿名内部类使用final局部变量
final String name = getUserName();
button.addActionListener(new ActionListener() {
  	public void actionPerformed(ActionEvent event) {
      	System.out.println("hi " + name);
    }
});

// Lambda表达式引用既成事实上的final变量
String name = getUserName();
button.addActionListener(event -> System.out.println("hi " + name));

// 下面未使用既成事实上的final变量，无法通过编译
String name = getUserName();
name = formatUserName(name);
button.addActionListener(event -> System.out.println("hi " + name));
```

### 2.4 函数接口

**函数接口**是**只有一个抽象方法的接口**，用作Lambda表达式的类型。

Lambda表达式的类型是什么？

如果一个接口中有多个抽象方法就不是函数接口，就不能用作 `Lambda` 表达式。多个抽象方法情况编译会直接报如下错误信息：

```java
Error:(373, 59) java: The target type of this expression must be a functional interface
```

`ActionListener`就是一个函数接口，接受ActionEvent类型参数，返回空。

```java
public interface ActionListener extends EventListener {
    public void actionPerformed(ActionEvent e);
}
```

`ActionListener` 只有一个抽象方法：`actionPerformed` ，被用来表示行为：接受一个参数，返回空。

记住，由于`actionPerformed` 定义在一个接口里，因此`abstract` 关键字不是必需的。该接口也继承自一个不具有任何方法的父接口：`EventListener` 。

可简单理解为可以把Lambda表达式，一组行为（函数接口的实现）传递给方法。以前想传递函数（行为），必须先将函数封装成对象的方法。然后传递改对象。Lambda表达式则可以直接传递函数（行为）。

#### Java中重要的函数接口


接口|参数|返回类型|说明|示例
:---|:---|:---|:---|:---
Predicate<T>|T|boolean|通过Lambda实现该接口中的test方法，返回一个布尔值，用作判断用|Predicate<Integer> boolValue = x -> x > 5;</br>System.out.println(boolValue.test(1));
Consumer<T>|T|void|通过Lambda实现该接口中的accept方法，不返回值，用于执行一些操作|Consumer<Integer> consumer = x -> System.out.println(x);</br>consumer.accept(123);
Function<T, R>|T|R|接受一个输入值T，处理后返回R类型数据|Function<Integer,Integer> function = t -> t+2;</br>System.out.println(function.apply(4));
Supplier<T>|None|T|类似于工厂方法，返回一个T类型的变量|Supplier<Integer> supplier = () -> 2;</br>Integer i = supplier.get();
UnaryOperator<T>|T|T|继承自接口`Function`，感觉和`Function`类似没看出什么差别|
BinaryOperator<T>|(T, T)|T|作用于于两个同类型操作符的操作，并且返回了操作符同类型的结果|BinaryOperator<Integer> binaryOperator = (x,y) -> x*y;</br>binaryOperator.apply(2,3);

### 2.5 类型推断

Lambda表达式中的类型推断，实际上是Java 7就引入的目标类型推断的扩展。Java 7中的菱形操作符`<>`就不需要明确声明泛型类型，编译器可以自己推断出来。

```java
// 菱形操作符，根据变量类型进行推断
Map<String, Integer> oldWordCounts = new HashMap<String, Integer>();
Map<String, Integer> newWordCounts = new HashMap<>();
```

如果将构造函数直接传递给一个方法，也可以根据方法签名来推断类型。

```java
// Java7中无法通过编译
userHashMap(new HashMap<>());
...
private void userHashMap(Map<String, String> values);
```

Java8中，程序员可以省略Lambda表达式中所有的参数类型。

javac**根据Lambda表达式上下文信息就能推断出参数的正确类型**，程序依然要经过类型检查来保证运行的安全性，但不再显式声明类型。这就是所谓的**类型推断**。

#### 详细的类型推断case

```java
// 1. Predicate
Predicate<Integer> atLeast5 = x -> x > 5;
// Predicate 也是一个Lambda表达式，它返回一个值。返回值就是Lambda表达式主体的值。

// 2. BinaryOperator 
// 该接口接受两个参数，返回一个值，参数和值的类型均相同。实例中所用的类型是Long。
BinaryOperator<Long> addLongs = (x, y) -> x + y;

// 3. 如果信息不足，类型推断系统也无法推断出类型。
// 比如下面的代码没有泛型，代码就无法通过编译。
BinaryOperator add = (x, y) -> x + y;

// 报错信息：“Operator '& #x002B;' cannot be applied to java.lang.Object, java.lang.Object.”
// 上面的例子中并没有给出变量add 的任何泛型信息，给出的正是原始 类型的定义。因此，编译器认为参数和返回值都是java.lang.Object 实例。

```























































