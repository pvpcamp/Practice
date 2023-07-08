package camp.pvp.queue;

public class GameQueue {

    public enum Type {
        UNRANKED, RANKED, PRIVATE;

        @Override
        public String toString() {
            switch(this) {
                case UNRANKED:
                    return "Unranked";
                case RANKED:
                    return "Ranked";
                case PRIVATE:
                    return "Private Game";
                default:
                    return null;
            }
        }
    }
}
