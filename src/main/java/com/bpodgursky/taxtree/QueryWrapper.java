package com.bpodgursky.taxtree;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.bpodgursky.taxtree.tables.CommonName;
import com.bpodgursky.taxtree.tables.CommonNameElement;
import com.bpodgursky.taxtree.tables.ScientificNameElement;
import com.bpodgursky.taxtree.tables.Taxon;
import com.bpodgursky.taxtree.tables.TaxonNameElement;
import com.google.common.collect.Lists;
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
import static com.bpodgursky.taxtree.Tables.TAXON;
import static com.bpodgursky.taxtree.Tables.TAXON_NAME_ELEMENT;

public class QueryWrapper {

  private final DSLContext context;

  public QueryWrapper(Connection conn) {
    this.context = DSL.using(conn, SQLDialect.MYSQL);
  }

  public Select<Record> taxonQuery(long taxonId) {
    return context.select()
        .from(TAXON)
        .join(TAXON_NAME_ELEMENT)
        .on(TAXON.ID.equal(TAXON_NAME_ELEMENT.TAXON_ID))
        .join(SCIENTIFIC_NAME_ELEMENT)
        .on(SCIENTIFIC_NAME_ELEMENT.ID.equal(TAXON_NAME_ELEMENT.SCIENTIFIC_NAME_ELEMENT_ID))
        .where(TAXON.ID.equal(UInteger.valueOf(taxonId)))
        .limit(1);
  }

  public Collection<TaxonInfo> parentsQuery(long childTaxonId) {

    Taxon child = TAXON.as("child_taxon");
    Taxon parent = TAXON.as("parent_taxon");
    TaxonNameElement childName = TAXON_NAME_ELEMENT.as("child_taxon_name");
    TaxonNameElement parentName = TAXON_NAME_ELEMENT.as("parent_taxon_name");
    ScientificNameElement sciName = SCIENTIFIC_NAME_ELEMENT.as("child_sci_name");
    CommonName commonName = COMMON_NAME.as("child_common_name");
    CommonNameElement commonNameElement = COMMON_NAME_ELEMENT.as("child_common_name_element");

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
        .leftOuterJoin(commonName)
        .on(commonName.TAXON_ID.equal(parent.ID))
        .leftOuterJoin(commonNameElement)
        .on(commonName.COMMON_NAME_ELEMENT_ID.equal(commonNameElement.ID))
        .where(child.ID.equal(UInteger.valueOf(childTaxonId))).fetch().stream().map(record -> new TaxonInfo(
            record.into(parent),
            record.into(parentName),
            record.into(sciName),
            record.into(commonName),
            record.into(commonNameElement)
        )).collect(Collectors.toList());
  }

  public Collection<TaxonInfo> children(long parentTaxonId) {
    return context.select()
        .from(TAXON)
        .leftOuterJoin(TAXON_NAME_ELEMENT)
        .on(TAXON.ID.equal(TAXON_NAME_ELEMENT.TAXON_ID))
        .leftOuterJoin(SCIENTIFIC_NAME_ELEMENT)
        .on(SCIENTIFIC_NAME_ELEMENT.ID.equal(TAXON_NAME_ELEMENT.SCIENTIFIC_NAME_ELEMENT_ID))
        .leftOuterJoin(COMMON_NAME)
        .on(COMMON_NAME.TAXON_ID.equal(TAXON.ID))
        .leftOuterJoin(COMMON_NAME_ELEMENT)
        .on(COMMON_NAME.COMMON_NAME_ELEMENT_ID.equal(COMMON_NAME_ELEMENT.ID))
        .where(TAXON_NAME_ELEMENT.PARENT_ID.equal(UInteger.valueOf(parentTaxonId))).fetch().stream().map(record -> new TaxonInfo(
            record.into(TAXON),
            record.into(TAXON_NAME_ELEMENT),
            record.into(SCIENTIFIC_NAME_ELEMENT),
            record.into(COMMON_NAME),
            record.into(COMMON_NAME_ELEMENT)
        )).collect(Collectors.toList());
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
}
