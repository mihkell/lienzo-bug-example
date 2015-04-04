package com.mytest.client;

public class ConLog {

    public static native void console(String text)
    /*-{
        console.log(text);
    }-*/;
    
    public static native void console(int num)
    /*-{
        console.log(num);
    }-*/;
    
    public static native void console(Object obj) /*-{
        console.log(obj);
    }-*/;

    
}
