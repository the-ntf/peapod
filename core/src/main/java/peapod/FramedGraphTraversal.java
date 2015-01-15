/*
 * Copyright 2015 Bay of Many
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * This project is derived from code in the Tinkerpop project under the following license:
 *
 *    Tinkerpop3
 *    http://www.apache.org/licenses/LICENSE-2.0
 */

package peapod;

import com.tinkerpop.gremlin.process.T;
import com.tinkerpop.gremlin.process.Traverser;
import com.tinkerpop.gremlin.process.graph.GraphTraversal;
import com.tinkerpop.gremlin.process.graph.step.map.MapStep;
import com.tinkerpop.gremlin.structure.Vertex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Extension of {@link com.tinkerpop.gremlin.process.Traversal} supporting framed vertices and edges.
 */
@SuppressWarnings("unchecked")
public class FramedGraphTraversal<S, E> {

    private GraphTraversal<S, E> traversal;

    private final Framer framer;

    private Class<E> lastFrameClass;

    private Map<String, Class<E>> stepLabel2FrameClass = new HashMap<>();

    public FramedGraphTraversal(GraphTraversal traversal, FramedGraph framedGraph) {
        this.traversal = traversal;
        this.framer = framedGraph.framer();
    }

    protected FramedGraphTraversal<S, E> label(Class<E> clazz) {
        this.lastFrameClass = clazz;
        traversal.has(T.label, clazz.getSimpleName().toLowerCase());
        return this;
    }

    public FramedGraphTraversal<S, E> has(final String key) {
        traversal.has(key);
        return this;
    }

    public FramedGraphTraversal<S, E> has(final String key, final Object value) {
        traversal.has(key, value);
        return this;
    }

    public FramedGraphTraversal<S, E> has(final T accessor, final Object value) {
        traversal.has(accessor, value);
        return this;
    }

    public FramedGraphTraversal<S, E> has(final String key, final BiPredicate predicate, final Object value) {
        traversal.has(key, predicate, value);
        return this;
    }

    public FramedGraphTraversal<S, E> has(final T accessor, final BiPredicate predicate, final Object value) {
        traversal.has(accessor, predicate, value);
        return this;
    }

    public FramedGraphTraversal<S, E> has(final String label, final String key, final Object value) {
        traversal.has(label, key, value);
        return this;
    }

    public FramedGraphTraversal<S, E> has(final String label, final String key, final BiPredicate predicate, final Object value) {
        traversal.has(label, key, predicate, value);
        return this;
    }

    public FramedGraphTraversal<S, E> hasNot(final String key) {
        traversal.hasNot(key);
        return this;
    }

    public FramedGraphTraversal<S, E> values(final String... propertyKeys) {
        this.lastFrameClass = null;
        traversal.values(propertyKeys);
        return this;
    }

    public FramedGraphTraversal<S, E> filter(final Predicate<Traverser<E>> predicate) {
        traversal.filter(predicate);
        return this;
    }

    public <E2> FramedGraphTraversal<S, E2> in(final String edgeLabel, Class<E2> clazz) {
        traversal.in(edgeLabel);
        this.lastFrameClass = (Class<E>) clazz;
        return (FramedGraphTraversal<S, E2>) this;
    }

    public <E2> FramedGraphTraversal<S, E2> out(final String edgeLabel, Class<E2> clazz) {
        traversal.out(edgeLabel);
        this.lastFrameClass = (Class<E>) clazz;
        return (FramedGraphTraversal<S, E2>) this;
    }

    public FramedGraphTraversal<S, E> as(final String label) {
        stepLabel2FrameClass.put(label, lastFrameClass);
        traversal.as(label);
        return this;
    }

    public FramedGraphTraversal<S, E> back(final String label) {
        lastFrameClass = stepLabel2FrameClass.get(label);
        traversal.back(label);
        return this;
    }

    public List<E> toList() {
        addFrameStep(lastFrameClass);
        return traversal.toList();
    }

    public Set<E> toSet() {
        addFrameStep(lastFrameClass);
        return traversal.toSet();
    }

    public E next() {
        addFrameStep(lastFrameClass);
        return traversal.next();
    }

    private void addFrameStep(Class<E> clazz) {
        if (clazz == null) {
            return;
        }

        MapStep<Vertex, E> mapStep = new MapStep<>(traversal);
        mapStep.setFunction(v -> framer.frame(clazz, v.get()));
        traversal.addStep(mapStep);
    }

}