package RxJava;

import io.reactivex.Observable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {

        Second();

    }

    public static void  Initial (){

        String[] arr = new String[] {"sayed" , "Bin" , "Rahman"};
        Observable.fromArray(arr).subscribe(s -> System.out.println("Hello "+s));
    }

    public static void Second () {
        String[] arraySecond = new String[] {"sayed" , "Bin" , "Rahman" ,"i" , "a" , "m" , "disco" ,"dancer"};
        Observable<String> stringObservable = Observable.fromArray(arraySecond);
        List<Integer> integerList = Arrays.asList( new Integer[] {1,2,3,4,9,6,88,74,85});
        Observable<Integer> integerObservable = Observable.fromIterable(integerList);
        Observable interval = Observable.interval(1000 , TimeUnit.MILLISECONDS);

        System.out.println(" first subscriber ");
        integerObservable.subscribe(integer -> System.out.println("value is = " + integer));
        System.out.println(" second subscriber ");
        integerObservable.subscribe(Main::Replica);
        System.out.println("Thrid subscriber ");
        integerObservable.subscribe(Main::even);

    }

    private static <T> void Replica (T val) {
        System.out.print(val + " - ");
    }

    private static void even (int val) {
        if (val % 2 == 0){
            System.out.println(val);
        }
    }
}
