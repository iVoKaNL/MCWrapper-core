package nl.ivoka.listeners;

import nl.ivoka.MCWrapper;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;

public class EventListener implements Listener {
    private MCWrapper plugin;
    private String worldName;

    public EventListener(MCWrapper plugin) { this.plugin = plugin; }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) { plugin.getConnectorServer().fireEvent("SERVER STATUS DONE"); }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) { plugin.getConnectorServer().fireEvent("SERVER COMMAND "+event.getCommand()); }

    @EventHandler
    public void onWorldSave(WorldSaveEvent event) { if (event.getWorld().equals(Bukkit.getWorlds().get(0))) plugin.getConnectorServer().fireEvent("SERVER SAVING"); }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) { plugin.getConnectorServer().fireEvent("PLAYER JOIN "+event.getPlayer().getUniqueId().toString()); }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) { plugin.getConnectorServer().fireEvent("PLAYER QUIT "+event.getPlayer().getUniqueId().toString()); }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) { plugin.getConnectorServer().fireEvent("PLAYER CHAT "+event.getPlayer().getUniqueId().toString()+" "+event.getMessage()); }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) { plugin.getConnectorServer().fireEvent("PLAYER TELEPORT "+event.getPlayer().getUniqueId().toString()+" "+event.getTo().getX()+" "+event.getTo().getY()+" "+event.getTo().getZ()+" "+event.getTo().getYaw()+" "+event.getTo().getPitch()); }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) { plugin.getConnectorServer().fireEvent("PLAYER COMMAND "+event.getPlayer().getUniqueId().toString()+" "+event.getMessage().substring(1)); }
}
