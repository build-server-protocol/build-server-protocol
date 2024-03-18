<?xml version="1.0" encoding="UTF-8"?>
<project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <modelVersion>4.0.0</modelVersion>

    <groupId>{groupId}</groupId>
    <artifactId>{artifactId}</artifactId>
    <version>{version}</version>

    <dependencies>
{dependencies}
    </dependencies>

    <build>
        <plugins>
           <plugin>
              <groupId>org.sonatype.plugins</groupId>
              <artifactId>nexus-staging-maven-plugin</artifactId>
              <version>1.6.13</version>
              <extensions>true</extensions>
              <configuration>
                 <serverId>ossrh</serverId>
                 <nexusUrl>https://s01.oss.sonatype.org</nexusUrl>
                 <autoReleaseAfterClose>true</autoReleaseAfterClose>
              </configuration>
           </plugin>
        </plugins>
    </build>
</project>
