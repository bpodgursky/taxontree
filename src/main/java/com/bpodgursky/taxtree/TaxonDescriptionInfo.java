package com.bpodgursky.taxtree;

import java.util.Map;

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

  public TaxonDescriptionInfo(TaxonRecord taxonRecord,
                              TaxonNameElementRecord taxonNameElementRecord,
                              ScientificNameElementRecord scientificNameElementRecord,
                              TaxonomicRankRecord rankRecord,
                              SourceDatabaseRecord sourceDatabaseRecord) {
    this.taxonRecord = taxonRecord.intoMap();
    this.taxonNameElementRecord = taxonNameElementRecord.intoMap();
    this.scientificNameElementRecord = scientificNameElementRecord.intoMap();
    this.taxonomicRank = rankRecord.intoMap();
    this.sourceDatabase = sourceDatabaseRecord.intoMap();
  }
}
