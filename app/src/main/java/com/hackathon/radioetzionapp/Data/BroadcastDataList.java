package com.hackathon.radioetzionapp.Data;

import java.util.List;

public class BroadcastDataList {

// TODO

    private List<BroadcastDataClass> broadcastsList;

    private static BroadcastDataList broadcasts;

    public static BroadcastDataList getInstance() {
        if (broadcasts == null) {
            broadcasts = new BroadcastDataList();
        }
        return broadcasts;
    }

    private BroadcastDataList() {

        // TODO generate list from DS (local)


    }


    public List<BroadcastDataClass> getDataList()
    {
        return broadcastsList;
    }
}
