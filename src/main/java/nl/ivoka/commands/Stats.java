package nl.ivoka.commands;

import nl.ivoka.MCWrapper;
import nl.ivoka.mongo.data.entity.PlayerStatistics;
import nl.ivoka.mongo.repository.PlayerStatisticsRepository;
import nl.ivoka.task.TaskScheduler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Stats implements CommandExecutor, TaskScheduler<MCWrapper> {

    private MCWrapper plugin;

    public Stats(MCWrapper plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            runAsync(() -> {
                PlayerStatisticsRepository repository = plugin.getMongoDataSource().getRepository(PlayerStatisticsRepository.class);
                PlayerStatistics statistics = repository.read(((Player) sender).getUniqueId(), true);

                sender.sendMessage(ChatColor.GOLD + "Your stats:");
                sender.sendMessage(String.format(ChatColor.GOLD + "  - Kills: %d", statistics.getKills()));
                sender.sendMessage(String.format(ChatColor.GOLD + "  - Deaths: %d", statistics.getDeaths()));
                sender.sendMessage(String.format(ChatColor.GOLD + "  - Logins: %d", statistics.getLogins()));
                sender.sendMessage(String.format(ChatColor.GOLD + "  - Teleports: %d", statistics.getTeleports()));
            });
        }

        return true;
    }

    @Override
    public MCWrapper getPlugin() { return plugin; }
}