/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2019 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.jpeek;

import com.jcabi.log.Logger;
import com.jcabi.xml.*;
import org.cactoos.collection.CollectionOf;
import org.cactoos.io.ResourceOf;
import org.cactoos.io.TeeInput;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.cactoos.scalar.And;
import org.cactoos.scalar.AndInThreads;
import org.cactoos.scalar.IoChecked;
import org.cactoos.scalar.LengthOf;
import org.jpeek.calculus.Calculus;
import org.jpeek.calculus.eo.*;
import org.jpeek.skeleton.Skeleton;
import org.xembly.Directives;
import org.xembly.Xembler;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * Application.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle ClassFanOutComplexityCheck (500 lines)
 * @checkstyle ExecutableStatementCountCheck (500 lines)
 * @checkstyle NPathComplexityCheck (500 lines)
 * @checkstyle MagicNumberCheck (500 lines)
 * @checkstyle CyclomaticComplexityCheck (500 lines)
 * @checkstyle MethodLengthCheck (500 lines)
 * @checkstyle JavaNCSSCheck (500 lines)
 */
@SuppressWarnings({
    "PMD.AvoidDuplicateLiterals",
    "PMD.NPathComplexity",
    "PMD.CyclomaticComplexity",
    "PMD.StdCyclomaticComplexity",
    "PMD.ModifiedCyclomaticComplexity",
    "all"
})
public final class App {

    /**
     * Location of the project to analyze.
     */
    private final Path input;

    /**
     * Directory to save reports to.
     */
    private final Path output;

    /**
     * XSL params.
     */
    private final Map<String, Object> params;

    /**
     * Ctor.
     * @param source Source directory
     * @param target Target dir
     */
    public App(final Path source, final Path target) {
        this(
            source, target,
            new MapOf<>(
                new MapEntry<>("EO_LCOM1", true),
                new MapEntry<>("EO_LCOM2", true),
                new MapEntry<>("EO_LCOM3", true),
                new MapEntry<>("EO_LCOM4", true),
                new MapEntry<>("EO_LCOM5", true),
                new MapEntry<>("EO_SCOM", true),
                new MapEntry<>("EO_NHD", true),
                new MapEntry<>("EO_OCC", true),
                new MapEntry<>("EO_PCC", true),
                new MapEntry<>("EO_TCC", true),
                new MapEntry<>("EO_LCC", true),
                new MapEntry<>("EO_CCM", true)
            )
        );
    }

    /**
     * Ctor.
     * @param source Source directory
     * @param target Target dir
     * @param args XSL params
     */
    public App(final Path source, final Path target,
        final Map<String, Object> args) {
        this.input = source;
        this.output = target;
        this.params = args;
    }

