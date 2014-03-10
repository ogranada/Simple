Simple
======

* Android Framework for easy application development.

Goals
-----

* Create an application reducing the development time.
* Create eficient applications


Wow Factor
----------

* Dependency Injection
* MV* Project management

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
		txt1.setText("Long Message.....");
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



MV*
---
Simple Model-View-* provides functionalities to separate in the application the data layer,
the logic and the UI's. this facilitates the process of application development and its maintenance.

* Data Layer
The data layer is composed by ```Models``` and ```Collections```. One ```Model``` has many fields.
One ```Collection``` has many ```Model``` instances.
```Models``` and ```Collections``` allow assign callbacks (Delegates) when an item is added, modified or removed.
Let's see the sample:

```java

class test {
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Collection collection = new Collection();
		collection.on("add", new CollectionDelegate() {
			
			@Override
			public void event(Model item) {
				System.out.println("added "+item);
			}
		});
		collection.on("edit", new CollectionDelegate() {
			
			@Override
			public void event(Model item) {
				System.out.println("item modified: "+item);
			}
		});
		
		Model m = new Model(collection);
		m.addField("newKey", "newValue");
		m.addField("anotherKey", "anotherValue");
    }
}

```

The output of las sample is:

```
added {}
item modified: {newKey=newValue}
item modified: {newKey=newValue, anotherKey=anotherValue}
```
