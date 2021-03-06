/*
 * 01/10/2005
 *
 * PluginClassLoader.java - Class loader for GUIApplication plugins.
 * Copyright (C) 2005 Robert Futrell
 * http://fifesoft.com/rtext
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package org.fife.ui.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.SwingUtilities;

import java.util.ArrayList;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;


/**
 * Loads {@link Plugin}s for an {@link AbstractPluggableGUIApplication}.  This
 * should be instantiated in a separate thread; it handles adding any found
 * <code>Plugin</code>s to the parent application on the EDT properly.
 *
 * @author Robert Futrell
 * @version 0.6
 * @see AbstractPluggableGUIApplication
 */
class PluginLoader {

	/**
	 * The manifest attribute that plugin jars must define that specifies
	 * the main <code>Plugin</code> class.
	 */
	public static final String PLUGIN_CLASS_ATTR = "Fife-Plugin-Class";


	/**
	 * The manifest attribute that plugin jars can optionally define to
	 * specify the priority with which to load the plugin.  If this is not
	 * specified or an invalid value, then "<code>normal</code>" is used.
	 *
	 * @see #LOAD_PRIORITIES
	 */
	public static final String PLUGIN_LOAD_PRIORITY = "Fife-Plugin-Load-Priority";


	/**
	 * Valid values for {@link #PLUGIN_LOAD_PRIORITY}.
	 */
	public static final String[] LOAD_PRIORITIES = {
		"highest",  // This does not exist
		"high",
		"normal",
		"low",
		"lowest",
	};


	/**
	 * The GUI application that owns this class loader.
	 */
	private AbstractPluggableGUIApplication app;

	/**
	 * The directory in which all plugin jars reside.
	 */
	private File pluginDir;

	/**
	 * Class loader pointing to all plugin jars.
	 */
	private URLClassLoader ucl;

	/**
	 * Indicates whether all plugins have been submitted to load.  Access to
	 * this member should be synchronized.
	 *
	 * @see #loadingPluginCount
	 */
	private boolean pluginSubmissionsCompleted;

	/**
	 * Indicates how many plugins have been submitted to load, but not yet
	 * loaded.  Access to this member should be synchronized.
	 *
	 * @see #pluginSubmissionsCompleted
	 */
	private int loadingPluginCount;

	/**
	 * The amount of time to sleep, in milliseconds, between loading each
	 * plugin.
	 */
	private static final int SLEEP_TIME						= 200;


	/**
	 * Constructor.
	 *
	 * @param app The GUI application.
	 */
	public PluginLoader(AbstractPluggableGUIApplication app) {
		this.app = app;
		pluginDir = new File(app.getInstallLocation(), "plugins");
	}


	/**
	 * Returns the priority with which the plugin should be loaded.
	 *
	 * @param attrs The main manifest attributes.
	 * @return The priority with which to load the plugin.
	 */
	private static final int getLoadPriority(Attributes attrs) {

		int priority = 2;

		String temp = attrs.getValue(PLUGIN_LOAD_PRIORITY);
		if (temp!=null) {
			for (int i=0; i<LOAD_PRIORITIES.length; i++) {
				if (LOAD_PRIORITIES[i].equalsIgnoreCase(temp)) {
					priority = i;
					break;
				}
			}
		}

		return priority;

	}


	/**
	 * Returns whether plugin loading has completed.
	 *
	 * @return Whether plugin loading has completed.
	 */
	public synchronized boolean isPluginLoadingComplete() {
		return pluginSubmissionsCompleted && loadingPluginCount==0;
	}


