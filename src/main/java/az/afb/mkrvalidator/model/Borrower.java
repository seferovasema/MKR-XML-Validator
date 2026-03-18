package az.afb.mkrvalidator.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Borrower {

    @JacksonXmlProperty(localName = "id")
    private String id;

    @JacksonXmlProperty(localName = "name")
    private String name;

    @JacksonXmlProperty(localName = "CountryCode")
    private String countryCode;

    @JacksonXmlProperty(localName = "BankruptcyStatus")
    private String bankruptcyStatus;

    @JacksonXmlProperty(localName = "DateOfBirth")
    private String dateOfBirth;

    @JacksonXmlProperty(localName = "PlaceOfBirth")
    private String placeOfBirth;

    @JacksonXmlProperty(localName = "PinCode")
    private String pinCode;

}