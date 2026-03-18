package az.afb.mkrvalidator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditInfo {

    @JacksonXmlProperty(localName = "Header")
    private Header header;

    @JacksonXmlProperty(localName = "Credits")
    private Credits credits;
}