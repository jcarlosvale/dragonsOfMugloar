package com.dragonsofmugloar;

import com.dragonsofmugloar.api.exception.CustomException;
import com.dragonsofmugloar.api.service.DragonsOfMugloarService;

public class DragonsOfMugloarApp {
    public static void main( String[] args ) throws CustomException {
        DragonsOfMugloarService service = new DragonsOfMugloarService();
        String operations = service.playTheGame();
        System.out.println(operations);
    }
}
