package az.afb.mkrvalidator.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Credit {

    @JacksonXmlProperty(localName = "Borrower")
    private Borrower borrower;

    @JacksonXmlProperty(localName = "CreditStatusCode")
    private String creditStatusCode;

    @JacksonXmlProperty(localName = "DisoutAmountOfCredit")
    private Double disoutAmountOfCredit;

}