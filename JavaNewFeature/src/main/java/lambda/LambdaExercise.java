package lambda;

import javax.swing.text.DateFormatter;
import java.text.SimpleDateFormat;

public class LambdaExercise {
    /**
     * ThreadLocal Lambda表达式 。
     * Java有一个ThreadLocal 类，作为容器保存了当前线程里局部变量的值。
     * Java 8为该类新加了一个工厂方法，接受一个Lambda表达式，并产生一个新的ThreadLocal 对象，而不用使用继承，语法上更加简洁。
     *
     * Q: DateFormatter 类是非线程安全的。使用构造函数创建一个线程安全的DateFormatter 对象，并输出日期，如“01-Jan-1970”。
     *
     */
    public final static ThreadLocal<DateFormatter> formatter
            = ThreadLocal.withInitial(() -> new DateFormatter(new SimpleDateFormat("dd-MMM-yyyy")));


}
