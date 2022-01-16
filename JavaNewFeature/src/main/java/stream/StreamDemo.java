package stream;


import org.junit.Test;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Character.isDigit;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class StreamDemo {

    public static void main(String[] args) {

    }

    // 常用操作
    // 1. collect，由Stream 里的值生成一个列表，是一个及早求值操作。
    @Test
    public void collected() {
        List<String> collected = Stream.of("a", "b", "c") // 由list生成stream
                                        .collect(Collectors.toList()); // collect操作由stream生成list
        assertEquals(asList("a", "b", "c"), collected);
    }

    // 2. map，通过一个函数，将一个流中的值转换成另外一个新的流。
    @Test
    public void map() {
        // 使用map将字符串转为大写形式
        List<String> collected = Stream.of("a", "b", "hello")
                                        .map(String::toUpperCase)
                                        .collect(Collectors.toList());
        assertEquals(asList("A", "B", "HELLO"), collected);
    }

    // 3. filter，遍历数据并检查其中的元素时，可以使用filter，核心是保留Stream中的一些元素，过滤掉其他的。
    @Test
    public void filter() {
        List<String> beginWithNums = Stream.of("a", "1abc", "abc1")
                                            .filter(value -> isDigit(value.charAt(0)))
                                            .collect(Collectors.toList());
        assertEquals(asList("1abc"), beginWithNums);
    }

    // 4. flatMap，map操作是用一个新的值替代Stream中的值，但是如果希望使用Stream替换值，然后将多个Stream连成一个Stream，就可以使用flatmap。
    @Test
    public void flatMap() {
        // 一个包含多个list的流，希望得到所有数字的list
        List<Integer> together = Stream.of(asList(1, 2), asList(3, 4))
                                        .flatMap(Collection::stream)
                                        .collect(Collectors.toList());
        assertEquals(asList(1, 2, 3, 4), together);
    }


    // 5. max和min，求最大值和最小值
    @Test
    public void maxAndMin() {
        List<Track> tracks = asList(new Track("bakai", 524),
                                    new Track("Violets for your furs", 378),
                                    new Track("Time was", 451));
        Track shortestTrack = tracks.stream()
                                    .min(Comparator.comparing(Track::getLength))
                                    .get();
        Track longestTrack = tracks.stream()
                                   .max(Comparator.comparing(Track::getLength))
                                   .get();
        assertEquals(tracks.get(1), shortestTrack);
        assertEquals(tracks.get(0), longestTrack);

    }

    // 6. reduce，实现从一组值中生成一个值。上述例子中用到的count 、min 和max 方法，其实都属于reduce方法。
    @Test
    public void reduce() {
        // 使用reduce求和
        int count = Stream.of(1, 2, 3)
                          .reduce(0, Integer::sum);
        assertEquals(6, count);

        // 展开reduce操作
        BinaryOperator<Integer> accumulator = Integer::sum;
        count = accumulator.apply(
                accumulator.apply(
                        accumulator.apply(0, 1),
                2),
        3);

        System.out.println(count);

    }

    /**
     * case: 重构遗留代码
     */
    // 原来的代码
    public Set<String> findLongTracks(List<Album> albums) {
        Set<String> longTrackNames = new HashSet<>();
        for(Album album : albums) {
            for (Track track : album.getTracks()) {
                if (track.getLength() > 60) {
                    String name = track.getName();
                    longTrackNames.add(name);
                }
            }
        }
        return longTrackNames;
    }

    // 重构后
    public Set<String> findLongTracksRe(List<Album> albums) {
        return albums.stream()
                     .flatMap(album -> album.getTracks().stream())
                     .filter(track -> track.getLength() > 60)
                     .map(Track::getName)
                     .collect(Collectors.toSet());
    }


    /*
      重构前后的差别：
      1, 代码可读性增加；
      2，代码不再包含一堆保存中间结果的垃圾变量；
      3，可以并行处理。
     */

    /*
        总结：
        - 内部迭代将更多控制权交给了集合类
        - Stream属于一种内部迭代方式
        - 将Lambda和Stream结合起来，可以完成很多常见的集合操作。
     */

}
