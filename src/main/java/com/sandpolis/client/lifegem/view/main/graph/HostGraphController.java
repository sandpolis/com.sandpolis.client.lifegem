//============================================================================//
//                                                                            //
//                         Copyright © 2015 Sandpolis                         //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPL    //
//  as published by the Mozilla Foundation.                                   //
//                                                                            //
//============================================================================//
package com.sandpolis.client.lifegem.view.main.graph;

import static com.sandpolis.core.net.connection.ConnectionStore.ConnectionStore;
import static com.sandpolis.core.net.network.NetworkStore.NetworkStore;
import static com.sandpolis.core.instance.profile.ProfileStore.ProfileStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fxgraph.graph.Graph;
import com.fxgraph.graph.Model;
import com.google.common.eventbus.Subscribe;
import com.sandpolis.client.lifegem.ui.common.controller.AbstractController;
import com.sandpolis.core.net.connection.ConnectionEvents.SockEstablishedEvent;
import com.sandpolis.core.net.connection.ConnectionEvents.SockLostEvent;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class HostGraphController extends AbstractController {

	private static final Logger log = LoggerFactory.getLogger(HostGraphController.class);

	@FXML
	private BorderPane pane;

	private Graph graph;

	@FXML
	public void initialize() {
		graph = new Graph();
		pane.setCenter(graph.getCanvas());

		Model model = graph.getModel();

		// Load the network's current state
		graph.beginUpdate();
		for (int cvid : NetworkStore.getNetwork().nodes()) {
			ProfileStore.getByCvid(cvid).ifPresentOrElse(profile -> {
				model.addCell(HostCell.build(profile));
			}, () -> {
				log.warn("No profile found for: {}", cvid);
			});
		}
		graph.endUpdate();

		// Setup network change listeners
		ConnectionStore.register(this);
	}

	@Subscribe
	private void onSockLost(SockLostEvent event) {
		Platform.runLater(() -> {
			graph.beginUpdate();
			// TODO condition
			graph.getModel().getAllEdges().removeIf(edge -> true);
			graph.endUpdate();
		});
	}

	@Subscribe
	private void onSockEstablished(SockEstablishedEvent event) {
		Platform.runLater(() -> {
			// TODO
		});
	}

}