    /**
     * Analyze sources.
     * @throws IOException If fails
     * @todo #452:30min Extract report building
     *  Analyze method is too big. We need to extract report building from
     *  here and use a map instead of if statements se we can make
     *  easier to add and remove metrics from execution.
     */
    @SuppressWarnings({
        "PMD.ExcessiveMethodLength",
        "PMD.NcssCount",
        "PMD.GuardLogStatement"
    })
    public void analyze() throws IOException {
        final long start = System.currentTimeMillis();
        final Base base = new DefaultBase(this.input);
        final XML skeleton = new Skeleton(base).xml();
        final Collection<XSL> layers = new LinkedList<>();
        if (this.params.containsKey("include-ctors")) {
            Logger.debug(this, "Constructors will be included");
        } else {
            layers.add(App.xsl("layers/no-ctors.xsl"));
            Logger.debug(this, "Constructors will be ignored");
        }
        if (this.params.containsKey("include-static-methods")) {
            Logger.debug(this, "Static methods will be included");
        } else {
            layers.add(App.xsl("layers/no-static-methods.xsl"));
            Logger.debug(this, "Static methods will be ignored");
        }
        if (this.params.containsKey("include-private-methods")) {
            Logger.debug(this, "Private methods will be included");
        } else {
            layers.add(App.xsl("layers/no-private-methods.xsl"));
            Logger.debug(this, "Private methods will be ignored");
        }
        this.save(skeleton.toString(), "skeleton.xml");
        final XSL chain = new XSLChain(layers);

        final Calculus eoCalc = new EOCalculus();
        final Collection<Report> reports = new LinkedList<>();
        if (this.params.containsKey("EO_LCOM1")) {
            reports.add(
                    new XslReport(
                            chain.transform(skeleton), eoCalc,
                            new ReportData("EO_LCOM1")
                    )
            );
        }
        if (this.params.containsKey("EO_LCOM2")) {
            reports.add(
                    new XslReport(
                            chain.transform(skeleton), eoCalc,
                            new ReportData("EO_LCOM2")
                    )
            );
        }
        if (this.params.containsKey("EO_LCOM3")) {
            reports.add(
                    new XslReport(
                            chain.transform(skeleton), eoCalc,
                            new ReportData("EO_LCOM3")
                    )
            );
        }
        if (this.params.containsKey("EO_LCOM4")) {
            reports.add(
                    new XslReport(
                            chain.transform(skeleton), eoCalc,
                            new ReportData("EO_LCOM4")
                    )
            );
        }
        if (this.params.containsKey("EO_LCOM5")) {
            reports.add(
                    new XslReport(
                            chain.transform(skeleton), eoCalc,
                            new ReportData("EO_LCOM5")
                    )
            );
        }
        if (this.params.containsKey("EO_CCM")) {
            reports.add(
                    new XslReport(
                            chain.transform(skeleton), eoCalc,
                            new ReportData("EO_CCM")
                    )
            );
        }
        if (this.params.containsKey("EO_TCC")) {
            reports.add(
                    new XslReport(
                            chain.transform(skeleton), eoCalc,
                            new ReportData("EO_TCC")
                    )
            );
        }
        if (this.params.containsKey("EO_LCC")) {
            reports.add(
                    new XslReport(
                            chain.transform(skeleton), eoCalc,
                            new ReportData("EO_LCC")
                    )
            );
        }
        if (this.params.containsKey("EO_CAMC")) {
            reports.add(
                    new XslReport(
                            chain.transform(skeleton), eoCalc,
                            new ReportData("EO_CAMC")
                    )
            );
        }
        if (this.params.containsKey("EO_NHD")) {
            reports.add(
                    new XslReport(
                            chain.transform(skeleton), eoCalc,
                            new ReportData("EO_NHD")
                    )
            );
        }
        if (this.params.containsKey("EO_SCOM")) {
            reports.add(
                    new XslReport(
                            chain.transform(skeleton), eoCalc,
                            new ReportData("EO_SCOM")
                    )
            );
        }
        if (this.params.containsKey("EO_OCC")) {
            reports.add(
                    new XslReport(
                            chain.transform(skeleton), eoCalc,
                            new ReportData("EO_OCC")
                    )
            );
        }
        if (this.params.containsKey("EO_PCC")) {
            reports.add(
                    new XslReport(
                            chain.transform(skeleton), eoCalc,
                            new ReportData("EO_PCC")
                    )
            );
        }
        new IoChecked<>(
            new AndInThreads(
                report -> {
                    report.save(this.output);
                },
                reports
            )
        ).value();
        Logger.debug(
            this, "%d XML reports created in %[ms]s",
            reports.size(), System.currentTimeMillis() - start
        );
        final XML index = new StrictXML(
            new XSLChain(
                new CollectionOf<>(
                    App.xsl("index-post-metric-diff.xsl"),
                    App.xsl("index-post-diff-and-defects.xsl")
                )
            ).transform(
                new XMLDocument(
                    new Xembler(new Index(this.output)).xmlQuietly()
                )
            ),
            new XSDDocument(App.class.getResourceAsStream("xsd/index.xsd"))
        );
        this.save(index.toString(), "index.xml");
        this.save(
            App.xsl("badge.xsl").transform(
                new XMLDocument(
                    new Xembler(
                        new Directives().add("badge").set(
                            index.xpath("/index/@score").get(0)
                        ).attr("style", "round")
                    ).xmlQuietly()
                )
            ).toString(),
            "badge.svg"
        );
        this.save(
            App.xsl("index.xsl").transform(index).toString(),
            "index.html"
        );
        final XML matrix = new StrictXML(
            App.xsl("matrix-post.xsl").transform(
                new XMLDocument(
                    new Xembler(new Matrix(this.output)).xmlQuietly()
                )
            ),
            new XSDDocument(App.class.getResourceAsStream("xsd/matrix.xsd"))
        );
        Logger.info(this, "Matrix generated with %d metrics", reports.size());
        this.save(matrix.toString(), "matrix.xml");
        this.save(
            App.xsl("matrix.xsl").transform(matrix).toString(),
            "matrix.html"
        );
        this.copy("jpeek.css");
        new IoChecked<>(
            new And(
                this::copyXsl,
                new ListOf<>("index", "matrix", "metric", "skeleton")
            )
        ).value();
        new IoChecked<>(
            new And(
                this::copyXsd,
                new ListOf<>("index", "matrix", "metric", "skeleton")
            )
        ).value();
    }

    /**
     * Copy resource.
     * @param name The name of resource
     * @throws IOException If fails
     */
    private void copy(final String name) throws IOException {
        new IoChecked<>(
            new LengthOf(
                new TeeInput(
                    new ResourceOf(String.format("org/jpeek/%s", name)),
                    this.output.resolve(name)
                )
            )
        ).value();
    }

    /**
     * Copy XSL.
     * @param name The name of resource
     * @throws IOException If fails
     */
    private void copyXsl(final String name) throws IOException {
        this.copy(String.format("xsl/%s.xsl", name));
    }

    /**
     * Copy XSL.
     * @param name The name of resource
     * @throws IOException If fails
     */
    private void copyXsd(final String name) throws IOException {
        this.copy(String.format("xsd/%s.xsd", name));
    }

    /**
     * Save file.
     * @param data Content
     * @param name The name of destination file
     * @throws IOException If fails
     */
    private void save(final String data, final String name) throws IOException {
        new IoChecked<>(
            new LengthOf(
                new TeeInput(
                    data,
                    this.output.resolve(name)
                )
            )
        ).value();
    }

    /**
     * Make XSL.
     * @param name The name of XSL file
     * @return XSL document
     */
    private static XSL xsl(final String name) {
        return new XSLDocument(
            App.class.getResourceAsStream(String.format("xsl/%s", name))
        ).with(new ClasspathSources());
    }

}
