public class HelloWorld {
    public static void main(String[] args) {
        int i = add(10);
        System.out.println(i);
    }

    public static int add(int i){
        if (i == 0) {
            return i;
        }
        return i + add(i - 1);
    }
}

