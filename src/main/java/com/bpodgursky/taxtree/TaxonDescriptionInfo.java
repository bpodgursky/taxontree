package com.bpodgursky.taxtree;

import java.util.Map;

import com.bpodgursky.taxtree.tables.records.CommonNameElementRecord;
import com.bpodgursky.taxtree.tables.records.CommonNameRecord;
import com.bpodgursky.taxtree.tables.records.ScientificNameElementRecord;
import com.bpodgursky.taxtree.tables.records.SourceDatabaseRecord;
import com.bpodgursky.taxtree.tables.records.TaxonNameElementRecord;
import com.bpodgursky.taxtree.tables.records.TaxonRecord;
import com.bpodgursky.taxtree.tables.records.TaxonomicRankRecord;

public class TaxonDescriptionInfo {

  private final Map<String, Object> taxonRecord;
  private final Map<String, Object> taxonNameElementRecord;
  private final Map<String, Object> scientificNameElementRecord;
  private final Map<String, Object> taxonomicRank;
  private final Map<String, Object> sourceDatabase;
  private final Map<String, Object> commonNameRecord;
  private final Map<String, Object> commonNameElementRecord;

  public TaxonDescriptionInfo(TaxonRecord taxonRecord,
                              TaxonNameElementRecord taxonNameElementRecord,
                              ScientificNameElementRecord scientificNameElementRecord,
                              TaxonomicRankRecord rankRecord,
                              SourceDatabaseRecord sourceDatabaseRecord,
                              CommonNameRecord commonNameRecord,
                              CommonNameElementRecord commonNameElementRecord) {
    this.taxonRecord = taxonRecord.intoMap();
    this.taxonNameElementRecord = taxonNameElementRecord.intoMap();
    this.scientificNameElementRecord = scientificNameElementRecord.intoMap();
    this.taxonomicRank = rankRecord.intoMap();
    this.sourceDatabase = sourceDatabaseRecord.intoMap();
    this.commonNameRecord = commonNameRecord.intoMap();
    this.commonNameElementRecord = commonNameElementRecord.intoMap();
  }

  @Override
  public String toString() {
    return "TaxonDescriptionInfo{" +
        "taxonRecord=" + taxonRecord +
        ", taxonNameElementRecord=" + taxonNameElementRecord +
        ", scientificNameElementRecord=" + scientificNameElementRecord +
        ", taxonomicRank=" + taxonomicRank +
        ", sourceDatabase=" + sourceDatabase +
        ", commonNameRecord=" + commonNameRecord +
        ", commonNameElementRecord=" + commonNameElementRecord +
        '}';
  }
}
