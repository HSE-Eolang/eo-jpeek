<img src="https://www.jpeek.org/logo.svg" height="92px"/>


jPeek is a cohesion analizator of Java projects.

## What this repository is for?

This repository proposes cohesion metrics that were written in EO and integrated in the original
original Jpeek project.

## How to use?

Make a fork of the [repository](https://github.com/HSE-Eolang/jpeek).
Download the latest `eo_hse_compiler` version from [here](https://github.com/HSE-Eolang/eo_hse).
Inside `Jpeek` do the command that will generate `jpeek-jar-with-dependencies.jar` file :

```bash
mvn clean package
```
After that it is possible to use Jpeek:
```bash
$ java -jar jpeek-jar-with-dependencies.jar --sources . --target ./jpeek_output --metrics EO_LCOM1,LCOM
```

jPeek will analyze Java classes(.class) in the `--source` directory.

Metrics that are used for calculating: `--metrics EO_LCOM1,LCOM`,the full list of EO_metrics,can be found  [here](https://github.com/HSE-Eolang/jpeek/tree/master/src/eo).

XML reports will be generated in the `./jpeek_output` directory.


## Cohesion Metrics
The following Jpeek metrics are implemented using EO:

- [EO_LCOM1](https://github.com/HSE-Eolang/jpeek/blob/master/src/eo/lcom1.eo)
- [EO_LCOM2](https://github.com/HSE-Eolang/jpeek/blob/master/src/eo/lcom2.eo)
- [EO_LCOM3](https://github.com/HSE-Eolang/jpeek/blob/master/src/eo/lcom3.eo)
- [EO_LCOM4](https://github.com/HSE-Eolang/jpeek/blob/master/src/eo/lcom4.eo)
- [EO_LCOM5](https://github.com/HSE-Eolang/jpeek/blob/master/src/eo/lcom5.eo)
- [EO_OCC](https://github.com/HSE-Eolang/jpeek/blob/master/src/eo/occ.eo)
- [EO_PCC](https://github.com/HSE-Eolang/jpeek/blob/master/src/eo/pcc.eo)
- [EO_LCC](https://github.com/HSE-Eolang/jpeek/blob/master/src/eo/lcc.eo)
- [EO_TCC](https://github.com/HSE-Eolang/jpeek/blob/master/src/eo/tcc.eo)
- [EO_CCM](https://github.com/HSE-Eolang/jpeek/blob/master/src/eo/ccm.eo)
- [EO_CAMC](https://github.com/HSE-Eolang/jpeek/blob/master/src/eo/camc.eo)
- [EO_NHD](https://github.com/HSE-Eolang/jpeek/blob/master/src/eo/nhd.eo)
- [EO_SCOM](https://github.com/HSE-Eolang/jpeek/blob/master/src/eo/scom.eo)

For extra information about formulas and implementing details, check [this](https://github.com/HSE-Eolang/Report-materials/blob/main/JPeek%20Metric%20Implementation%20using%20EO%20.pdf) document.

## How it works?

First, `Skeleton` parses Java bytecode using Javaassit and ASM, in order to produce
`skeleton.xml`. This XML document contains information about each class, which
is necessary for the metrics calculations. For example, this simple Java
class:

```java
class Book {
  private int id;
  int getId() {
    return this.id;
  }
}
```

Will look like this in the `skeleton.xml`:

```xml
<class id='Book'>
  <attributes>
   <attribute public='false' static='false' type='I'>id</attribute>
  </attributes>
  <methods>
    <method abstract='false' ctor='true' desc='()I' name='getId' public='true' static='false'>
      <return>I</return>
      <args/>
    </method>
  </methods>
</class>
```

Then, we have a collection of EO classes, one per each metric. For example,
`EO_lcom1.eo` transforms `skeleton.xml` into `LCOM1.xml`, which may look like this:

```xml
<metric>
  <title>LCOM1</title>
  <app>
    <class id='InstantiatorProvider' value='1'/>
    <class id='InstantationException' value='0'/>
    <class id='AnswersValidator' value='0.0583'/>
    <class id='ClassNode' value='0.25'/>
    [... skipped ...]
  </app>
</metric>
```


## Known Limitations

* The java compiler is known to inline constant variables as per [JLS 13.1](https://docs.oracle.com/javase/specs/jls/se8/html/jls-13.html#jls-13.1). This affects the results calculated by metrics that take into account access to class attributes if these are `final` constants. For instance, all LCOM* and *COM metrics are affected.
* Class `constructors`, `static` and `private` methods are not taken into account by default.

## How to contribute?

Just fork, make changes, run `mvn clean install` and submit
a pull request;

## Contributors of current solution

  - [@wsngit](https://github.com/wsngit) as Ivan Spirin
  - [@nlchar](https://github.com/nlchar) as Eugene Popov
  - [@aventador3000](https://github.com/aventador3000) as Vitaliy Korzun
  - [@jizzel](https://github.com/jizzel) as Joseph A. Attakorah
  - [@hadiSaleh](https://github.com/hadiSaleh) as Hadi M. Saleh
