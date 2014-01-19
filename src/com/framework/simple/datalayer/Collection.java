package com.framework.simple.datalayer;

import java.util.ArrayList;
import java.util.List;

public class Collection {

	List<Model> data;
	private CollectionDelegate onAddItem;
	private CollectionDelegate onEditItem;
	private CollectionDelegate onRemoveItem;

	public Collection() {
		data = new ArrayList<Model>(0);
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
