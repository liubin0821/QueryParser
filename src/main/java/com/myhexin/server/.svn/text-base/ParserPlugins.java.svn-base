package com.myhexin.server;

import java.util.ArrayList;

import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginAbstract;
import com.myhexin.qparser.phrase.parsePrePlugins.PhraseParserPrePluginAbstract;

public class ParserPlugins {
	/*public String[] PRETREAT_PLUGINS = null;
	public String[] PLUGINS = null;*/
	public ArrayList<PhraseParserPrePluginAbstract> pre_plugins_ = new ArrayList<PhraseParserPrePluginAbstract>();
	public ArrayList<PhraseParserPluginAbstract> plugins_ = new ArrayList<PhraseParserPluginAbstract>();
	public ArrayList<PhraseParserPluginAbstract> post_plugins_ = new ArrayList<PhraseParserPluginAbstract>();

	/**
	 * @author 徐祥
	 * @createTime 2014-04-30 19:26
	 * @description 设置预处理插件和处理插件
	 */
	public ParserPlugins(ArrayList<PhraseParserPrePluginAbstract> pre_plugins,
			ArrayList<PhraseParserPluginAbstract> plugins,
			ArrayList<PhraseParserPluginAbstract> post_plugins) {

		pre_plugins_ = pre_plugins;
		plugins_ = plugins;
		post_plugins_ = post_plugins;
	}

	/*public ParserPlugins(String parserPluginsConfFile) {
		try {
			loadPlugins(Util.readTxtFile(parserPluginsConfFile, true));
		} catch (Exception e) {

		}
		// init pre plugins
		for (String pre_str : PRETREAT_PLUGINS) {
			PhraseParserPrePluginAbstract pre_plugin = PhraseParserPrePluginFactory
					.Create(pre_str);
			if (pre_plugin != null)
				pre_plugins_.add(pre_plugin);
		}
		// init plugins
		for (String str : PLUGINS) {
			PhraseParserPluginAbstract plugin = PhraseParserPluginFactory
					.Create(str);
			if (plugin != null)
				plugins_.add(plugin);
		}
	}

	private void loadPlugins(List<String> confLines) {
		for (int i = 0; i < confLines.size(); i++) {
			String line = confLines.get(i).trim();
			if (line.length() == 0 || line.charAt(0) == '#') {
				continue;
			}
			int pos = line.indexOf('=');
			String key = line.substring(0, pos);
			String val = line.substring(pos + 1);
			if (key.equals("pretreat_plugins")) {
				PRETREAT_PLUGINS = val.split(",");
			} else if (key.equals("plugins")) {
				PLUGINS = val.split(",");
			}
		}
	}*/
}
