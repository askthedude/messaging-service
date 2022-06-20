package service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TimerService {
    private final Map<String, TimerEntry> timers = new ConcurrentHashMap<>();
    public final Map<Long, String> timerIdToUniqueId = new ConcurrentHashMap<>();

    public void addNewInitializedTimer(String id, long currentValue){
        timers.put(id, new TimerEntry(currentValue, NOT_ASSIGNED_YET));
    }

    public long changeTimerValueForId(String id, long delta){
        timers.computeIfPresent(id, (key, oldVal) -> new TimerEntry(oldVal.value + delta, oldVal.timerId));
        var timerEntry = timers.get(id);
        if(timerEntry != null && timerEntry.value <= 0 ) {
            return timerEntry.timerId;
        }
        return CONTINUE_FLAG;
    }

    public void removeTimerWithId(String id){
        TimerEntry remove = timers.remove(id);
        if(remove != null){
            timerIdToUniqueId.remove(remove.timerId);
        }
    }

    public void updateTimerIdForUniqueId(String uniqueId, long timerId){
        timers.get(uniqueId).timerId = timerId;
    }

    public String getUniqueIdForTimerId(long timerId){
        return timerIdToUniqueId.get(timerId);
    }

    public void removeTimerWithTimerId(long timerId){
        var uniqueId = timerIdToUniqueId.get(timerId);
        if(uniqueId != null){
            timerIdToUniqueId.remove(timerId);
            timers.remove(uniqueId);
        }
    }

    public TimerEntry getTimerEntryForUniqueId(String uniqueId){
        return timers.get(uniqueId);
    }

    public static final long NOT_ASSIGNED_YET = -1;
    public static final long CONTINUE_FLAG = -1;

    public static class TimerEntry{
        public long value;
        public long timerId;

        public TimerEntry(long value, long timerId) {
            this.value = value;
            this.timerId = timerId;
        }
    }
}
