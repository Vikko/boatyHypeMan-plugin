package com.boatyExcited;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class boatyExcitedPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(boatyExcitedPlugin.class);
		RuneLite.main(args);
	}
}