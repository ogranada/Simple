package com.framework.simple.datalayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model {

	Map<String, Object> data;
	private ModelDelegate onAddItem;
	private ModelDelegate onEditItem;
	private ModelDelegate onDeleteItem;
	private List<Collection> collections = new ArrayList<Collection>(0);

	public Model() {
		data = new HashMap<String, Object>();
	}

	public Model(Collection collection) {
		data = new HashMap<String, Object>();
		collections.add(collection);
		collection.addItem(this);
	}

	public Model setCollection(Collection collection) {
		if (!collections.contains(collection)) {
			collections.add(collection);
		}
		return this;
	}

	public Model addField(String key, Object value) {
		ModelDelegate ModelDelegate = null;
		if (data.containsKey(key)) {
			ModelDelegate = onEditItem;
		} else {
			ModelDelegate = onAddItem;
		}
		data.put(key, value);
		if (ModelDelegate != null) {
			ModelDelegate.event(this);
		}
		for (Collection collection : collections) {
			collection.onItemModified(this);
		}
		return this;
	}

	public Model addFields(Map<String, Object> newValues) {
		ModelDelegate ModelDelegate = null;
		for (String key : newValues.keySet()) {
			if (data.containsKey(key)) {
				ModelDelegate = onEditItem;
			} else {
				ModelDelegate = onAddItem;
			}
		}
		data.putAll(newValues);
		if (ModelDelegate != null) {
			ModelDelegate.event(this);
		}
		return this;
	}

	public Model deleteField(String key) {
		data.remove(key);
		if (onDeleteItem != null) {
			onDeleteItem.event(this);
		}
		return this;
	}

	public Model clear() {
		data.clear();
		return this;
	}

	/**
	 * 
	 * @param evt
	 *            : event trigger, the available values are add, edit and delete
	 * @param ModelDelegate
	 * @return actual
	 */
	public Model on(String evt, ModelDelegate ModelDelegate) {
		if (evt.toLowerCase().equals("add")) {
			onAddItem = ModelDelegate;
		} else if (evt.toLowerCase().equals("edit")) {
			onEditItem = ModelDelegate;
		} else if (evt.toLowerCase().equals("delete")) {
			onDeleteItem = ModelDelegate;
		}
		return this;
	}

	@Override
	public String toString() {
		return data.toString();
	}

	public static interface ModelDelegate {
		public void event(Model item);
	}

}
