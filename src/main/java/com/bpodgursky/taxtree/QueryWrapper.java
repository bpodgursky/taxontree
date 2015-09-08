package com.bpodgursky.taxtree;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.bpodgursky.taxtree.tables.ScientificNameElement;
import com.bpodgursky.taxtree.tables.Taxon;
import com.bpodgursky.taxtree.tables.TaxonNameElement;
import com.google.common.collect.Lists;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

import static com.bpodgursky.taxtree.Tables.COMMON_NAME;
import static com.bpodgursky.taxtree.Tables.COMMON_NAME_ELEMENT;
import static com.bpodgursky.taxtree.Tables.SCIENTIFIC_NAME_ELEMENT;
import static com.bpodgursky.taxtree.Tables.SOURCE_DATABASE;
import static com.bpodgursky.taxtree.Tables.TAXON;
import static com.bpodgursky.taxtree.Tables.TAXONOMIC_RANK;
import static com.bpodgursky.taxtree.Tables.TAXON_NAME_ELEMENT;

public class QueryWrapper {

  private final DSLContext context;

  public QueryWrapper(Connection conn) {
    this.context = DSL.using(conn, SQLDialect.MYSQL);
  }

  public Collection<TaxonNodeInfo> parents(long childTaxonId) {

    Taxon child = TAXON.as("child_taxon");
    Taxon parent = TAXON.as("parent_taxon");
    TaxonNameElement childName = TAXON_NAME_ELEMENT.as("child_taxon_name");
    TaxonNameElement parentName = TAXON_NAME_ELEMENT.as("parent_taxon_name");
    ScientificNameElement sciName = SCIENTIFIC_NAME_ELEMENT.as("child_sci_name");

    return context.select()
        .from(child)
        .join(childName)
        .on(child.ID.equal(childName.TAXON_ID))
        .join(parent)
        .on(parent.ID.equal(childName.PARENT_ID))
        .leftOuterJoin(parentName)
        .on(parent.ID.equal(parentName.TAXON_ID))
        .leftOuterJoin(sciName)
        .on(sciName.ID.equal(parentName.SCIENTIFIC_NAME_ELEMENT_ID))
        .where(child.ID.equal(UInteger.valueOf(childTaxonId))).fetch().stream().map(record -> new TaxonNodeInfo(
            record.into(parent),
            record.into(parentName),
            record.into(sciName)
        )).collect(Collectors.toList());
  }

  public TaxonDescriptionInfo detail(long taxonId) {
    return Accessors.only(context.select()
        .from(TAXON)
        .leftOuterJoin(TAXON_NAME_ELEMENT)
        .on(TAXON.ID.equal(TAXON_NAME_ELEMENT.TAXON_ID))
        .leftOuterJoin(SCIENTIFIC_NAME_ELEMENT)
        .on(SCIENTIFIC_NAME_ELEMENT.ID.equal(TAXON_NAME_ELEMENT.SCIENTIFIC_NAME_ELEMENT_ID))
        .leftOuterJoin(TAXONOMIC_RANK)
        .on(TAXON.TAXONOMIC_RANK_ID.equal(TAXONOMIC_RANK.ID))
        .leftOuterJoin(SOURCE_DATABASE)
        .on(TAXON.SOURCE_DATABASE_ID.equal(SOURCE_DATABASE.ID))
        .where(TAXON.ID.equal(UInteger.valueOf(taxonId))).fetch().stream().map(record -> new TaxonDescriptionInfo(
            record.into(TAXON),
            record.into(TAXON_NAME_ELEMENT),
            record.into(SCIENTIFIC_NAME_ELEMENT),
            record.into(TAXONOMIC_RANK),
            record.into(SOURCE_DATABASE))).collect(Collectors.toList()));

  }

  public Collection<TaxonNodeInfo> children(long parentTaxonId) {
    return taxonInfo(TAXON_NAME_ELEMENT.PARENT_ID.equal(UInteger.valueOf(parentTaxonId)));
  }

  public TaxonNodeInfo id(long taxonId) {
    return Accessors.only(taxonInfo(TAXON.ID.equal(UInteger.valueOf(taxonId))));
  }

  public List<String> scientificName(String commonName) {

    Result<Record> taxon = context.select()
        .from(COMMON_NAME_ELEMENT)
        .join(COMMON_NAME)
        .on(COMMON_NAME_ELEMENT.ID.equal(COMMON_NAME.COMMON_NAME_ELEMENT_ID))
        .join(TAXON)
        .on(COMMON_NAME.TAXON_ID.equal(TAXON.ID))
        .where(COMMON_NAME_ELEMENT.NAME.equal(commonName))
        .fetch();

    UInteger taxonId = Accessors.only(taxon).getValue(TAXON.ID);
    List<String> names = Lists.newArrayList();

    while (taxonId != null) {
      Record only = Accessors.only(taxonQuery(taxonId.longValue())
          .fetch());
      names.add(only.getValue(SCIENTIFIC_NAME_ELEMENT.NAME_ELEMENT));
      taxonId = only.getValue(TAXON_NAME_ELEMENT.PARENT_ID);
    }

    return names;

  }

  private Collection<TaxonNodeInfo> taxonInfo(Condition condition) {
    return context.select()
        .from(TAXON)
        .leftOuterJoin(TAXON_NAME_ELEMENT)
        .on(TAXON.ID.equal(TAXON_NAME_ELEMENT.TAXON_ID))
        .leftOuterJoin(SCIENTIFIC_NAME_ELEMENT)
        .on(SCIENTIFIC_NAME_ELEMENT.ID.equal(TAXON_NAME_ELEMENT.SCIENTIFIC_NAME_ELEMENT_ID))
        .where(condition).fetch().stream().map(record -> new TaxonNodeInfo(
            record.into(TAXON),
            record.into(TAXON_NAME_ELEMENT),
            record.into(SCIENTIFIC_NAME_ELEMENT)
        )).collect(Collectors.toList());
  }

  private Select<Record> taxonQuery(long taxonId) {
    return context.select()
        .from(TAXON)
        .join(TAXON_NAME_ELEMENT)
        .on(TAXON.ID.equal(TAXON_NAME_ELEMENT.TAXON_ID))
        .join(SCIENTIFIC_NAME_ELEMENT)
        .on(SCIENTIFIC_NAME_ELEMENT.ID.equal(TAXON_NAME_ELEMENT.SCIENTIFIC_NAME_ELEMENT_ID))
        .where(TAXON.ID.equal(UInteger.valueOf(taxonId)))
        .limit(1);
  }

}
