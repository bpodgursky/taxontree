### Taxon Tree ###

This is a simple visualization of the taxonomic tree of all known species, using the database provided by the [Catalog of Life](http://www.catalogueoflife.org/).  The site is hosted here:

http://taxontree.bpodgursky.com/

![screenshot](http://i.imgur.com/YIi79DM.png)

Many rough edges; fixes / PRs welcome.

To set up an instance, just download the latest [checklist archive](http://www.catalogueoflife.org/content/annual-checklist-archive) SQL dump into a MySQL instance and run the embedded [jetty server](https://github.com/bpodgursky/taxontree/blob/master/src/main/java/com/bpodgursky/taxtree/WebServer.java).

 

