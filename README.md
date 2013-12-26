Simple
======

* Android Framework for easy application development.

Goals
-----

* Create an application reducing the development time.
* Create eficient applications


Dependencies
------------

* [ActionBarSherlock](https://github.com/JakeWharton/ActionBarSherlock)
* [Menu Drawer](https://github.com/SimonVT/android-menudrawer)


Wow Factor
----------

* Dependency Injection
* Menu Drawer (Compatible With old android versions)

_______________________________________________________________________

Tutorial
========

Injection
---------

Simple Injection has support for views and methods injection. The injection 
has executed when the method ```inject()``` from Simple Activities is called.

* View Injection

```java

	@InjectView(id=R.id.txt1)
	TextView txt1;

```

* Method Injection

```java

	@InjectMethod(id=R.id.btn1)
	public void onBtnClick(View v){
		txt1.setText("Mensaje.....");
	}

	@InjectMethod(id=R.id.btn1,method="OnLongClickListener")
	public boolean onBtnLongClick(View v){
		txt1.setText("Long Mensaje.....");
		return true;
	}
	
```

* Sample Class

```java


public class MainActivity extends SimpleActivity {

	@InjectView(id=R.id.txt1)
	TextView txt1;

	@InjectMethod(id=R.id.btn1)
	public void onBtnClick(View v){
		txt1.setText("Mensaje.....");
	}

	@InjectMethod(id=R.id.btn1,method="OnLongClickListener")
	public boolean onBtnLClick(View v){
		txt1.setText("Long Mensaje.....");
		return true;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		inject();
	}

	

}

```

