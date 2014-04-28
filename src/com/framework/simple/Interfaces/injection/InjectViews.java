package com.framework.simple.interfaces.injection;

/**
 * Created by ogranada on 25/04/14.
 */
public @interface InjectViews {
    InjectView[] value();
}

/*
    @Foos({@Foo(bar="one"), @Foo(bar="two")})
    public void haha() {}
*/

