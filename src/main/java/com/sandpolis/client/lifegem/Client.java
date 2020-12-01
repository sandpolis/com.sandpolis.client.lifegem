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
package com.sandpolis.client.lifegem;

import static com.sandpolis.client.lifegem.stage.StageStore.StageStore;
import static com.sandpolis.core.instance.Environment.printEnvironment;
import static com.sandpolis.core.instance.MainDispatch.register;
import static com.sandpolis.core.instance.plugin.PluginStore.PluginStore;
import static com.sandpolis.core.instance.pref.PrefStore.PrefStore;
import static com.sandpolis.core.instance.profile.ProfileStore.ProfileStore;
import static com.sandpolis.core.instance.state.STStore.STStore;
import static com.sandpolis.core.instance.thread.ThreadStore.ThreadStore;
import static com.sandpolis.core.net.connection.ConnectionStore.ConnectionStore;
import static com.sandpolis.core.net.exelet.ExeletStore.ExeletStore;
import static com.sandpolis.core.net.network.NetworkStore.NetworkStore;
import static com.sandpolis.core.net.stream.StreamStore.StreamStore;

import java.util.List;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sandpolis.client.lifegem.ui.common.FxUtil;
import com.sandpolis.core.foundation.Config;
import com.sandpolis.core.instance.Core;
import com.sandpolis.core.instance.Environment;
import com.sandpolis.core.instance.MainDispatch;
import com.sandpolis.core.instance.MainDispatch.InitializationTask;
import com.sandpolis.core.instance.MainDispatch.ShutdownTask;
import com.sandpolis.core.instance.MainDispatch.Task;
import com.sandpolis.core.instance.profile.Profile;
import com.sandpolis.core.instance.state.st.ephemeral.EphemeralDocument;
import com.sandpolis.core.instance.state.vst.VirtCollection;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import tornadofx.FX;

/**
 * {@link Client} is responsible for initializing the instance.
 *
 * @since 4.0.0
 */
public final class Client {

	private static final Logger log = LoggerFactory.getLogger(Client.class);

	public static void main(String[] args) {
		printEnvironment(log, "Sandpolis Client");

		register(Client.loadEnvironment);
		register(Client.loadStores);
		register(Client.loadPlugins);
		register(Client.loadUserInterface);

		register(Client.shutdown);
	}

	/**
	 * Load the runtime environment.
	 */
	@InitializationTask(name = "Load runtime environment", fatal = true)
	private static final Task loadEnvironment = new Task(outcome -> {

		// TODO move
		Config.MESSAGE_TIMEOUT.register(1000);

		Environment.LIB.set(Config.PATH_LIB.value().orElse(null)).requireReadable();
		Environment.LOG.set(Config.PATH_LOG.value().orElse(null)).requireWritable();
		Environment.PLUGIN.set(Config.PATH_PLUGIN.value().orElse(null)).requireWritable();
		return outcome.success();
	});

	/**
	 * Load static stores.
	 */
	@InitializationTask(name = "Load static stores", fatal = true)
	private static final Task loadStores = new Task(outcome -> {
		Platform.startup(() -> {
		});

		STStore.init(config -> {
			config.concurrency = 1;
			config.root = new EphemeralDocument();
		});

		ProfileStore.init(config -> {
			config.collection = new VirtCollection<Profile>(STStore.root(), Profile::new);
		});

		ThreadStore.init(config -> {
			config.defaults.put("net.exelet", new NioEventLoopGroup(2));
			config.defaults.put("net.connection.outgoing", new NioEventLoopGroup(2));
			config.defaults.put("net.message.incoming", new UnorderedThreadPoolEventExecutor(2));
			config.defaults.put("store.event_bus", Executors.newSingleThreadExecutor());
		});

		PrefStore.init(config -> {
			config.instance = Core.INSTANCE;
			config.flavor = Core.FLAVOR;

			config.defaults.put("ui.help", true);
			config.defaults.put("ui.animations", true);
			config.defaults.put("ui.main.view", "list");
			config.defaults.put("ui.main.console", false);
			config.defaults.put("ui.tray.minimize", true);
			config.defaults.put("ui.theme", "Crimson");

			config.defaults.put("ui.view.login.width", 535);
			config.defaults.put("ui.view.login.height", 415);
			config.defaults.put("ui.view.main.width", 770);
			config.defaults.put("ui.view.main.height", 345);
			config.defaults.put("ui.view.about.width", 660);
			config.defaults.put("ui.view.about.height", 400);
			config.defaults.put("ui.view.generator.width", 700);
			config.defaults.put("ui.view.generator.height", 400);
		});

		ExeletStore.init(config -> {
			config.exelets = List.of();
		});

		StreamStore.init(config -> {
		});

		NetworkStore.init(config -> {
		});

		ConnectionStore.init(config -> {
			config.collection = ProfileStore.getByUuid(Core.UUID).get().connection();
		});

		PluginStore.init(config -> {
			config.collection = ProfileStore.getByUuid(Core.UUID).get().plugin();
		});

		return outcome.success();
	});

	@ShutdownTask
	public static final Task shutdown = new Task(outcome -> {
		NetworkStore.close();
		ConnectionStore.close();
		PrefStore.close();
		PluginStore.close();
		ProfileStore.close();
		ThreadStore.close();

		return outcome.success();
	});

	/**
	 * Load plugins.
	 *
	 * @return The task's outcome
	 */
	@InitializationTask(name = "Load client plugins")
	private static final Task loadPlugins = new Task(outcome -> {
		if (!Config.PLUGIN_ENABLED.value().orElse(true))
			return outcome.skipped();

		PluginStore.scanPluginDirectory();
		PluginStore.loadPlugins();

		return outcome.success();
	});

	/**
	 * Load and show the user interface.
	 *
	 * @return The task's outcome
	 */
	@InitializationTask(name = "Load user interface")
	private static final Task loadUserInterface = new Task(outcome -> {
		new Thread(() -> Application.launch(UI.class)).start();

		return outcome.success();
	});

	/**
	 * The {@link Application} class for starting the user interface.
	 */
	public static class UI extends Application {

		private static Application singleton;

		public UI() {
			if (singleton != null)
				throw new IllegalStateException();

			singleton = this;
		}

		/**
		 * Get the application handle.
		 *
		 * @return The {@link Application} or {@code null} if it has not started
		 */
		public static Application getApplication() {
			return singleton;
		}

		@Override
		public void start(Stage main) throws Exception {

			// Ignore the primary stage and create a new one
			var stage = StageStore.create(s -> {
				s.setRoot("com.sandpolis.client.lifegem.ui.login.LoginView");
				//s.setWidth(PrefStore.getInt("ui.view.login.width"));
				//s.setHeight(PrefStore.getInt("ui.view.login.height"));
				//s.setResizable(false);
				s.setTitle(FxUtil.translate("stage.login.title"));
			});

			// Register application
			FX.registerApplication(this, stage);

		}
	}

	private Client() {
	}

	static {
		MainDispatch.register(Client.class);
	}
}
