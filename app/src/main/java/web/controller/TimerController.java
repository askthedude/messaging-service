package web.controller;

import service.TimerService;

public class TimerController {
    private final TimerService service;

    public TimerController(TimerService service) {
        this.service = service;
    }

    public void addNewInitializedTimer(String uniqueid, long initValue){
        service.addNewInitializedTimer(uniqueid, initValue);
    }

    public void changeTimerValueForId(String id, long value){
        service.changeTimerValueForId(id, value);
    }

    public void removeTimerWithId(long id){
        var uniqueId = service.getUniqueIdForTimerId(id);
        if(uniqueId != null){
            service.removeTimerWithId(uniqueId);
        }
    }

    public void updateTimerIdForUniqueId(String uniqueId, long timerId){
        service.updateTimerIdForUniqueId(uniqueId, timerId);
    }

}
