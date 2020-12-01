//============================================================================//
//                                                                            //
//                Copyright © 2015 - 2020 Subterranean Security               //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPL    //
//  as published by the Mozilla Foundation at:                                //
//                                                                            //
//    https://mozilla.org/MPL/2.0                                             //
//                                                                            //
//=========================================================S A N D P O L I S==//
package com.sandpolis.client.lifegem.state;

import java.util.List;
import java.util.function.Supplier;

import com.sandpolis.core.instance.State.ProtoAttribute;
import com.sandpolis.core.instance.state.oid.Oid;
import com.sandpolis.core.instance.state.oid.RelativeOid;
import com.sandpolis.core.instance.state.st.STAttribute;
import com.sandpolis.core.instance.state.st.STAttributeValue;
import com.sandpolis.core.instance.state.st.STDocument;
import com.sandpolis.core.instance.state.st.ephemeral.EphemeralAttribute;

import javafx.beans.value.ObservableValueBase;

public class FxAttribute<T> extends ObservableValueBase<T> implements STAttribute<T> {

	private EphemeralAttribute<T> container;

	public FxAttribute(FxDocument<?> parent) {
		this.container = new EphemeralAttribute<>();
	}

	@Override
	public void addListener(Object listener) {
		container.addListener(listener);
	}

	@Override
	public T get() {
		return container.get();
	}

	@Override
	public T getValue() {
		return get();
	}

	@Override
	public List<STAttributeValue<T>> history() {
		return container.history();
	}

	@Override
	public void merge(ProtoAttribute snapshot) {
		container.merge(snapshot);
	}

	@Override
	public Oid oid() {
		return container.oid();
	}

	@Override
	public void removeListener(Object listener) {
		container.removeListener(listener);
	}

	@Override
	public void set(T value) {
		container.set(value);
	}

	@Override
	public ProtoAttribute snapshot(RelativeOid... oids) {
		return container.snapshot(oids);
	}

	@Override
	public void source(Supplier<T> source) {
		container.source(source);
	}

	@Override
	public long timestamp() {
		return container.timestamp();
	}

	@Override
	public STDocument parent() {
		return container.parent();
	}

}
