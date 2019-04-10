package nl.ivoka.task;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public interface TaskScheduler<T extends JavaPlugin> {

    T getPlugin();

    default BukkitTask run(Runnable task) {
        return new BukkitRunnable() {
            public void run() {
                try {
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTask(getPlugin());
    }

    default BukkitTask runLater(Runnable task, int delay) {
        return new BukkitRunnable() {
            public void run() {
                try {
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskLater(getPlugin(), delay);
    }

    default BukkitTask runAsync(Runnable task) {
        return new BukkitRunnable() {
            public void run() {
                try {
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(getPlugin());
    }

    default BukkitTask runAsyncLater(Runnable task, int delay) {
        return new BukkitRunnable() {
            public void run() {
                try {
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskLaterAsynchronously(getPlugin(), delay);
    }

    default BukkitTask runTimer(Runnable task, int period) {
        return new BukkitRunnable() {
            public void run() {
                try {
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskTimer(getPlugin(), 0, period);
    }

    default BukkitTask runTimer(Runnable task, int delay, int period) {
        return new BukkitRunnable() {
            public void run() {
                try {
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskTimer(getPlugin(), delay, period);
    }

    default BukkitTask runTimer(Runnable task, int delay, int period, int steps) {
        return new BukkitRunnable() {
            int step = 0;

            public void run() {
                if (step >= steps) {
                    this.cancel();
                    return;
                }

                try {
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                step++;
            }
        }.runTaskTimer(getPlugin(), delay, period);
    }

    default BukkitTask runTimerAsync(Runnable task, int delay, int period) {
        return new BukkitRunnable() {
            public void run() {
                try {
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskTimerAsynchronously(getPlugin(), delay, period);
    }
}