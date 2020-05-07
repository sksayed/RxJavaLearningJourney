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

public class MultiplyMainClass {

    //create a observable from the input stream
    public static ConnectableObservable<String> getObservableFromInputStream(final InputStream inputStream) {

        return Observable.create(new Observable.OnSubscribe<String>() {
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                try {
                    while (!subscriber.isUnsubscribed() && ((line = reader.readLine()) != null)) {
                        if (line != null && line.equalsIgnoreCase("exit")) {
                            break;
                        }
                        subscriber.onNext(line);
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                    System.err.println("Error in getting input");
                }
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }
        }).publish();

    }

    public static Observable<Double> varStream(String varName, Observable<String> value) {
        final Pattern pattern = Pattern.compile("^\\s*" + varName
                + "\\s*[:|=]\\s*(-?\\d+\\.?\\d*)$");
        return value.map(new Func1<String, Matcher>() {
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
            }
        }).map(new Func1<Matcher, Double>() {
            @Override
            public Double call(Matcher matcher) {
                return Double.parseDouble(matcher.group(1));
            }
        });
    }

    public static class ReactiveMultiply implements Observer<Double> {
        Double result;

        public ReactiveMultiply(Observable<Double> a, Observable<Double> b) {
            this.result = 0.0;
            Observable.combineLatest(a, b, new Func2<Double, Double, Double>() {

                @Override
                public Double call(Double a, Double b) {
                    return  result = a * b;
                }
            }).subscribe(this);
        }

        @Override
        public void onCompleted() {
            System.out.println("The last update is :"+this.result);
        }

        @Override
        public void onError(Throwable throwable) {
            System.out.println("Error occured while computing");
        }

        @Override
        public void onNext(Double aDouble) {
            System.out.println(" the multiplication of a and b is "+result);
        }
    }

    public static void main(String[] args) {
        ConnectableObservable<String> reactiveInput = getObservableFromInputStream(System.in) ;
        Observable<Double> val1 = varStream("a", reactiveInput);
        Observable<Double> val2 = varStream("b", reactiveInput);

        ReactiveMultiply reactiveMultiply = new ReactiveMultiply(val1, val2);
        reactiveInput.connect();



    }
}
