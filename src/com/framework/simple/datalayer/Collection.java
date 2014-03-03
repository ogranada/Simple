package com.framework.simple.datalayer;

import java.util.ArrayList;
import java.util.List;

import com.framework.simple.datalayer.sources.RESTDataSource;
import com.framework.simple.interfaces.DataSource;

public class Collection {

	List<Model> data;
	private CollectionDelegate onAddItem;
	private CollectionDelegate onEditItem;
	private CollectionDelegate onRemoveItem;
	private DataSource dataSource;

	public Collection() {
		data = new ArrayList<Model>(0);
	}

	public Collection(String source) {
		data = new ArrayList<Model>(0);
		if(source.startsWith("http://")){
			String host = source.contains(":")?source.split(":")[0]:source;
			int port = source.contains(":")?Integer.parseInt(source.split(":")[1]):80;
			this.setDataSource(new RESTDataSource(host, port));
		} else if(source.startsWith("file://") && source.endsWith(".db")){
			// TODO:Create SQLite3 DataSource class
			//this.setDataSource();
		}
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return this.dataSource;
	}

	public Collection addItem(Model item) {
		data.add(item);
		item.setCollection(this);
		if (onAddItem != null) {
			onAddItem.event(item);
		}
		return this;
	}

	public Model getItem(int position) {
		return data.get(position);
	}

	public void onItemModified(Model m) {
		if (data.contains(m)) {
			if (onEditItem != null) {
				onEditItem.event(m);
			}
		}
	}

	public Collection removeItem(int location) {
		if (location >= 0 && location < data.size()) {
			Model item = data.get(location);
			if (onRemoveItem != null) {
				onRemoveItem.event(item);
			}
		}
		return this;
	}

	public Collection removeItem(Model item) {
		if (data.contains(item)) {
			if (onRemoveItem != null) {
				onRemoveItem.event(item);
			}
		}
		return this;
	}

	/**
	 * 
	 * @param evt
	 *            : event trigger, the available values are add, edit and delete
	 * @param CollectionDelegate
	 * @return actual
	 */
	public Collection on(String evt, CollectionDelegate CollectionDelegate) {
		if (evt.toLowerCase().equals("add")) {
			onAddItem = CollectionDelegate;
		} else if (evt.toLowerCase().equals("edit")) {
			onEditItem = CollectionDelegate;
		} else if (evt.toLowerCase().equals("delete")) {
			onRemoveItem = CollectionDelegate;
		}
		return this;
	}

	public static interface CollectionDelegate {
		public void event(Model item);
	}

}
