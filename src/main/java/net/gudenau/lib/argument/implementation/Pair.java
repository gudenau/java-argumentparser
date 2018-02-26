package net.gudenau.lib.argument.implementation;

public class Pair<T, T1>{
    private final T item1;
    private final T1 item2;
    
    public Pair(T item1, T1 item2){
        this.item1 = item1;
        this.item2 = item2;
    }
    
    public T getItem1(){
        return item1;
    }
    
    public T1 getItem2(){
        return item2;
    }
}
