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
package org.jpeek.graph;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapOf;
import org.hamcrest.collection.IsEmptyCollection;
import org.hamcrest.core.AllOf;
import org.jpeek.FakeBase;
import org.jpeek.skeleton.Skeleton;
import org.junit.jupiter.api.Disabled;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.HasValues;
import org.llorllale.cactoos.matchers.HasValuesMatching;

/**
 * Test case for {@link XmlGraph}.
 * @since 0.30.9
 * @todo #413:30min In #413 we evolved graph connection building to take into account overloaded
 *  method differentiation as stated in #403. Wait until #403 is fully resolved (for now, it still
 *  has the puzzle #437) and then activate back the 2 tests in this class.
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class XmlGraphTest {

    /**
     * First method name.
     */
    private static final String METHOD_ONE = "MethodMethodCalls.methodOne";

    /**
     * Second method name.
     */
    private static final String METHOD_TWO = "MethodMethodCalls.methodTwo";

    /**
     * Third method name.
     */
    private static final String METHOD_THREE = "MethodMethodCalls.methodThree";

    /**
     * Fourth method name.
     */
    private static final String METHOD_FOUR = "MethodMethodCalls.methodFour";

    /**
     * Fifth method name.
     */
    private static final String METHOD_FIVE = "MethodMethodCalls.methodFive";

    /**
     * Class name.
     */
    private static final String CLASS_NAME = "MethodMethodCalls";

    @Disabled
    @SuppressWarnings("unchecked")
    public void buildsMethodsAsNodes() throws IOException {
        final List<Node> nodes = new XmlGraph(
            new Skeleton(new FakeBase(XmlGraphTest.CLASS_NAME)),
            "", XmlGraphTest.CLASS_NAME
        ).nodes();
        new Assertion<>(
            "Must build nodes representing methods",
            nodes,
            new AllOf<Iterable<Node>>(
                new ListOf<>(
                    new HasValuesMatching<>(
                        node -> {
                            return node.name().equals(XmlGraphTest.METHOD_ONE);
                        }
                    ),
                    new HasValuesMatching<>(
                        node -> {
                            return node.name().equals(XmlGraphTest.METHOD_TWO);
                        }
                    ),
                    new HasValuesMatching<>(
                        node -> {
                            return node.name().equals(XmlGraphTest.METHOD_THREE);
                        }
                    ),
                    new HasValuesMatching<>(
                        node -> {
                            return node.name().equals(XmlGraphTest.METHOD_FOUR);
                        }
                    ),
                    new HasValuesMatching<>(
                        node -> {
                            return node.name().equals(XmlGraphTest.METHOD_FIVE);
                        }
                    )
                )
            )
        ).affirm();
    }

    @Disabled
    public void buildsConnections() throws IOException {
        final Map<String, Node> byname = new MapOf<>(
            node -> node.name(),
            node -> node,
            new XmlGraph(
                new Skeleton(new FakeBase(XmlGraphTest.CLASS_NAME)),
                "", XmlGraphTest.CLASS_NAME
            ).nodes()
        );
        final Node one = byname.get(XmlGraphTest.METHOD_ONE);
        final Node two = byname.get(XmlGraphTest.METHOD_TWO);
        final Node three = byname.get(XmlGraphTest.METHOD_THREE);
        final Node four = byname.get(XmlGraphTest.METHOD_FOUR);
        final Node five = byname.get(XmlGraphTest.METHOD_FIVE);
        new Assertion<>(
            "Must build nodes connections when called",
            one.connections(),
            new HasValues<Node>(two)
        ).affirm();
        new Assertion<>(
            "Must build nodes connections when called or calling",
            two.connections(),
            new HasValues<Node>(one, four)
        ).affirm();
        new Assertion<>(
            "Must build nodes connections when neither called nor calling",
            three.connections(),
            new IsEmptyCollection<Node>()
        ).affirm();
        new Assertion<>(
            "Must build nodes connections when calling",
            four.connections(),
            new HasValues<Node>(two)
        ).affirm();
        new Assertion<>(
            "Must build nodes connections when throwing",
            five.connections(),
            new IsEmptyCollection<Node>()
        ).affirm();
    }
}
