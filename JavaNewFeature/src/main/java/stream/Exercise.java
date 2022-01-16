package stream;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Exercise {
    /**
     * 1.常用流操作 。实现如下函数：
     *
     * a. 编写一个求和函数，计算流中所有数之和。例如，int addUp(Stream<Integer> numbers) ；
     * b. 编写一个函数，接受艺术家列表作为参数，返回一个字符串列表，其中包含艺术家的姓名和国籍；
     * c. 编写一个函数，接受专辑列表作为参数，返回一个由最多包含3首歌曲的专辑组成的列表。
     *
     */
    public static int addUp(Stream<Integer> numbers) {
        return numbers.reduce(0, Integer::sum);
    }

    public static List<String> getNamesAndNationalities(List<Artist> artists) {
        return artists.stream()
                      .flatMap(artist -> Stream.of(artist.getName(), artist.getNationality()))
                      .collect(Collectors.toList());
    }

    public static List<Album> getAlbumsWithAtMost3Songs(List<Album> albums) {
        return albums.stream()
                     .filter(album -> album.getTracks().size() <= 3)
                     .collect(Collectors.toList());
    }

    /**
     * 2. 迭代 。修改如下代码，将外部迭代转换成内部迭代。
     *
     * 原来的代码：
     * int totalMembers = 0;
     * for (Artist artist : artists) {
     *     Stream<Artist> members = artist.getMembers();
     *     totalMembers += members.count();
     * }
     */
    public static int countBandMembersInternal(List<Artist> artists) {
//        return artists.stream()
//                .map(artist -> artist.getMembers().size())
//                .reduce(0, Integer::sum);

        return (int) artists.stream()
                .flatMap(artist -> artist.getMembers().stream()).count();
    }


    /**
     * 3. 求值 。根据Stream 方法的签名，判断其是惰性求值还是及早求值。
     *
     * a. boolean anyMatch(Predicate<? super T> predicate);
     * b. Stream<T> limit(long maxSize);
     *
     * Answer:
     * a. 及早求值，Eager
     * b. 惰性求值，Lazy
     */

    /**
     * 4. 判断一个函数是否是高阶函数，主要看其是否使用一个function作为argument。
     * e.g.
     *  a. boolean anyMatch(Predicate<? super T> predicate); (yes)
     *  b. Stream<T> limit(long maxSize); (no)
     */

    /**
     * 5. 纯函数 。下面的Lambda表达式有无副作用，或者说它们是否更改了程序状态？
     * a. x -> x+1 (side effect free)
     * b. AtomicInteger count = new AtomicInteger(0);
     *    List<String> origins = albums.musicians.forEach(musician -> count.incAndGet();)
     *    (Mutates count)
     */


    /**
     * 6. 计算一个字符串中小写字母的个数（提示：参阅String 对象的chars 方法）。
     */
    public static int countLowercaseLetters(String string) {
        return (int) string.chars()
                           .filter(Character::isLowerCase)
                           .count();
    }

    /**
     * 7. 在一个字符串列表中，找出包含最多小写字母的字符串。对于空列表，返回Optional<String> 对象。
     */
    public static Optional<String> mostLowercaseString(List<String> strings) {
        return strings.stream()
                      .max(Comparator.comparing(Exercise::countLowercaseLetters));
    }

    /**
     * 8. 只用reduce 和Lambda表达式写出实现Stream 上的map 操作的代码，如果不想返回Stream ，可以返回一个List 。
     */
    public static <I, O> List<O> map(Stream<I> stream, Function<I, O> mapper) {
        return stream.reduce(new ArrayList<O>(), (acc, x) -> {
            // We are copying data from acc to new list instance. It is very inefficient,
            // but contract of Stream.reduce method requires that accumulator function does
            // not mutate its arguments.
            // Stream.collect method could be used to implement more efficient mutable reduction,
            // but this exercise asks to use reduce method.
            List<O> newAcc = new ArrayList<>(acc);
            newAcc.add(mapper.apply(x));
            return newAcc;
        }, (List<O> left, List<O> right) -> {
            // We are copying left to new list to avoid mutating it.
            List<O> newLeft = new ArrayList<>(left);
            newLeft.addAll(right);
            return newLeft;
        });
    }

    /**
     * 9. 只用reduce 和Lambda表达式写出实现Stream 上的filter 操作的代码，如果不想返回Stream ，可以返回一个List 。
     */

    public static <I> List<I> filter(Stream<I> stream, Predicate<I> predicate) {
        List<I> initial = new ArrayList<>();
        return stream.reduce(initial,
                (List<I> acc, I x) -> {
                    if (predicate.test(x)) {
                        // We are copying data from acc to new list instance. It is very inefficient,
                        // but contract of Stream.reduce method requires that accumulator function does
                        // not mutate its arguments.
                        // Stream.collect method could be used to implement more efficient mutable reduction,
                        // but this exercise asks to use reduce method explicitly.
                        List<I> newAcc = new ArrayList<>(acc);
                        newAcc.add(x);
                        return newAcc;
                    } else {
                        return acc;
                    }
                },
                Exercise::combineLists);
    }

    private static <I> List<I> combineLists(List<I> left, List<I> right) {
        // We are copying left to new list to avoid mutating it.
        List<I> newLeft = new ArrayList<>(left);
        newLeft.addAll(right);
        return newLeft;
    }

}

