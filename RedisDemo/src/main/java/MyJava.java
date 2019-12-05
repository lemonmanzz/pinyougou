public class MyJava {
    public static void main(String[] a) {
        Integer aa = new Integer(1);
        Integer bb = new Integer(2);
        num num = new num();
        aa(num.a,num.b);
        System.out.println(num.a);
        System.out.println(num.b);
    }
    public static void aa(Integer a,Integer b){
        a+=b;
        b=a;
        System.out.println("a = " + a);
        System.out.println("b = " + b);
    }
}
class num{
    public Integer a = 1;
    public Integer b = 2;
}
