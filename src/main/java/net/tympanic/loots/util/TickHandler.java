package net.tympanic.loots.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import java.util.HashMap;
import java.util.Map;

public class TickHandler {
    private static final Map<Integer, DelayedAction> delayedActions = new HashMap<>();
    private static int nextId = 0;
    public static void init() {
        ServerTickEvents.START_WORLD_TICK.register(world -> {
            if (!delayedActions.isEmpty()) {
                for (int id : delayedActions.keySet()) {
                    DelayedAction delayedAction = delayedActions.get(id);
                    if (delayedAction.isExpired()) delayedAction.destroy();
                    delayedAction.tick();
                }
            }
        });
    }
    public static DelayedAction create(int ticks, Runnable action) {
        int id = nextId++;
        DelayedAction delayedAction = new DelayedAction(ticks, action);
        delayedActions.put(id, delayedAction);
        return delayedAction;
    }

    public static class DelayedAction {
        private final int ticks;
        private final Runnable action;
        private int tickCount;
        public DelayedAction(int ticks, Runnable action) {
            this.ticks = ticks;
            this.action = action;
            this.tickCount = 0;
        }
        public void tick() {
            if (tickCount >= ticks) {
                action.run();
            } else {
                tickCount++;
            }
        }
        public boolean isExpired() {
            return tickCount >= ticks;
        }
        public void destroy() {
            delayedActions.values().remove(this);
        }
    }
}

