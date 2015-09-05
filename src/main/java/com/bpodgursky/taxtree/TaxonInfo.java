package com.bpodgursky.taxtree;

import java.util.Map;

import com.bpodgursky.taxtree.tables.records.CommonNameElementRecord;
import com.bpodgursky.taxtree.tables.records.CommonNameRecord;
import com.bpodgursky.taxtree.tables.records.ScientificNameElementRecord;
import com.bpodgursky.taxtree.tables.records.TaxonNameElementRecord;
import com.bpodgursky.taxtree.tables.records.TaxonRecord;

public class TaxonInfo {

  private final Map<String, Object> taxonRecord;
  private final Map<String, Object> taxonNameElementRecord;
  private final Map<String, Object> scientificNameElementRecord;
  private final Map<String, Object> commonNameRecord;
  private final Map<String, Object> commonNameElementRecord;

  public TaxonInfo(TaxonRecord taxonRecord,
                   TaxonNameElementRecord taxonNameElementRecord,
                   ScientificNameElementRecord scientificNameElementRecord,
                   CommonNameRecord commonNameRecord,
                   CommonNameElementRecord commonNameElementRecord) {
    this.taxonRecord = taxonRecord.intoMap();
    this.taxonNameElementRecord = taxonNameElementRecord.intoMap();
    this.scientificNameElementRecord = scientificNameElementRecord.intoMap();
    this.commonNameRecord = commonNameRecord.intoMap();
    this.commonNameElementRecord = commonNameElementRecord.intoMap();
  }
}
