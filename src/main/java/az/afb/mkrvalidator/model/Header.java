package az.afb.mkrvalidator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Header {

    @JacksonXmlProperty(localName = "BankID")
    private String BankID;

    @JacksonXmlProperty(localName = "BankName")
    private String BankName;

    @JacksonXmlProperty(localName = "ReportingDate")
    private String ReportingDate;
}