package com.boatyExcited;

import com.google.inject.Provides;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Pattern;
import javax.inject.Inject;

import lombok.AccessLevel;
import static com.boatyExcited.Sound.levelup;
import static com.boatyExcited.Sound.combat_task;
import static com.boatyExcited.Sound.collLog;
import static com.boatyExcited.Sound.death;
import static com.boatyExcited.Sound.money;
import static com.boatyExcited.Sound.quest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.RuneLite;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.grounditems.GroundItemsConfig;
import net.runelite.client.plugins.grounditems.GroundItemsPlugin;
import okhttp3.OkHttpClient;

@Slf4j
@PluginDescriptor(
	name = "Boaty Hype man"
)
@PluginDependency(GroundItemsPlugin.class)
public class boatyExcitedPlugin extends Plugin {
	private static final String DELETE_WARNING_FILENAME = "EXTRA_FILES_WILL_BE_DELETED_BUT_FOLDERS_WILL_REMAIN";
	private static final File DOWNLOAD_DIR = new File(RuneLite.RUNELITE_DIR.getPath() + File.separator + "boaty-excited");
	private static final File DELETE_WARNING_FILE = new File(DOWNLOAD_DIR, DELETE_WARNING_FILENAME);

	@Inject
	private Client client;
	@Getter(AccessLevel.PACKAGE)
	@Inject
	private ClientThread clientThread;
	@Inject
	private ScheduledExecutorService executor;
	@Inject
	private ItemManager itemManager;
	private static final Random random = new Random();
	@Inject
	private GroundItemsConfig groundItemsConfig;
	@Inject
	private boatyExcitedConfig config;
	@Inject
	private ConfigManager configManager;
	@Inject
	private SoundEngine soundEngine;
	@Inject
	private OkHttpClient okHttpClient;
	private final Map<Skill, Integer> oldExperience = new EnumMap<>(Skill.class);
	private static final Pattern COLLECTION_LOG_ITEM_REGEX = Pattern.compile("New item added to your collection log:.*");
	private static final Pattern COMBAT_TASK_REGEX = Pattern.compile("Congratulations, you've completed an? (?:\\w+) combat task:.*");
	private static final Pattern QUEST_REGEX = Pattern.compile("Congratulations, you've completed a quest:.*");
	// Pet Drops
	private static final String FOLLOW_PET = "You have a funny feeling like you're being followed";
	private static final String INVENTORY_PET = "You feel something weird sneaking into your backpack";
	private static final String DUPE_PET = "You have a funny feeling like you would have been followed";


	@Override
	protected void startUp() throws Exception {
		executor.submit(() -> {
			SoundFileManager.ensureDownloadDirectoryExists();
			SoundFileManager.downloadAllMissingSounds(okHttpClient);
		});
	}

	@Override
	protected void shutDown() throws Exception {
	}

	private boolean isLoggedIn = false;
	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		if (!this.isLoggedIn) {
			if (event.getGameState().equals(GameState.LOGGED_IN)) {
				if (config.announceLogin()){
					SoundEngine.playSound(Sound.LOGIN);
					this.isLoggedIn = true;
					return;
				}
			}
		}
		if (event.getGameState().equals(GameState.LOGIN_SCREEN) && this.isLoggedIn) {
			// This will only occur when we've just signed out.
			// Next, since we have signed out, let's set the flag to false.
			this.isLoggedIn = false;
			return;
		}
	}

	@Provides
	boatyExcitedConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(boatyExcitedConfig.class);
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void ensureDownloadDirectoryExists() {
		if (!DOWNLOAD_DIR.exists()) {
			DOWNLOAD_DIR.mkdirs();
		}
		try {
			DELETE_WARNING_FILE.createNewFile();
		} catch (IOException ignored) {
		}
	}

	@Subscribe
	public void onItemSpawned(ItemSpawned itemSpawned) {
		final TileItem item = itemSpawned.getItem();
		final int id = item.getId();
		final ItemComposition itemComposition = itemManager.getItemComposition(id);
		final int notifyValue = Integer.parseInt(configManager.getConfiguration("grounditems", "highValuePrice"));
		final String list = configManager.getConfiguration("grounditems", "highlightedItems").toLowerCase();
		if (list.contains(itemComposition.getName().toLowerCase()) || notifyValue <= itemComposition.getPrice()) {
			if (config.announceDrops()){
				SoundEngine.playSound(money[random.nextInt(money.length)]);
				return;
			}
		}
	}

	@Subscribe
	public void onActorDeath(ActorDeath actorDeath) {
		if (config.announceDeath() && actorDeath.getActor() == client.getLocalPlayer()) {
			SoundEngine.playSound(death[random.nextInt(death.length)]);
		}
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied hitsplatApplied) {
		if (hitsplatApplied.getHitsplat().isMine()) {
			if (hitsplatApplied.getHitsplat().getHitsplatType() == HitsplatID.DAMAGE_MAX_ME_ORANGE ||
					hitsplatApplied.getHitsplat().getHitsplatType() == HitsplatID.DAMAGE_ME_ORANGE ||
					hitsplatApplied.getHitsplat().getHitsplatType() == HitsplatID.DAMAGE_MAX_ME

			) {
				if (config.announceMaxHit()){
				SoundEngine.playSound(Sound.MAXHIT);
			}
			}
		}
	}

	@Subscribe
	public void onSoundEffectPlayed(SoundEffectPlayed event) {
		int soundId = event.getSoundId();

		if (config.rubyBoltSpec()) {
			if (soundId == 2911) {
				event.consume();
				SoundEngine.playSound(Sound.RUBYSPEC);
				return;
			}
		}
	}

	@Subscribe
	public void onStatChanged(StatChanged statChanged) {
		final Skill skill = statChanged.getSkill();

		final int xpAfter = client.getSkillExperience(skill);
		final int levelAfter = Experience.getLevelForXp(xpAfter);
		final int xpBefore = oldExperience.getOrDefault(skill, -1);
		final int levelBefore = xpBefore == -1 ? -1 : Experience.getLevelForXp(xpBefore);

		oldExperience.put(skill, xpAfter);
		if (xpBefore == -1 || xpAfter <= xpBefore || levelBefore >= levelAfter) {
			return;
		}

		// If we get here, 'skill' was leveled up!
		if (config.announceLevelUp()) {
			soundEngine.playSound(levelup[random.nextInt(levelup.length)]);
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage) {
		if (chatMessage.getType() != ChatMessageType.GAMEMESSAGE && chatMessage.getType() != ChatMessageType.SPAM) {
			return;
		}
		if (config.announceCollectionLog() && COLLECTION_LOG_ITEM_REGEX.matcher(chatMessage.getMessage()).matches()) {
			soundEngine.playSound(collLog[random.nextInt(collLog.length)]);
		} else if (config.announceCombatAchievement() && COMBAT_TASK_REGEX.matcher(chatMessage.getMessage()).matches()) {
			soundEngine.playSound(combat_task[random.nextInt(combat_task.length)]);
		} else if (config.announceQuestCompletion() && QUEST_REGEX.matcher(chatMessage.getMessage()).matches()) {
			soundEngine.playSound(quest[random.nextInt(quest.length)]);
		}else if (config.announcePets() && chatMessage.getMessage().contains(FOLLOW_PET)){
			soundEngine.playSound(Sound.PET);
		} else if (config.announcePets() && chatMessage.getMessage().contains(INVENTORY_PET)){
			soundEngine.playSound(Sound.PET);
		}else if (config.announcePets() && chatMessage.getMessage().contains(DUPE_PET)){
			soundEngine.playSound(Sound.PET);
		}
	}
}

