package org.communitybridge.permissionhandlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.manager.UserManager;

public class PermissionHandlerLuckPerms extends PermissionHandler {
	LuckPermsApi api = null;

	public PermissionHandlerLuckPerms() throws IllegalStateException {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("LuckPerms");
		RegisteredServiceProvider<LuckPermsApi> provider = Bukkit.getServicesManager()
				.getRegistration(LuckPermsApi.class);
		if (provider != null) {
			api = provider.getProvider();

		}
		validate(plugin, "LuckPerms", "4.4.1");
	}

	@Override
	public boolean addToGroup(Player player, String groupName) {
		User user = getUser(player);
		boolean result = user.setPermission(api.getNodeFactory().makeGroupNode(groupName).build()).asBoolean();
		api.getUserManager().cleanupUser(user);
		return result;
	}

	@Override
	public List<String> getGroups(Player player) {
		User user = getUser(player);
		List<String> groups = new ArrayList<String>();
		for (Group group : api.getGroups()) {
			if (user.inheritsGroup(group))
				groups.add(group.getName());
		}
		return groups;
	}

	@Override
	public List<String> getGroupsPure(Player player) {
		return getGroups(player);
	}

	@Override
	public String getPrimaryGroup(Player player) {
		UserManager userManager = api.getUserManager();
		if (player.isOnline())
			return userManager.getUser(player.getUniqueId()).getPrimaryGroup();
		else
			return userManager.getUserOpt(player.getUniqueId()).get().getPrimaryGroup();
	}

	@Override
	public boolean isMemberOfGroup(Player player, String groupName) {
		User user = getUser(player);
		return user.inheritsGroup(getGroup(groupName));

	}

	@Override
	public boolean isPrimaryGroup(Player player, String groupName) {
		User user = getUser(player);
		return user.getPrimaryGroup().equalsIgnoreCase(groupName);
	}

	@Override
	public boolean removeFromGroup(Player player, String groupName) {
		User user = getUser(player);
		boolean result = user.unsetPermission(api.getNodeFactory().makeGroupNode(groupName).build()).asBoolean();
		api.getUserManager().cleanupUser(user);
		return result;
	}

	@Override
	public boolean setPrimaryGroup(Player player, String groupName, String formerGroupName) {
		User user = getUser(player);
		boolean result = user.setPrimaryGroup(groupName).asBoolean();
		api.getUserManager().cleanupUser(user);
		return result;
	}

	@Override
	public boolean supportsPrimaryGroups() {
		return true;
	}

	private User getUser(Player player) {
		UserManager userManager = api.getUserManager();
		if (player.isOnline())
			return userManager.getUser(player.getUniqueId());
		else
			return userManager.getUserOpt(player.getUniqueId()).get();
	}

	private Group getGroup(String groupName) {
		return api.getGroup(groupName);
	}

}
