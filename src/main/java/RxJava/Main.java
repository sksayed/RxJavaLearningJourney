package RxJava;

import io.reactivex.Observable;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {


        String[] arr = new String[] {"sayed" , "Bin" , "Rahman"};
        Observable.fromArray(arr).subscribe(s -> System.out.println("Hello "+s));

        String[] arraySecond = new String[] {"sayed" , "Bin" , "Rahman" ,"i" , "a" , "m" , "disco" ,"dancer"};
        Observable<String> stringObservable = Observable.fromArray(arraySecond);
        List<Integer> integerList = Arrays.asList( new Integer[] {1,2,3,4,9,6,88,74,85});
        Observable<Integer> integerObservable = Observable.fromIterable(integerList);


    }

}
