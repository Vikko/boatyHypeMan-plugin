package com.boatyExcited;

import com.google.inject.Provides;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
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
import net.runelite.client.audio.AudioPlayer;

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
	private AudioPlayer audioPlayer;
//	@Inject
//	private SoundEngine soundEngine;
	@Inject
	private OkHttpClient okHttpClient;
	private final Map<Skill, Integer> oldExperience = new EnumMap<>(Skill.class);
	private static final Pattern COLLECTION_LOG_ITEM_REGEX = Pattern.compile("New item added to your collection log:.*");
	private static final Pattern COMBAT_TASK_REGEX = Pattern.compile("CA_ID:\\d+\\|Congratulations, you've completed an? \\w+ combat task:.*");
	private static final Pattern LEAGUES_TASK_REGEX = Pattern.compile("Congratulations, you've completed an? (?:\\w+) task:.*");
	private static final Pattern QUEST_REGEX = Pattern.compile("Congratulations, you've completed a quest:.*");
	private static final Pattern HIGHLIGHTED_ITEM = Pattern.compile("^(.+)([<>])([0-9]+)$");
	// Pet Drops
	private static final String FOLLOW_PET = "You have a funny feeling like you're being followed";
	private static final String INVENTORY_PET = "You feel something weird sneaking into your backpack";
	private static final String DUPE_PET = "You have a funny feeling like you would have been followed";


	@Override
	protected void startUp() throws Exception {
		executor.submit(() -> {
			SoundFileManager.prepareSoundFiles(okHttpClient);
		});
	}

	@Override
	protected void shutDown() throws Exception {
	}

	private void playSound(Sound sound){
		float gain = 20f * (float) Math.log10(config.announcementVolume() / 100f);
		try {
			audioPlayer.play(SoundFileManager.getSoundFile(sound), gain);
		} catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

	private boolean isLoggedIn = false;
	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		if (!this.isLoggedIn) {
			if (event.getGameState().equals(GameState.LOGGED_IN)) {
				if (config.announceLogin()){
					playSound(Sound.LOGIN);
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
	
	private static boolean itemListContains(final String list, final String itemName, final int quantity)
	{
		final String[] listItems = list.split(",");
		
		for (String listItem: listItems)
		{
			listItem = listItem.trim();
			
			// Check item name first, quicker;
			if (listItem.equalsIgnoreCase(itemName))
			{
				return true;
			}
			
			final Matcher m = HIGHLIGHTED_ITEM.matcher(listItem);
			if (!m.find())
				continue;
			
			if (!m.group(1).equalsIgnoreCase(itemName))
				continue;
			
			final String comparison = m.group(2);
			final int quantityLimit = Integer.parseInt(m.group(3));
			if (comparison.equals(">"))
			{
				if (quantity > quantityLimit)
				{
					return true;
				}
			}
			else
			{
				if (quantity < quantityLimit)
				{
					return true;
				}
			}
		}
		
		return false;
	}

	@Subscribe
	public void onItemSpawned(ItemSpawned itemSpawned) {
		// If sound disabled, exit method without processing
		if (!config.announceDrops())
			return;
		
		final TileItem item = itemSpawned.getItem();
		final int id = item.getId();
		final int quantity = item.getQuantity();
		final ItemComposition itemComposition = itemManager.getItemComposition(id);
		final String itemName = itemComposition.getName();

		String hiddenItems = "";
		if (!config.dropCustomConfig()) {
			hiddenItems = configManager.getConfiguration("grounditems", "hiddenItems");
		} else {
			hiddenItems = config.dropHiddenItems();
		}
		// Check hidden list, exit if found
		if (itemListContains(hiddenItems, itemName, quantity))
			return;

		// Check notify value first as easiest to check
		int notifyValue = 0;
		if (!config.dropCustomConfig()) {
			notifyValue = Integer.parseInt(configManager.getConfiguration("grounditems", "highValuePrice"));
		} else {
			notifyValue = config.dropAnnouncementValue();
		}
        //Grab price, dependent on what is configured. GE by default.
		int price = 0;
		switch(config.dropAnnouncementType()){
		    case STORE:
		        price = itemComposition.getPrice();
                break;
		    case HA:
		        price = itemComposition.getHaPrice();
                break;
		    case GE:
		    default:
                price = itemManager.getItemPrice(id);

		}

		if (notifyValue <= price) {
			playSound(money[random.nextInt(money.length)]);
			return;
		}

		String highlightedItems = "";
		if (!config.dropCustomConfig()) {
			highlightedItems = configManager.getConfiguration("grounditems", "highlightedItems");
		} else {
			highlightedItems = config.dropHighlightedItems();
		}
		// Check each item in the list individually - prevents false positives due to partial item names, e.g. A drop of "Seaweed" matching highlighted item "Seaweed spore"
		if (itemListContains(highlightedItems, itemName, quantity)){
			playSound(money[random.nextInt(money.length)]);
			return;
		}
	}

	@Subscribe
	public void onActorDeath(ActorDeath actorDeath) {
		if (config.announceDeath() && actorDeath.getActor() == client.getLocalPlayer()) {
			playSound(death[random.nextInt(death.length)]);
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
					playSound(Sound.MAXHIT);
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
				playSound(Sound.RUBYSPEC);
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
			playSound(levelup[random.nextInt(levelup.length)]);
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage) {
		if (chatMessage.getType() != ChatMessageType.GAMEMESSAGE && chatMessage.getType() != ChatMessageType.SPAM) {
			return;
		}
		if (config.announceCollectionLog() && COLLECTION_LOG_ITEM_REGEX.matcher(chatMessage.getMessage()).matches()) {
			playSound(collLog[random.nextInt(collLog.length)]);
		} else if (config.announceCombatAchievement() && COMBAT_TASK_REGEX.matcher(chatMessage.getMessage()).matches()) {
			playSound(combat_task[random.nextInt(combat_task.length)]);
		} else if (config.announceQuestCompletion() && QUEST_REGEX.matcher(chatMessage.getMessage()).matches()) {
			playSound(quest[random.nextInt(quest.length)]);
		} else if (config.announcePets() && chatMessage.getMessage().contains(FOLLOW_PET)){
			playSound(Sound.PET);
		} else if (config.announcePets() && chatMessage.getMessage().contains(INVENTORY_PET)){
			playSound(Sound.PET);
		} else if (config.announcePets() && chatMessage.getMessage().contains(DUPE_PET)){
			playSound(Sound.PET);
		} else if (config.announceLeaguesTask() && LEAGUES_TASK_REGEX.matcher(chatMessage.getMessage()).matches()) {
			playSound(combat_task[random.nextInt(combat_task.length)]);
		}
	}
}

