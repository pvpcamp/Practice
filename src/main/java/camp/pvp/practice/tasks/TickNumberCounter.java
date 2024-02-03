package camp.pvp.practice.tasks;

import lombok.Getter;

@Getter
public class TickNumberCounter implements Runnable{

    private int currentTick;

    @Override
    public void run() {
        currentTick++;
    }
}
