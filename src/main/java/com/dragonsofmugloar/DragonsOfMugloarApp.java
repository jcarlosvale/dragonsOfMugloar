package com.dragonsofmugloar;

import com.dragonsofmugloar.api.service.DragonsOfMugloarService;
import com.dragonsofmugloar.api.service.util.Util;

/**
 * Main APP used to execute the code.
 */
public class DragonsOfMugloarApp {
    public static void main( String[] args ) {
        System.out.println("Starting the game and running...");
        DragonsOfMugloarService service = new DragonsOfMugloarService();
        System.out.println(Util.logGameInfo(service.playTheGame()));
    }
}
