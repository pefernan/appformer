/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.documents;

import com.google.gwt.user.client.Window;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Templated
public class DocumentsView implements IsElement {

    @Inject
    @DataField
    private HTMLDivElement documents_div;

    @PostConstruct
    public void init() {
        Documents.get()
                .bind(documents_div)
                .onDrop(this::callback);
    }

    private void callback(String name, String url, int size) {
        Window.alert("name: " + name);
        Window.alert("size: " + size);
        Window.alert("url: " + url);
    }

    private void callback(Document document) {
        if(document != null) {
            Window.alert("name: " + document.getName());
            Window.alert("size: " + document.getSize());
            Window.alert("url: " + document.getUrl());
        }
    }
}
