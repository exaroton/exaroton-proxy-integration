package com.exaroton.proxy;

import net.kyori.adventure.text.format.TextColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
	public static final String PLUGIN_ID = "exaroton";
	public static final Logger LOG = LoggerFactory.getLogger(PLUGIN_ID);
	public static final String CHANNEL_ID = PLUGIN_ID + ":command";
	public static final TextColor EXAROTON_GREEN = TextColor.color(0x19ba19);
}
