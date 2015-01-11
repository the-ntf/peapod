/*
 * Copyright 2015-Bay of Many
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
 * This project is derived from code in the Tinkerpop project under the following licenses:
 *
 * Tinkerpop3
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the TinkerPop nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL TINKERPOP BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package peapod;

import com.tinkerpop.gremlin.process.T;
import com.tinkerpop.gremlin.structure.Element;
import com.tinkerpop.gremlin.structure.Graph;
import com.tinkerpop.gremlin.structure.Graph.Features;
import com.tinkerpop.gremlin.structure.Graph.Variables;
import com.tinkerpop.gremlin.structure.Transaction;
import com.tinkerpop.gremlin.structure.Vertex;
import org.apache.commons.configuration.Configuration;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;

/**
 * A framed instance of a tinkerpop graph.
 * Created by Willem on 26/12/2014.
 */
public class FramedGraph implements AutoCloseable {

    private final Graph graph;

    private final Framer framer = new Framer() {
        @Override
        @SuppressWarnings("unchecked")
        public <S> S frame(Class<S> clazz, Element element) {
            try {
                Class<?> framingClass = clazz.getClassLoader().loadClass(clazz.getName() + "$Impl");
                Constructor<?> constructor = framingClass.getConstructor(Vertex.class, FramedGraph.class);
                return (S) constructor.newInstance(element, FramedGraph.this);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    };

    public FramedGraph(Graph graph) {
        this.graph = graph;
    }

    /**
     * Add a linked vertex of type {@link V} to the graph. The label will be the lowercase value of the class.
     *
     * @param clazz a class implementing {@link FramedVertex} and annotated with {@link com.tinkerpop.gremlin.structure.Vertex}
     * @return The newly created labeled linked vertex
     */
    public <V> V addVertex(Class<V> clazz) {
        Vertex v = graph.addVertex(toLabel(clazz));
        return framer.frame(clazz, v);
    }

    /**
     * Add a linked vertex of type {@link V} and given id to the graph. The label will be the lowercase value of the class.
     *
     * @param clazz a class implementing {@link FramedVertex} and annotated with {@link com.tinkerpop.gremlin.structure.Vertex}
     * @return The newly created labeled linked vertex
     */
    public <V> V addVertex(Class<V> clazz, Object id) {
        Vertex v = graph.addVertex(T.id, id, T.label, toLabel(clazz));
        return framer.frame(clazz, v);
    }

    public <S, V> FramedTraversal<S, V> V(Class<V> clazz) {
        return new FramedTraversal<S, V>(graph, framer).label(clazz);
    }

    /**
     * Get a {@link Vertex} given its unique identifier.
     *
     * @param id The unique identifier of the linked vertex to locate
     * @throws NoSuchElementException if the linked vertex is not found.
     */
    @SuppressWarnings("unchecked")
    public <V> V v(Object id, Class<V> clazz)  throws NoSuchElementException {
        return framer.frame(clazz, graph.v(id));
    }

    private <V> String toLabel(Class<V> clazz) {
        return clazz.getSimpleName().toLowerCase();
    }

    /**
     * @see com.tinkerpop.gremlin.structure.Graph#tx()
     */
    public Transaction tx() {
        return graph.tx();
    }

    /**
     * @see com.tinkerpop.gremlin.structure.Graph#variables()
     */
    public Variables variables() {
        return graph.variables();
    }

    /**
     * @see com.tinkerpop.gremlin.structure.Graph#configuration()
     */
    public Configuration configuration() {
        return graph.configuration();
    }

    /**
     * @see com.tinkerpop.gremlin.structure.Graph#features()
     */
    public Features features() {
        return graph.features();
    }

    /**
     * @return the underlying {@link com.tinkerpop.gremlin.structure.Graph}
     */
    public Graph graph() {
        return graph;
    }

    @Override
    public void close() throws Exception {
        graph.close();
    }
}
