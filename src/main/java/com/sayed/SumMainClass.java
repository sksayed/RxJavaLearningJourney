package com.sayed;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.ConnectableObservable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SumMainClass {
    public static ConnectableObservable<String> from(final InputStream inputStream) {
        //this is an example of facade
        return getRealConnectableObservable(new BufferedReader(new InputStreamReader(inputStream)));
    }

    private static ConnectableObservable<String> getRealConnectableObservable(BufferedReader bufferedReader) {
        return Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (subscriber.isUnsubscribed()) {
                    //jodi unsubscribe kore thake taile return koro
                    return;
                }
                //jodi unsubscribe na kore thake
                try {
                    String line;
                    //jotokhn na unsubscribe kore ar input nite thake
                    while (!subscriber.isUnsubscribed() && ((line = bufferedReader.readLine()) != null)) {
                        if (line == null || line.equalsIgnoreCase("exit")) {
                            break;
                            //get out of this while loop
                        }
                        //jodi exit na kore ar input dey taile
                        //pass on the value to subscriber
                        subscriber.onNext(line);
                    }
                } catch (Exception e) {
                    //jodi error dhora khay
                    e.printStackTrace();
                    subscriber.onError(e);
                }
                //here is the out of while loop
                //and subscriber is still subscribed to this
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }
        }).publish();
        //here its also acting like a producer
    }

    public static Observable<Double> varStream(final String varName, Observable<String> input) {
        //first e ekta pattern declare korbo
        //ei pattern e input nile manbo , naile manbo na
        /*final Pattern pattern = Pattern.compile("\\^s*" + varName + "\\s*[:|=]\\s*(-?\\d+\\.?\\d*)$");*/
        final Pattern pattern = Pattern.compile("^\\s*" + varName
                + "\\s*[:|=]\\s*(-?\\d+\\.?\\d*)$");
        return input.map(new Func1<String, Matcher>() {
            @Override
            public Matcher call(String s) {
                return pattern.matcher(s);
            }
        }).filter(new Func1<Matcher, Boolean>() {
            @Override
            public Boolean call(Matcher matcher) {
                if (matcher.matches() && matcher.group(1) != null) {
                    return true;
                } else {
                    return false;
                }
                //if it matches then the value is stored inside group 1
                //its an assumption just
            }
        }).map(new Func1<Matcher, Double>() {
            @Override
            public Double call(Matcher matcher) {
                return Double.parseDouble(matcher.group(1));
            }
        });
    }

    public static class ReactiveSum implements Observer<Double> {
        private Double sum;

        public ReactiveSum(Observable<Double> a, Observable<Double> b) {
            this.sum = 0.0;
            Observable.combineLatest(a, b, new Func2<Double, Double, Double>() {

                @Override
                public Double call(Double a, Double b) {
                    return a + b;
                }
            }).subscribe(this);
        }

        @Override
        public void onCompleted() {
            System.out.println("Exiting , last sum was :" + sum);
        }

        @Override
        public void onError(Throwable throwable) {
            System.err.println("An Error Occured");
            throwable.printStackTrace();
        }

        @Override
        public void onNext(Double aDouble) {
            this.sum = aDouble;
            System.out.println("update a + b : = " + sum);
        }
    }

    public static void main(String[] args) {
        //this is the client code
        ConnectableObservable<String> input = from(System.in);
        // getting the observable from input
        Observable<Double> valueA = varStream("a", input);
        Observable<Double> valueB = varStream("b", input);

        ReactiveSum reactiveSum = new ReactiveSum(valueA, valueB);
        //this line for listening the inputs
        input.connect();

    }

}
