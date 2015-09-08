package com.bpodgursky.taxtree;

import java.util.Map;

import com.bpodgursky.taxtree.tables.records.ScientificNameElementRecord;
import com.bpodgursky.taxtree.tables.records.TaxonNameElementRecord;
import com.bpodgursky.taxtree.tables.records.TaxonRecord;

public class TaxonNodeInfo {

  private final Map<String, Object> taxonRecord;
  private final Map<String, Object> taxonNameElementRecord;
  private final Map<String, Object> scientificNameElementRecord;

  public TaxonNodeInfo(TaxonRecord taxonRecord,
                       TaxonNameElementRecord taxonNameElementRecord,
                       ScientificNameElementRecord scientificNameElementRecord) {
    this.taxonRecord = taxonRecord.intoMap();
    this.taxonNameElementRecord = taxonNameElementRecord.intoMap();
    this.scientificNameElementRecord = scientificNameElementRecord.intoMap();
  }

  public Map<String, Object> getTaxonRecord() {
    return taxonRecord;
  }

  public Map<String, Object> getTaxonNameElementRecord() {
    return taxonNameElementRecord;
  }

  public Map<String, Object> getScientificNameElementRecord() {
    return scientificNameElementRecord;
  }

}
