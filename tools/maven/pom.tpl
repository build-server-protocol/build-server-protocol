<?xml version="1.0" encoding="UTF-8"?>
<project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <modelVersion>4.0.0</modelVersion>

    <groupId>{groupId}</groupId>
    <artifactId>{artifactId}</artifactId>
    <version>{version}</version>

    <packaging>jar</packaging>
    <description>bsp4j</description>
    <url>https://github.com/build-server-protocol/build-server-protocol</url>
    <licenses>
        <license>
            <name>Apache-2.0</name>
            <url>https://www.apache.org/licenses/LICENSE-2.0</url>
            <distribution>repo</distribution>
        </license>
    </licenses>
    <name>bsp4j</name>
    <organization>
        <name>ch.epfl.scala</name>
        <url>https://github.com/build-server-protocol/build-server-protocol</url>
    </organization>
    <scm>
        <url>https://github.com/build-server-protocol/build-server-protocol</url>
        <connection>scm:git:https://github.com/build-server-protocol/build-server-protocol.git</connection>
        <developerConnection>scm:git:git@github.com:build-server-protocol/build-server-protocol.git</developerConnection>
    </scm>
    <developers>
        <developer>
            <id>ckipp01</id>
            <name>Chris Kipp</name>
            <url>https://github.com/ckipp01</url>
            <email>open-source@chris-kipp.io</email>
        </developer>
        <developer>
            <id>jastice</id>
            <name>Justin Kaeser</name>
            <url>https://github.com/jastice</url>
            <email>justin@justinkaeser.com</email>
        </developer>
        <developer>
            <id>agluszak</id>
            <name>Andrzej Głuszak</name>
            <url>https://github.com/agluszak</url>
            <email>andrzej.gluszak@jetbrains.com</email>
        </developer>
    </developers>

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
                 <nexusUrl>https://oss.sonatype.org/</nexusUrl>
                 <autoReleaseAfterClose>true</autoReleaseAfterClose>
              </configuration>
           </plugin>
        </plugins>
    </build>
</project>
