package com.digigate.engineeringmanagement.storemanagement.fileupload;

import com.digigate.engineeringmanagement.planning.entity.Part;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Inventory Report DTO
 *
 * @author Sayem Hasnat
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryReportDto {
    @JsonProperty("S/N")
    public Long sn;

    @JsonProperty("ROW_NUMBER")
    public int rowNumber;

    @JsonProperty("PART NUMBER")
    public String partNumber;

    @JsonProperty("NAME")
    public String name;

    @JsonProperty("UNIT")
    public String unit;

    @JsonProperty("SH./LIFE")
    public String shLife;

    @JsonProperty("Date")
    public String date;

    @JsonProperty("U/Price(USD)")
    public String uPriceUSD;

    @JsonProperty("OpeningQty")
    public Double openingQty;

    @JsonProperty("OpeningValue(USD)")
    public String openingValueUsd;

    @JsonProperty("PurchasedQty")
    public Double purchasedQty;

    @JsonProperty("PurchasedValue(USD)")
    public String purchasedValueUsd;

    @JsonProperty("IssuedQty")
    public Double issuedQty;

    @JsonProperty("IssuedValue(USD)")
    public String issuedValueUsd;

    @JsonProperty("Qty")
    public Double qty;

    @JsonProperty("ClosingQty")
    public Double closingQty;

    @JsonProperty("ClosingValue(USD)")
    public String closingValueUsd;

    @JsonProperty("Serial Number")
    public List<String> serialNumber;

    @JsonIgnore
    public String lotNumber;

    @JsonIgnore
    public String grnConsumable;

    @JsonIgnore
    public Part part;

    @JsonProperty("G.R.N")
    public List<String> grn;

    @JsonProperty("ISSUED A/C")
    public String issuedAc;

    @JsonProperty("Location")
    public String location;

    @JsonProperty("Alt P/No.")
    public String altPNo;
}
