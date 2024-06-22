package camp.pvp.practice.queue;

public enum QueueMenuType {
    DUEL_UNRANKED, DUEL_RANKED, MINIGAME;


    @Override
    public String toString() {
        switch (this) {
            case DUEL_UNRANKED:
                return "Duel (Unranked)";
            case DUEL_RANKED:
                return "Duel (Ranked)";
            case MINIGAME:
                return "Minigame";
            default:
                return "None";
        }
    }
}
