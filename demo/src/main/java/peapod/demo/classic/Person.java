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
 * This project is derived from code in the TinkerPop project under the following license:
 *
 *    TinkerPop3
 *    http://www.apache.org/licenses/LICENSE-2.0
 */

package peapod.demo.classic;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import peapod.FramedVertex;
import peapod.annotations.Edge;
import peapod.annotations.Vertex;

import java.util.List;

@Vertex
public abstract class Person implements FramedVertex<Person> {

    public abstract String getName();

    public abstract Integer getAge();

    @Edge("knows")
    public abstract List<Person> getKnowsPerson();

    public abstract List<Knows> getKnows();

    public abstract Knows getKnows(Person person);

    @Edge("created")
    public abstract List<Software> getCreatedSoftware();

    public abstract List<Created> getCreated();

    public List<Person> getKnowsPersonsOlderThan(int age) {
        return out("knows", Person.class).has("age", P.gt(age)).toList();
    }

}

