package web.controller;

import service.TimerService;

import static utils.Constants.NO_TIMER_RESULT;

public class TimerController {

    private final TimerService service;

    public TimerController(TimerService service) {
        this.service = service;
    }

    public void addNewInitializedTimer(String uniqueid, long initValue){
        service.addNewInitializedTimer(uniqueid, initValue);
    }

    public long changeTimerValueForId(String id, long value){
        return service.changeTimerValueForId(id, value);
    }

    public void removeTimerWithId(long id){
        service.removeTimerWithTimerId(id);
    }

    public void removeTimerUniqueId(String uniqueId){
        service.removeTimerWithId(uniqueId);
    }


    public void updateTimerIdForUniqueId(String uniqueId, long timerId){
        service.updateTimerIdForUniqueId(uniqueId, timerId);
    }

    public long getResultForTimerId(String uniqueId){
        var result = service.getTimerEntryForUniqueId(uniqueId);
        if(result == null){
            return NO_TIMER_RESULT;
        }
        return result.value;
    }
}
