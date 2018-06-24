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

## Caches
There are few different cache types and Cactoos primitives with this caches.<br/>
It is [SoftReference](https://docs.oracle.com/javase/7/docs/api/java/lang/ref/SoftReference.html) based caches,
[WeakReference](https://docs.oracle.com/javase/7/docs/api/java/lang/ref/WeakReference.html) key caching,
LRU caches and expired caches.

### SoftReference based caches
From Java documentation:
> Soft reference objects, which are cleared at the discretion of the garbage collector in response to memory demand. Soft references are most often used to implement memory-sensitive caches. 

This kind of caches wraps results in `SoftReference` which can be cleared on demand if JVM will need more memory.
There are `SoftBiFunc`, `SoftFunc`, `SoftScalar` and `SoftText`.For example to cache lazy initialization you can
use `SoftScalar`:
```java
final Scalar<Value> scalar = new SoftScalar(() -> value());
assert scalar.value() == scalar.value(); // same references here
```

To build a cache use `SoftFunc` or `SoftBiFunc` or `SoftFunc`:
```java
final Func<Argument, Value> func = new SoftFunc(arg -> value(arg));
assert func.apply(arg) == func.apply(arg); // same references for one argument
```

### WeakReference caches
Weak reference are used when you want to keep value until you have a string reference for key somewere.
There are only `WeakFunc` implementation, it will keep func result in memory func argument is present:
```java
Argument arg = argument();
final Func<Argument, Value> func = new WeakFunc(arg -> value(arg));
final Value value = func.apply(arg);
assert value == func.apply(arg); // same references for one argument
arg = null;
System.gc(); // now func.apply may return new value if arg was garbage-collected. 
```

### LRU caches
LRU (Least Recently Used) caches keeps only values which are used more than others and clear least used values, there are
`LruFunc` and `LruBiFunc` implementations.

### Expired caches
Expired caches are similar to LRU caches, but they use last access time instead of access count. (not implemented yet)
