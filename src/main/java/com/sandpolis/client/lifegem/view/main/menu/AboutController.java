//============================================================================//
//                                                                            //
//                         Copyright © 2015 Sandpolis                         //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPL    //
//  as published by the Mozilla Foundation.                                   //
//                                                                            //
//============================================================================//
package com.sandpolis.client.lifegem.view.main.menu;

import static com.sandpolis.core.instance.pref.PrefStore.PrefStore;
import static com.sandpolis.client.lifegem.stage.StageStore.StageStore;

import java.io.IOException;

import com.sandpolis.client.lifegem.ui.common.FxUtil;
import com.sandpolis.client.lifegem.ui.common.controller.AbstractController;

import javafx.fxml.FXML;
import javafx.stage.StageStyle;

public class AboutController extends AbstractController {

	@FXML
	public void open_about() throws IOException {
		StageStore.create(stage -> {
			stage.initStyle(StageStyle.TRANSPARENT);
			stage.setRoot("/fxml/view/about/About.fxml");
			stage.setWidth(PrefStore.getInt("ui.view.about.width"));
			stage.setHeight(PrefStore.getInt("ui.view.about.height"));
			stage.setTitle(FxUtil.translate("stage.about.title"));
			stage.setResizable(false);
		});
	}
}
