public class Main {
    public static void main(String[] args) {
        Container<String> strings = new Container<String>();
        Container<Integer> numbers = new Container<>();

        Container stuff = numbers;
        stuff.add("hello");
        strings.add("hello");
        strings.add("world");

        String first = strings.get(0);
        String second = strings.get(1);

        Integer value = numbers.get(0); 

        System.out.println(first + " " + second);
    }
}