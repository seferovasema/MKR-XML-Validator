package az.afb.mkrvalidator.model;

import lombok.Builder;
import lombok.Data;

@Data
public class Guarantee {
    private String id;
    private String name;
    private String dateOfBirth;
    private String placeOfBirth;
    private String pinCode;
}