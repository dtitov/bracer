[![Build Status](http://2.bp.blogspot.com/-gZhL15ujs-E/UP_Yh6BBKTI/AAAAAAAAKu8/3Cuz1k9BxmI/s200/icon_512.png)](http://bracer.autsia.com)

[![Build Status](https://travis-ci.org/dtitov/bracer.png?branch=master)](https://travis-ci.org/dtitov/bracer)

*Except as otherwise noted, this library is licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)*

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
        <url>https://raw.github.com/dtitov/bracer/mvn-repo/</url>
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

Projects that use this library:
=======
<a href="https://play.google.com/store/apps/details?id=com.calcu.sapiens.general">![CalcuSapiens](https://lh4.ggpht.com/wYgLmF9AKg3wSxOP2PwCSKPrelTQuf-fygZo6-TP-mBJEi3vzmCraiQWooqBy4d_rVqL=w705 "CalcuSapiens")</a>
