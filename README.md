# SODevelopment
## Skeleton files for SO development.
This library is useful only if you are doing any development targetting *SO Platform*.

*SO Platform* is a Java Application run-time environment that allows developers to develop and deploy
application logic as isolated logic units. The logic units can be "data classes" to
represent "data objects" or pure logic for processing, UI, reporting etc. It uses
[PostgreSQL](https://www.postgresql.org/) as the backend for storing data as well as logic
(yes, the datbase saves logic also). The UI part is built on top of
[Vaadin](https://vaadin.com) and a couple of add-ons of Vaadin (Please see the POM file
for understanding dependencies). [Apache POI](https://poi.apache.org/) library is
utilized for the creation of Excel compatible output.

### Maven
```
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/syampillai/SODevelopment</url>
    </repository>
</repositories>
```
```
<dependency>
  <groupId>com.storedobject</groupId>
  <artifactId>so-development</artifactId>
  <version>3.6.4</version>
</dependency>
```