	/**
	 * Loads a single plugin.
	 *
	 * @param className The class name of the plugin.
	 * @throws Exception If an error occurs.
	 */
	private void loadPluginImpl(String className) throws InvalidPluginException {
		
		Object[] objs = { app };
		Class[] params = { AbstractPluggableGUIApplication.class };
		Class c = null;
		try {
			c = ucl.loadClass(className);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		
		// This should be true unless there was an error in the manifest
		if (Plugin.class.isAssignableFrom(c)) {
			synchronized (this) {
				loadingPluginCount++;
			}
			
			Constructor cnst;
			try {
				cnst = c.getConstructor(params);
			} catch (NoSuchMethodException e1 ) { //should never happen
				throw new InvalidPluginException("Specified plugin does not implement the Plugin interface: " +className);
			} catch (SecurityException e2) { //should never happen
				throw new InvalidPluginException("Unknown security exception while loading " +className);
			}
			SwingUtilities.invokeLater(new LoadPluginRunnable(className, cnst, objs));
		}

		else {
			throw new InvalidPluginException(
				"Specified plugin does not implement the Plugin interface: " +
				className);
		}
	}


	/**
	 * Loads any plugins for this application.  This method is thread-safe; it
	 * ensures all plugins are added to the GUI on the EDT if necessary.
	 *
	 * @throws IOException If an I/O error occurs.
	 */
	public void loadPlugins() throws IOException {

		if (!pluginDir.isDirectory()) {
			return;
		}

		// Get all jars in the plugin directory.
		File[] jars = pluginDir.listFiles(new FileFilter() {
			public boolean accept(File f) {
				return f.getName().endsWith(".jar") && Character.isLetterOrDigit(f.getName().charAt(0));
			}
		});
		int jarCount = jars.length;

		ArrayList[] plugins = new ArrayList[LOAD_PRIORITIES.length];
		ArrayList urlList = new ArrayList();

		for (int i=0; i<jarCount; i++) {

			urlList.add(jars[i].toURI().toURL());

			JarFile jarFile = new JarFile(jars[i]);
			try {
				// If this jar contains a plugin, remember the class to load.
				Manifest mf = jarFile.getManifest();
				if (mf!=null) {
					Attributes attrs = mf.getMainAttributes();
					String clazz = attrs.getValue(PLUGIN_CLASS_ATTR);
					if (clazz!=null) {
						int priority = getLoadPriority(attrs);
						if (plugins[priority]==null) {
							plugins[priority] = new ArrayList(3); // Small
						}
						plugins[priority].add(clazz);
					}
				}
			} finally {
				jarFile.close();
			}

		}

		// Create the ClassLoader that does the actual dirty-work.
		URL[] urls = (URL[])urlList.toArray(new URL[urlList.size()]);
		ucl = new URLClassLoader(urls, app.getClass().getClassLoader());

		loadPluginsImpl(plugins);
	}


	/**
	 * Loads the main plugin classes, and adds the resulting {@link Plugin}s
	 * to the application.
	 *
	 * @param plugins The list of plugin classes.
	 */
	private void loadPluginsImpl(ArrayList[] plugins) {
		
		for (int p=0; p<plugins.length; p++) {
			int pluginCount = plugins[p]==null ? 0 : plugins[p].size();
			for (int i=0; i<pluginCount; i++) {
				try {
					Thread.sleep(SLEEP_TIME); // Space the Runnables out
					String className = (String)plugins[p].get(i);
					loadPluginImpl(className);
				} catch (InterruptedException ex) { //should never happen
				} catch (final InvalidPluginException e) {
					e.printStackTrace();
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							app.displayException(e);
						}
					});
				}
			}
		}
		
		// Specify that plugins have all been submitted to load
		synchronized (this) {
			pluginSubmissionsCompleted = true;
		}
		
	}

	class LoadPluginRunnable implements Runnable {

		private String name;
		private Constructor cnst;
		Object[] objs;
		
		LoadPluginRunnable(String name, Constructor cnst, Object[] objs) {
			this.name = name;
			this.cnst = cnst;
			this.objs = objs;
		}
		
		public void run() {
			try {
				Plugin p = (Plugin)cnst.newInstance(objs);
				app.addPlugin(p);
			} catch (InvocationTargetException ite) {
				Throwable e = ite.getTargetException();
				e.printStackTrace();
				app.displayException(e);
			} catch (Exception e) {
				e.printStackTrace();
				app.displayException(e);
			} finally {
				synchronized (this) {
					loadingPluginCount--;
				}
			}
		}
		
	}

}