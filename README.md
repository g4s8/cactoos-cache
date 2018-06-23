# cactoos-cache
Caching primitives for [Cactoos](https://github.com/yegor256/cactoos) library.

[![EO principles respected here](http://www.elegantobjects.org/badge.svg)](http://www.elegantobjects.org)
[![DevOps By Rultor.com](http://www.rultor.com/b/g4s8/cactoos-cache)](http://www.rultor.com/p/g4s8/cactoos-cache)

[![Bintray](https://api.bintray.com/packages/g4s8/mvn/com.g4s8.cactoos-cache/images/download.svg)](https://bintray.com/g4s8/mvn/com.g4s8.cactoos-cache/_latestVersion)
[![Build Status](https://img.shields.io/travis/g4s8/cactoos-cache.svg?style=flat-square)](https://travis-ci.org/g4s8/cactoos-cache)
[![Build status](https://ci.appveyor.com/api/projects/status/ahhde7mposa3ra9w?svg=true)](https://ci.appveyor.com/project/g4s8/cactoos-cache)
[![PDD status](http://www.0pdd.com/svg?name=g4s8/cactoos-cache)](http://www.0pdd.com/p?name=g4s8/cactoos-cache)
[![License](https://img.shields.io/github/license/g4s8/cactoos-cache.svg?style=flat-square)](https://github.com/g4s8/cactoos-cache/blob/master/LICENSE)
[![Test Coverage](https://img.shields.io/codecov/c/github/g4s8/cactoos-cache.svg?style=flat-square)](https://codecov.io/github/g4s8/cactoos-cache?branch=master)

## Install
Add maven dependency:
```xml
<dependency>
  <groupId>com.google.code.findbugs</groupId>
  <artifactId>annotations</artifactId>
</dependency>
```
latest version on bintray is [![Bintray](https://api.bintray.com/packages/g4s8/mvn/com.g4s8.cactoos-cache/images/download.svg)](https://bintray.com/g4s8/mvn/com.g4s8.cactoos-cache/_latestVersion).

## Usage
To cache a value with `SoftReference` use `SoftScalar`:
```java
final Scalar<Value> scalar = new SoftScalar(() -> value());
assert scalar.value() == scalar.value(); // same references here
```

To build a cache based on `SoftReference`s use `SoftFunc` or `SoftBiFunc`:
```java
final Func<Argument, Value> func = new SoftFunc(arg -> value(arg));
assert func.apply(arg) == func.apply(arg); // same references for one argument
```

// @todo #1:30min Add more usages, describe `WeakFunc` usage, basic soft and weak reference usage
//  and  -XX:SoftRefLRUPolicyMSPerMB JVM option.
