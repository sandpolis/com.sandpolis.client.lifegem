//============================================================================//
//                                                                            //
//                         Copyright © 2015 Sandpolis                         //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPL    //
//  as published by the Mozilla Foundation.                                   //
//                                                                            //
//============================================================================//
package com.sandpolis.client.lifegem.view.main.list;

import static com.sandpolis.core.instance.state.oid.InstanceOid.InstanceOid;
import static com.sandpolis.core.net.connection.ConnectionStore.ConnectionStore;
import static com.sandpolis.core.net.network.NetworkStore.NetworkStore;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sandpolis.client.lifegem.ui.common.controller.AbstractController;
import com.sandpolis.client.lifegem.view.main.Events.HostListHeaderChangeEvent;
import com.sandpolis.core.instance.state.oid.AbsoluteOid;
import com.sandpolis.core.instance.state.st.STAttribute;

import javafx.fxml.FXML;

public class HostListController extends AbstractController {

	private static final Logger log = LoggerFactory.getLogger(HostListController.class);

//	@FXML
//	private TableView<FxProfile> table;
//
//	private FxDocument<FxProfile> collection;

	private static final List<AbsoluteOid<?>> DEFAULT_HEADERS = List.of(InstanceOid().profile.uuid,
			InstanceOid().profile.ip_address, InstanceOid().profile.instance_type);

	private String serverUuid;

	@FXML
	public void initialize() {

//		table.setItems(collection.getObservable());
//		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//		table.getSelectionModel().selectedItemProperty().addListener((p, o, n) -> {
//			if (n == null)
//				post(HostDetailCloseEvent::new);
//			else
//				post(HostDetailOpenEvent::new, n);
//		});

		// Set default headers
//		addColumns(DEFAULT_HEADERS);

		resync();
	}

	/**
	 * Change host table columns to the given set.
	 *
	 * @param event The change event which contains the desired headers
	 */
	@Subscribe
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void changeColumns(HostListHeaderChangeEvent event) {
//		List<AttributeColumn> columns = (List) table.getColumns();
//
//		// Remove current columns that are not in the new set
//		columns.removeIf(column -> !event.get().contains(column.getOid()));
//
//		// Add new columns that are not in the current set
//		addColumns(event.get().stream()
//				.filter(header -> !columns.stream().anyMatch(column -> header.equals(column.getOid())))
//				.collect(Collectors.toList()));
	}

	/**
	 * Add columns to the host table.
	 *
	 * @param headers The headers to add
	 */
	private void addColumns(List<AbsoluteOid<STAttribute<?>>> headers) {
		if (serverUuid == null) {
//			table.getColumns().clear();
		} else {
//			headers.stream().map(oid -> oid.resolve(serverUuid)).map(AttributeColumn::new)
//					.forEach(table.getColumns()::add);
		}
	}

	private void resync() {
		NetworkStore.getPreferredServer().ifPresentOrElse(cvid -> {
			serverUuid = ConnectionStore.getByCvid(cvid).get().getRemoteUuid();

			// Attach the local collection
//			STCmd.async().sync(collection, InstanceOid().profile);
		}, () -> {
			serverUuid = null;
//			table.setPlaceholder(new Label("Not connected to a server"));
		});
	}

}
