package az.afb.mkrvalidator.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Collateral {
    private String collateralTypeCode;
    private String anyInfoToDisting;
    private Double marketValue;
    private String registryNo;
    private String registryDate;
    private String registryAgency;
}