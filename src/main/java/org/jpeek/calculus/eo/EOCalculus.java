package org.jpeek.calculus.eo;

import com.jcabi.xml.*;
import org.cactoos.collection.Joined;
import org.cactoos.collection.Mapped;
import org.eolang.core.EOObject;
import org.eolang.core.data.EODataObject;
import org.jpeek.Header;
import org.jpeek.calculus.Calculus;
import org.jpeek.skeleton.eo.EOSkeleton;
import org.xembly.Directives;
import org.xembly.Xembler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EOCalculus implements Calculus {

    private Directives eo_directives(EOSkeleton eoSkeleton, String metricName){
        final Map<String, Directives> packages = new HashMap<>(0);
        eoSkeleton.getClassFieldsAndMethods().forEach(oeClass ->{
            EOObject metric = new EODataObject(0);
            switch (metricName){
                case "EO_LCOM1": metric = new org.jpeek.calculus.eo.EOlcom1(oeClass);
                    break;
                case "EO_LCOM2": metric = new org.jpeek.calculus.eo.EOlcom2(oeClass);
                    break;
                case "EO_LCOM3": metric = new org.jpeek.calculus.eo.EOlcom3(oeClass);
                    break;
                case "EO_TCC": metric = new org.jpeek.calculus.eo.EOtcc(oeClass);
                    break;
                case "EO_LCC": metric = new org.jpeek.calculus.eo.EOlcc(oeClass);
                    break;
            }

            String className = ((org.jpeek.calculus.eo.EOclass)oeClass).EOname()._getData().toString();
            int i = className.lastIndexOf('.');
            String packageName = className.substring(0,i);
            className = className.substring(i+1);
            packages.putIfAbsent(packageName, new Directives());
            Directives packDirectives = packages.get(packageName);
            packDirectives.add("class")
                    .attr("id", className)
                    .attr("value",  metric._getData().toString())
                    .up();

        });
        return new Directives().append(
                new Joined<>(
                        new Mapped<>(
                                ent -> new Directives()
                                        .add("package")
                                        .attr("id", ent.getKey())
                                        .append(ent.getValue())
                                        .up(),
                                packages.entrySet()
                        )
                )
        );
    }

    @Override
    public XML node(String metric, Map<String, Object> params, XML skeleton) throws IOException {
        final EOSkeleton eoSkeleton = new EOSkeleton(skeleton);
        Directives mDirectives = new Directives();
        mDirectives.append(eo_directives(eoSkeleton, metric));
        final XML res =
                new XMLDocument(
                        new Xembler(
                                new Directives()
                                        .add("metric")
                                        .append(new Header())
                                        .append(
                                                () -> new Directives()
                                                        .attr("schema", "xsd/skeleton.xsd")
                                                        .iterator()
                                        )
                                        .add("title")
                                        .set(metric)
                                        .up()
                                        .add("description")
                                        .set("Metric description")
                                        .up()
                                        .add("app")
                                        .attr("id", skeleton.xpath("//app/@id").get(0))
                                        .append(mDirectives)
                        ).xmlQuietly()
                );
        return res;
    }
}