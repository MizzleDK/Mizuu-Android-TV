/*
 * Copyright (c) 2015 Michell Bak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tv.mizuu.app;

import org.fourthline.cling.support.model.container.Container;

public class ContainerDisplay {

    Container container;

    public ContainerDisplay(Container container) {
        this.container = container;
    }

    public Container getContainer() {
        return container;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContainerDisplay that = (ContainerDisplay) o;
        return container.equals(that.container);
    }

    @Override
    public int hashCode() {
        return container.hashCode();
    }

    @Override
    public String toString() {
        return getContainer().getTitle();
    }

}