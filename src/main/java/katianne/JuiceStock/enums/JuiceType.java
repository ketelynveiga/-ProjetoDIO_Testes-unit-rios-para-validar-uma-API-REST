package katianne.JuiceStock.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JuiceType { 

    FOCUSED("Focused"),
    FRUITSHOOT("Fruit Shoot"),
    LIFE("Life"),
    SUPERFRUIT("Superfruit"),
    DELVALLEFRUT("Del Valle Frut"),
    DELVALLEFRESH("Del valle Fresh");
    

    private final String description;
}
