import java.util.*;

public class HelloWorld {
    public static void main(String[] args) {
        Integer integer = 10;
        String[] strings = {"1", "2", "3"};
        String str = "Hello world!";
        Test2 test2 = new Test2(9, 9);
        Test test = new Test(1, 2, test2);
        Map<String,Integer> map = new HashMap<>();
        map.put("test", 1);
        List<String> list = new ArrayList<>();
        list.add("list");
        List<List<String>> listList = new ArrayList<>();
        listList.add(list);
        Set<String> set = new HashSet<>();
        set.add("set");
        int i = 10;
        String new_str = "new";
        i = add(i, test);
        System.out.println(new_str);
    }

    public static int add(int i, Test t) {
        return add(i) + 1;
    }

    public static int add(int i){
        return i + 1;
    }
}

class Test {
    private int a;
    private Integer b;
    private Test2 innerTest2;

    public Test(int a, Integer b, Test2 test) {
        this.a = a;
        this.b = b;
        this.innerTest2 = test;
    }

    @Override
    public String toString() {
        return "Test{" +
                "a=" + a +
                ", b=" + b +
                ", innerTest2=" + innerTest2 +
                '}';
    }
}

class Test2 {
    private int c;
    private Integer d;

    public Test2(int c, Integer d) {
        this.c = c;
        this.d = d;
    }

    @Override
    public String toString() {
        return "Test2{" +
                "c=" + c +
                ", d=" + d +
                '}';
    }
}