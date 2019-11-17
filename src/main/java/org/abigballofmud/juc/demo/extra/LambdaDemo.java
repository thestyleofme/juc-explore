package org.abigballofmud.juc.demo.extra;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2019/11/03 0:41
 * @since 1.0
 */
public class LambdaDemo {

    public static void main(String[] args) {
        // Integer::sum 相当于 (x, y) -> x + y 所以这里可以简写
        // Foo foo = Integer::sum;
        Foo foo = (x, y) -> x + y + 1;
        System.out.println(foo.justDoSomething(10, 2));
        System.out.println(foo.div(10, 2));
        Foo.say("world");
    }

}

/**
 * 函数式接口，只能一个抽象方法
 * 可以多个default/多个static
 */
@FunctionalInterface
interface Foo {

    static void say(String name) {
        System.out.println("hello, " + name);
    }

    /**
     * 求和
     *
     * @param x int
     * @param y int
     * @return x+y
     */
    int justDoSomething(int x, int y);

    /**
     * 相除
     *
     * @param x int
     * @param y int
     * @return x/y
     */
    default int div(int x, int y) {
        return x / y;
    }

}