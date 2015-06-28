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

package peapod.demo.iclassic;

import peapod.annotations.Edge;
import peapod.annotations.Vertex;

import java.util.List;

@Vertex
public interface Person  {

    public String getName();

    public Integer getAge();

    @Edge("knows")
    public List<Person> getKnowsPerson();

    public List<Knows> getKnows();

    public Knows getKnows(Person person);

    @Edge("created")
    public List<Software> getCreatedSoftware();

    public List<Created> getCreated();

    

}

