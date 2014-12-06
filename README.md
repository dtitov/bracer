Features:
=======
-parse math expressions;

-evaluate math expressions;

-convert from infix to postfix notation;

-calculate expression in postfix notation;

-implements the shunting-yard algorithm;

-understands complex numbers.

Usage:
=======
Add the repo to your pom.xml:
```
<repositories>
    <repository>
        <id>bracer-mvn-repo</id>
        <url>https://raw.github.com/autsia/bracer/mvn-repo/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>
```

Then add the dependency:
```
        <dependency>
            <groupId>io.github.autsia</groupId>
            <artifactId>bracer</artifactId>
            <version>~</version>
        </dependency>
```

*Except as otherwise noted, this library is licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)*
